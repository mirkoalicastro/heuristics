package heuristics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An abstraction representation of a heuristic
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
public abstract class Heuristic {
    private int iterations, iterationsWithNoIncrement;
    private final Map<Double, Long> history;
    private final long initTime;
    private Vector bestVector;

    /**
     * Updates the incumbent vector.
     * Sets the best vector with its objective function value, reset the counter
     * of iterations with no increment and logs that objective function value
     * as reached at the current time, in milliseconds.
     * @param bestVector the incumbent vector
     */
    public void updateBest(Vector bestVector) {
        double bestValue = bestVector.getValue();
        history.put(bestValue, System.currentTimeMillis()-initTime);
        this.bestVector = bestVector;
        iterationsWithNoIncrement = 0;
    }
    
    /**
     * Returns a copy of the incumbent vector.
     * Each call to this method costs a copy of the incumbent vector, i.e., it
     * is linear in the vector length.
     * @return a copy of the incumbent vector
     */
    public final Vector getBestVector() {
        return new Vector(bestVector);
    }
    
    /**
     * Computes an iteration of the heuristic.
     * It must return true if the stopping criterion allows the computation,
     * false otherwise.
     * @return true if the iteration was performed, false otherwise.
     */
    public abstract boolean iterate();

    public Heuristic() {
        history = new HashMap<>();
        initTime = System.currentTimeMillis();
    }
    
    /**
     * Computes an undefined number of iterations.
     * This method breaks when the current thread is interrupted or the
     * <i>iterate</i> method returns false.
     * @see Heuristic#iterate() 
     */
    public void iterateUntilStoppingCriterion() {
        while(!Thread.currentThread().isInterrupted() && iterate()) {}
    }
    
    /**
     * Increases the number of iterations and of iterations with no increments
     * of 1.
     */
    public void increaseIterations() {
        iterations++;
        iterationsWithNoIncrement++;
    }
    /**
     * Returns the number of <i>increaseIterations</i> calls.
     * @return the number of performed iterations
     * @see Heuristic#increaseIterations() 
     */
    public int getIterations() {
        return iterations;
    }
    /**
     * Returns the number of <i>increaseIterations</i> calls since last
     * <i>updateBest</i> call.
     * @return the number of iterations with no increment since last incumbent
     * vector update
     * @see Heuristic#increaseIterations() 
     * @see Heuristic#updateBest(heuristics.Vector, double) 
     */
    public int getIterationsWithNoIncrement() {
        return iterationsWithNoIncrement;
    }
    /**
     * Returns the number of <i>increaseIterations</i> calls utilized to reach
     * the incumbent vector.
     * @return the number of iterations utilized by the heuristic to reach the
     * incumbent vector
     */
    public int getIterationsForBestVector() {
        return iterations - iterationsWithNoIncrement;
    }
    /**
     * Returns an unmodifiable map which represents the heuristic history.
     * The map goes from <i>double</i>, i.e., the objective function value, to
     * <i>long</i>, i.e., the milliseconds since this Heuristic object creation
     * to that objective function value reached.
     * @return an unmodifiable copy of the heuristic history
     * @see Collections#unmodifiableMap(java.util.Map) 
     */
    public Map<Double, Long> getHistory() {
        return Collections.unmodifiableMap(history);
    }
}
