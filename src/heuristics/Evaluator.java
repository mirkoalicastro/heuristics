package heuristics;

/**
 * Defines the evaluation process of a Vector.
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 * @see Vector
 */
@FunctionalInterface
public interface Evaluator {
    
    /**
     * Calculates and returns the objective function value of a vector.
     * @param vector the vector whose objective function value has to be
     * calculated
     * @return the objective function value of the vector
     */
    double eval(Vector vector);
}