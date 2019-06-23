package heuristics.brkga.client;

/**
 *
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
@FunctionalInterface
public interface DNAGenerator {
    /**
     * Modifies the given double array turning it into another random double
     * array that represents a feasible individual.
     * @param chromosome to modify
     * @see heuristics.brkga.independent.Individual
     */
    void random(double[] chromosome);
}
