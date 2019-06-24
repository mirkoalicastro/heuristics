package heuristics.nbh.ts;

import heuristics.Batch;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import heuristics.Vector;
import heuristics.FitnessFunction;

/**
 *
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
public class TabuSearchBatch extends Batch {

    /**
     * Constructs a multi-thread tabu search algorithm composed by the specified
     * number of threads.
     * The neighborhood function, the vector comparator and the stopping
     * criterion objects are shared by all the threads. It is your own
     * responsibility to implement synchronized methods or methods which don't
     * arise race conditions.
     * This multi-threading manager creates the specified number of heuristic
     * objects using a different seed for each of it, i.e., using <tt>seed+i</tt>
     * where <tt>seed</tt> is given and <tt>i</tt> is the index of the heuristic
     * in the internal sorting.
     * @param numThreads the number of threads
     * @param tabuListSize the maximum capacity for the tabù list
     * @param decoder the decoder used by all threads
     * @param fitnessFunction MIN or MAX problem
     * @param feasibleSolutions the starting feasible solutions for threads
     * @param neighborhood the function from (un)feasible solution to its
     * neighbors
     * @param stoppingCriterion the predicate that returns true if the stopping
     * criterion has been met
     * @param seed the seed parameter to be used for random calls
     */
    public TabuSearchBatch (int numThreads, int tabuListSize, Function<? super Vector, Double> decoder, FitnessFunction fitnessFunction, Vector[] feasibleSolutions, Function<Vector, List<Vector>> neighborhood, Predicate<? super TabuSearch> stoppingCriterion, long seed) {
        if(numThreads < 1)
            throw new IllegalArgumentException("There must be at least one thread");
        if(numThreads != feasibleSolutions.length)
            throw new IllegalArgumentException("There number of threads and the array length of feasible solutions must be the same");
        TabuSearch[] tabuSearches = new TabuSearch[numThreads];
        for(int i=0; i<tabuSearches.length; i++) {
            Random random = new Random();
            random.setSeed(seed+i);
            tabuSearches[i] = new TabuSearch(tabuListSize, decoder, fitnessFunction, feasibleSolutions[i], neighborhood, stoppingCriterion, random);
        }
        super.setFitnessFunction(fitnessFunction);
        super.setHeuristics(tabuSearches);
    }    
}
