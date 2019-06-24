package heuristics.nbh.ils.independent;

import heuristics.Batch;
import heuristics.Vector;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
public class IteratedLocalSearchBatch extends Batch {

    /**
     * Constructs a multi-thread iterated local search algorithm composed by
     * the specified number of threads.
     * The neighborhood function, the vector comparator and the stopping
     * criterion objects are shared by all the threads. It is your own
     * responsibility to implement synchronized methods or methods which don't
     * arise race conditions.
     * This multi-threading manager creates the specified number of heuristic
     * objects using a different seed for each of it, i.e., using <tt>seed+i</tt>
     * where <tt>seed</tt> is given and <tt>i</tt> is the index of the heuristic
     * in the internal sorting.
     * @param numThreads the number of independent iterated local search
     * @param localSearch the local search procedure
     * @param perturbation the perturbation procedure
     * @param decoder the decoder used to evaluate
     * @param fitnessFunction MIN or MAX problem
     * @param feasibleSolutions the starting feasible solutions
     * @param stoppingCriterion the predicate that returns true if the stopping
     * criterion has been met
     */
    public IteratedLocalSearchBatch (int numThreads, Function<Vector, Vector> localSearch, Function<Vector, Vector> perturbation, Function<? super Vector, Double> decoder, Comparator<? super Vector> fitnessFunction, Vector[] feasibleSolutions, Predicate<? super IteratedLocalSearch> stoppingCriterion) {
        if(numThreads < 1)
            throw new IllegalArgumentException("There must be at least one thread");
        if(numThreads != feasibleSolutions.length)
            throw new IllegalArgumentException("The number of threads and the array length of feasible solutions must be the same");
        IteratedLocalSearch[] iteratedLocalSearches = new IteratedLocalSearch[numThreads];
        for(int i=0; i<iteratedLocalSearches.length; i++)
            iteratedLocalSearches[i] = new IteratedLocalSearch(localSearch, perturbation, decoder, fitnessFunction, feasibleSolutions[i], stoppingCriterion);
        super.setFitnessFunction(fitnessFunction);
        super.setHeuristics(iteratedLocalSearches);
    }    
}
