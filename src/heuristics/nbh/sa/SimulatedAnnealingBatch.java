package heuristics.nbh.sa;

import heuristics.Batch;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import heuristics.FitnessFunction;
import heuristics.Vector;

/**
 *
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
public class SimulatedAnnealingBatch extends Batch {

    /**
     * Constructs a multi-thread simulated annealing algorithm composed by the
     * specified number of simulated annealing.
     * The neighborhood function, the vector comparator and the stopping
     * criterion objects are shared by all the threads. It is your own
     * responsibility to implement synchronized methods or methods which don't
     * arise race conditions.
     * The initial temperature and the delta temperature are those specified by
     * the default values.
     * This multi-threading manager creates the specified number of heuristic
     * objects using a different seed for each of it, i.e., using <tt>seed+i</tt>
     * where <tt>seed</tt> is given and <tt>i</tt> is the index of the heuristic
     * in the internal sorting.
     * @param numThreads the number of independent simulated annealing
     * @param t0 the initial temperature
     * @param tDelta the decreasing amount for the temperature at each step
     * @param decoder the decoder used by all simulated annealing
     * @param fitnessFunction MIN or MAX problem
     * @param feasibleSolutions the starting feasible solutions for each
     * simulated annealing
     * @param randomFeasibleNeighbor the function from (un)feasible solution to its
     * neighbors
     * @param stoppingCriterion the predicate that returns true if the stopping
     * criterion has been met
     */
    public SimulatedAnnealingBatch (int numThreads, float t0, float tDelta, Function<? super Vector, Double> decoder, FitnessFunction fitnessFunction, Vector[] feasibleSolutions, Function<Vector, Vector> randomFeasibleNeighbor, Predicate<? super SimulatedAnnealing> stoppingCriterion, long seed) {
        if(numThreads < 1)
            throw new IllegalArgumentException("There must be at least one thread");
        if(numThreads != feasibleSolutions.length)
            throw new IllegalArgumentException("There number of threads and the array length of feasible solutions must be the same");
        SimulatedAnnealing[] simulatedAnnealings = new SimulatedAnnealing[numThreads];
        for(int i=0; i<simulatedAnnealings.length; i++) {
            Random random = new Random();
            random.setSeed(seed+i);
            simulatedAnnealings[i] = new SimulatedAnnealing(t0, tDelta, decoder, fitnessFunction, feasibleSolutions[i], randomFeasibleNeighbor, stoppingCriterion, random);
        }
        super.setFitnessFunction(fitnessFunction);
        super.setHeuristics(simulatedAnnealings);
    }
}
