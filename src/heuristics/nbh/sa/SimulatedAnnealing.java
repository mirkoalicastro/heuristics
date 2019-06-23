package heuristics.nbh.sa;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import heuristics.Vector;
import heuristics.Evaluator;
import heuristics.FitnessFunction;
import heuristics.Heuristic;

/**
 *
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
public class SimulatedAnnealing extends Heuristic {
    private Vector curVector;
    private final Evaluator decoder;
    private final FitnessFunction fitnessFunction;
    private final Predicate<? super SimulatedAnnealing> stoppingCriterion;
    private final Function<Vector, Vector> randomFeasibleNeighbor;
    private final Random rand;
    
    private float temperature;
    private final float t0, tDelta;    

    SimulatedAnnealing(float t0, float tDelta, Evaluator decoder, FitnessFunction fitnessFunction, Vector feasibleSolution, Function<Vector, Vector> randomFeasibleNeighbor, Predicate<? super SimulatedAnnealing> stoppingCriterion, Random random) {
        this.t0 = t0;
        this.tDelta = tDelta;
        this.decoder = decoder;
        this.fitnessFunction = fitnessFunction;
        this.randomFeasibleNeighbor = randomFeasibleNeighbor;
        this.stoppingCriterion = stoppingCriterion;
        Vector bestVector = new Vector(feasibleSolution);
        bestVector.setValue(decoder.eval(bestVector));
        curVector = bestVector;
        temperature = t0;
        if(random == null) {
            random = new Random();
            random.setSeed(System.nanoTime());
        }
        this.rand = random;
        super.updateBest(bestVector);
    }
    
    /**
     * Processes a single iteration of the simulated annealing.
     * @return true if the stopping criterion hadn't been met, false otherwise
     */
    @Override
    public boolean iterate() {
        if(stoppingCriterion.test(this))
            return false;
        super.increaseIterations();
        Vector nextVector = randomFeasibleNeighbor.apply(curVector);
        if(nextVector == null)
            return false;
        nextVector.setValue(decoder.eval(nextVector));
        if(fitnessFunction.compare(nextVector, super.getBestVector()) < 0) {
            curVector = nextVector;
            super.updateBest(curVector);
        } else {
            double probability;
            if(fitnessFunction.compare(nextVector, curVector) < 0)
                probability = 1;
            else
                probability = Math.pow(Math.E, -Math.abs(nextVector.getValue()-curVector.getValue())/temperature);
            if(rand.nextDouble() < probability)
                curVector = nextVector;
        }
        temperature = decreaseTemperature(temperature);
        return true;
    }

    private float decreaseTemperature(float t) {
        float tmp = t - tDelta;
        if(tmp < 0)
            tmp = t0;
        return tmp;
    }

}
