package heuristics;

import java.util.Comparator;

/**
 * Specifies if the objective function value must be maximized or minimized.
 * This enum class is also utilized to compare two Vector objects, according
 * to the nature of the problem.
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 * @see Vector
 */
public enum FitnessFunction implements Comparator<Vector> {
    MIN {
        @Override
        public int compare(Vector a, Vector b) {
            return Double.compare(a.getValue(), b.getValue());
        }
    }, MAX {
        @Override
        public int compare(Vector a, Vector b) {
            return Double.compare(b.getValue(), a.getValue());
        }
    };
    
    @Override
    public abstract int compare(Vector o1, Vector o2);

}