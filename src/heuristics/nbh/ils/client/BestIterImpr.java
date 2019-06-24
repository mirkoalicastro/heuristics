package heuristics.nbh.ils.client;

import heuristics.Vector;
import java.util.List;
import java.util.function.Function;
import heuristics.FitnessFunction;

/**
 * An implementation of a best improvement local search procedure.
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
public final class BestIterImpr implements Function<Vector, Vector> {

    private final int maxIterations;
    private final Function<Vector, List<Vector>> neighborhood;
    private final Function<? super Vector, Double> decoder;
    private final FitnessFunction fitnessFunction;

    /**
     * Constructs a local search procedure.
     * @param maxIterations the maximum number of iterations to perform to reach
     * a local optimum
     * @param neighborhood the neighborhood function
     * @param decoder the decoder used to evaluate vectors
     * @param fitnessFunction the fitness function type
     */
    public BestIterImpr(int maxIterations, Function<Vector, List<Vector>> neighborhood, Function<? super Vector, Double> decoder, FitnessFunction fitnessFunction) {
        if(maxIterations < 1)
            throw new IllegalArgumentException("The maximum number of iterations must be greater than 0");
        this.maxIterations = maxIterations;
        this.neighborhood = neighborhood;
        this.decoder = decoder;
        this.fitnessFunction = fitnessFunction;
    }
    
    /**
     * Explores the neighborhood of a given vector and moves on the best vector
     * among the neighbors with a value that is better than the current vector
     * value.
     * If no neighbor is better than the current vector than this method returns
     * the found local optimum. It repeats this procedure for a maximum number
     * of iterations, specified during the construction of this object.
     * @param t the starting point of the local search procedure
     * @return a local optimum, if founded within a maximum number of iterations,
     * the best found vector otherwise.
     * @see #BestIterImpr(int, java.util.function.Function, heuristics.Evaluator, heuristics.FitnessFunction) 
     */
    @Override
    public Vector apply(Vector t) {
        boolean improve = true;
        Vector cur = t;
        cur.setValue(decoder.apply(cur));
        int iterations = 0;
        while(improve && iterations < maxIterations) {
            List<Vector> neighbors = neighborhood.apply(cur);
            neighbors.parallelStream().forEach(neighbor -> neighbor.setValue(decoder.apply(neighbor)));
            if(neighbors.isEmpty())
                return cur;
            neighbors.sort(fitnessFunction);
            Vector bestNeighbor = neighbors.get(0);
            if(fitnessFunction.compare(bestNeighbor, cur) < 0)
                cur = bestNeighbor;
            else
                improve = false;
            iterations++;
        }
        return cur;
    }
}
