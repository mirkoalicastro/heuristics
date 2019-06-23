package heuristics.nbh.ils.client;

import heuristics.Vector;
import java.util.List;
import java.util.function.Function;
import heuristics.Evaluator;
import heuristics.FitnessFunction;

/**
 * An implementation of a first improvement local search procedure.
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
public final class FirstIterImpr implements Function<Vector, Vector> {

    private final int maxIterations;
    private final Function<Vector, List<Vector>> neighborhood;
    private final Evaluator decoder;
    private final FitnessFunction fitnessFunction;

    /**
     * Constructs a local search procedure.
     * @param maxIterations the maximum number of iterations to perform to reach
     * a local optimum
     * @param neighborhood the neighborhood function
     * @param decoder the decoder used to evaluate vectors
     * @param fitnessFunction the fitness function type
     */
    public FirstIterImpr(int maxIterations, Function<Vector, List<Vector>> neighborhood, Evaluator decoder, FitnessFunction fitnessFunction) {
        if(maxIterations < 1)
            throw new IllegalArgumentException("The maximum number of iterations must be greater than 0");
        this.maxIterations = maxIterations;
        this.neighborhood = neighborhood;
        this.decoder = decoder;
        this.fitnessFunction = fitnessFunction;
    }
    
    /**
     * Explores the neighborhood of a given vector and moves on the first found
     * vector among the neighbors with a value that is better than the current
     * vector value.
     * If no neighbor is better than the current vector than this method returns
     * the found local optimum. It repeats this procedure for a maximum number
     * of iterations, specified during the construction of this object.
     * @param t the starting point of the local search procedure
     * @return a local optimum, if founded within a maximum number of iterations,
     * the best found vector otherwise.
     * @see #FirstIterImpr(int, java.util.function.Function, heuristics.Evaluator, heuristics.FitnessFunction) 
     */
    @Override
    public Vector apply(Vector t) {
        boolean improve = true;
        Vector cur = t;
        cur.setValue(decoder.eval(cur));
        int iterations = 0;
        while(improve && iterations < maxIterations) {
            List<Vector> neighbors = neighborhood.apply(cur);
            improve = false;
            for(Vector neighbor: neighbors) {
                neighbor.setValue(decoder.eval(neighbor));
                if(fitnessFunction.compare(neighbor, cur) < 0) {
                    cur = neighbor;
                    improve = true;
                    break;
                }
            }
            iterations++;
        }
        return cur;
    }
}
