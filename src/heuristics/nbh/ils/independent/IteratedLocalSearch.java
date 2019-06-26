package heuristics.nbh.ils.independent;

import heuristics.Heuristic;
import heuristics.Vector;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
public class IteratedLocalSearch extends Heuristic {
    private Vector curVector;
    private final Function<? super Vector, Double> decoder;
    private final Comparator<? super Vector> fitnessFunction;
    private final Predicate<? super IteratedLocalSearch> stoppingCriterion;
    private final Function<Vector, Vector> localSearch, perturbation;
    
    /**
     * Constructs an IteratedLocalSearch object.
     * @param localSearch the local search procedure
     * @param perturbation the perturbation procedure
     * @param decoder the decoder used to evaluate
     * @param fitnessFunction MIN or MAX problem
     * @param feasibleSolution the starting feasible solution
     * @param stoppingCriterion the predicate that returns true if the stopping
     * criterion has been met
     */
    IteratedLocalSearch(Function<Vector, Vector> localSearch, Function<Vector, Vector> perturbation, Function<? super Vector, Double> decoder, Comparator<? super Vector> fitnessFunction, Vector feasibleSolution, Predicate<? super IteratedLocalSearch> stoppingCriterion) {
        this.decoder = decoder;
        this.localSearch = localSearch;
        this.perturbation = perturbation;
        this.fitnessFunction = fitnessFunction;
        this.stoppingCriterion = stoppingCriterion;
        curVector = new Vector(feasibleSolution);
        curVector.setValue(decoder.apply(curVector));
        super.updateBest(new Vector(curVector));
    }

    /**
     * Processes a single iteration of the iterated local search.
     * @return true if the stopping criterion hadn't been met, false otherwise
     */
    @Override
    public boolean iterate() {
        if(stoppingCriterion.test(this))
            return false;
        super.increaseIterations();
        Vector bestLocal = localSearch.apply(curVector);
        bestLocal.setValue(decoder.apply(bestLocal));
        if(fitnessFunction.compare(bestLocal, super.getBestVector()) < 0) {
            super.updateBest(new Vector(bestLocal));
        }
        curVector = perturbation.apply(bestLocal);
        curVector.setValue(decoder.apply(curVector));
        return true;
    }
    
}
