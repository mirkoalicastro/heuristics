package heuristics.nbh.ts;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import heuristics.Vector;
import heuristics.Evaluator;
import heuristics.FitnessFunction;
import heuristics.Heuristic;
import java.util.Random;

/**
 *
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
public class TabuSearch extends Heuristic {
    public static final int DEFAULT_TABU_LIST_SIZE = 150;

    private final Function<Vector, List<Vector>> neighborhood;
    private final Predicate<? super TabuSearch> stoppingCriterion;
    private final LimitedList<Vector> tabuList;
    private Vector curVector;
    
    private final Evaluator decoder;
    private final FitnessFunction fitnessFunction;
    
    private final Random random;
    /**
     * Constructs a TabuSearch object.
     * @param tabuListSize the tabu list size
     * @param decoder the decoder used to evaluate
     * @param fitnessFunction MIN or MAX problem
     * @param feasibleSolution the starting feasible solution
     * @param neighborhood the function from (un)feasible solution to its
     * neighbors
     * @param stoppingCriterion the predicate that returns true if the stopping
     * criterion has been met
     * @throws IllegalArgumentException if the tabu list size is not greater
     * than 0
     */
    TabuSearch(int tabuListSize, Evaluator decoder, FitnessFunction fitnessFunction, Vector feasibleSolution, Function<Vector, List<Vector>> neighborhood, Predicate<? super TabuSearch> stoppingCriterion, Random random) throws IllegalArgumentException {
        this.decoder = decoder;
        this.fitnessFunction = fitnessFunction;
        this.neighborhood = neighborhood;
        this.stoppingCriterion = stoppingCriterion;
        tabuList = new LimitedList<>(tabuListSize);
        curVector = new Vector(feasibleSolution);
        curVector.setValue(decoder.eval(curVector));
        if(random == null) {
            random = new Random();
            random.setSeed(System.nanoTime());
        }
        this.random = random;
        super.updateBest(curVector);
    }

    /**
     * Processes a single iteration of the tabu search.
     * @return true if the stopping criterion hadn't been met, false otherwise
     */
    @Override
    public boolean iterate() {
        if(stoppingCriterion.test(this))
            return false;
        super.increaseIterations();
        List<Vector> neighbors = neighborhood.apply(curVector);
        neighbors.removeAll(tabuList);
        if(neighbors.isEmpty()) {
            if(!tabuList.isEmpty()) {
                tabuList.clear();
                return true;
            } else {
                return false;
            }
        }
        evaluateAndThenSortNeighbors(neighbors);
        int bestFirst = 0;
        while(bestFirst+1 < neighbors.size() && fitnessFunction.compare(neighbors.get(bestFirst), neighbors.get(bestFirst+1)) == 0)
            bestFirst++;
        int randBest = random.nextInt(bestFirst+1);
        curVector = neighbors.get(randBest);
        tabuList.add(curVector);
        if(fitnessFunction.compare(curVector, super.getBestVector()) < 0)
            super.updateBest(curVector);
        return true;
    }
    
    private void evaluateAndThenSortNeighbors(List<Vector> neighbors) {
        neighbors.parallelStream().forEach(neighbor -> neighbor.setValue(decoder.eval(neighbor)));
        neighbors.sort(fitnessFunction);
    }
    
    private static class LimitedList<T> extends LinkedList<T> {
        int capacity;
        
        LimitedList(int capacity) throws IllegalArgumentException {
            if(capacity < 1)
                throw new IllegalArgumentException("Limited list capacity must be greater than 0");
            this.capacity = capacity;
        }
        
        @Override
        public boolean add(T el) {
            super.add(el);
            while(size() > capacity)
                super.remove();
            return true;
        }
    }
        
}
