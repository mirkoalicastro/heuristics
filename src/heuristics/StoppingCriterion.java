package heuristics;

import java.util.Comparator;
import java.util.function.Predicate;

/**
 * A predicate for a generic heuristic
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
public class StoppingCriterion implements Predicate<Heuristic> {
    private long startedAt;
    private final Comparator<? super Vector> comparator;
    private final int maxIterationsWithNoIncrement, maxIterations, timeLimit;
    private final Vector stoppingVector;

    /**
     * Creates a heuristic predicate.
     * This predicate will check that all the following conditions were not met:
     * <ul>
     * <li>The number of iterations are less or equal to a maximum number
     * of iterations, i.e., <i>maxIterations</i></li>
     * <li>The number of iterations with no increment are less or equal to a
     * maximum number of iterations, i.e., <i>maxIterationsWithNoIncrement</i>
     * </li>
     * <li>The heuristic is not working for more of a specified time in
     * milliseconds, i.e., <i>timeLimit</i><sup>1</sup></li>
     * <li>The heuristic has not already reached an incumbent vector whose
     * objective function value is better or equal to a specified value, i.e.,
     * <i>stoppingOFV</i></li>
     * </ul>
     * If one of the above conditions were met, the <i>test</i> method returns
     * false.<br>
     * Note [1]: the time was calculated since the difference between the
     * <i>test</i> call time and the maximum between this object creationt time
     * and the last <i>resetTime</i> call time.
     * @param maxIterations the maximum number of iterations
     * @param maxIterationsWithNoIncrement the maximum number of iterations with
     * no increment
     * @param timeLimit the maximum time in milliseconds
     * @param comparator the fitness function type
     * @param stoppingOFV the target objective function value
     * @see #test(heuristics.Heuristic) 
     * @see #resetTime() 
     */
    public StoppingCriterion(int maxIterations, int maxIterationsWithNoIncrement, int timeLimit, Comparator<? super Vector> comparator, double stoppingOFV) {
        this.maxIterations = maxIterations;
        this.maxIterationsWithNoIncrement = maxIterationsWithNoIncrement;
        this.timeLimit = timeLimit*1000;
        this.comparator = comparator;
        Vector vector = new Vector((double[])null);
        vector.setValue(stoppingOFV);
        this.stoppingVector = vector;
        resetTime();
    }

    /**
     * Sets to now the starting time of the heuristic.
     * @see #StoppingCriterion(int, int, int, heuristics.FitnessFunction, double) 
     */
    public final void resetTime() {
        startedAt = System.currentTimeMillis();
    }
    
    /**
     * Checks if the heuristic must interrupt or can still work.
     * @param heuristic the heuristic to check
     * @return true if the stopping criterion was met, false otherwise.
     * @see #StoppingCriterion(int, int, int, heuristics.FitnessFunction, double) 
     */
    @Override
    public boolean test(Heuristic heuristic) {
        boolean a = heuristic.getIterations() > maxIterations;
        boolean b = heuristic.getIterationsWithNoIncrement() > maxIterationsWithNoIncrement;
        boolean c = isTimedOut();
        boolean d = comparator.compare(heuristic.getBestVector(), stoppingVector) <= 0;
        return a || b || c || d;
    }
    
    /**
     * Returns true if the heuristic has timed out, false otherwise.
     * @return true if the heuristic has timed out
     */
    public boolean isTimedOut() {
        return System.currentTimeMillis()-startedAt > timeLimit;
    }
}
