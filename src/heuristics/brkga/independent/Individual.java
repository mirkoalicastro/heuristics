package heuristics.brkga.independent;

import heuristics.Vector;
import java.util.function.Consumer;

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
    Individual(Consumer<Vector> generator, int length) {
        super(length);
        random(generator);
    }
    final void random(Consumer<Vector> generator) {
        generator.accept(this);
    }

}
