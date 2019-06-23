package heuristics.brkga.independent;

import heuristics.Vector;
import heuristics.brkga.client.DNAGenerator;

/**
 * An individual of a population
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
public class Individual extends Vector {

    Individual(Vector other) {
        super(other);
    }
    Individual(double[] array) {
        super(array);
    }
    Individual(int length) {
        super(length);
    }
    Individual(DNAGenerator generator, int length) {
        super(length);
        random(generator);
    }
    final void random(DNAGenerator generator) {
        generator.random(super.array);
    }

}
