package heuristics.brkga.independent;

import heuristics.Vector;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A population made up by individuals
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
public class Population implements Iterable<Vector> {
    private final Vector[] individuals;
    Population(int p, int n) {
        individuals = new Vector[p];
        for(int i=0; i<individuals.length; i++)
            individuals[i] = new Vector(n);
    }
    Vector get(int i) {
        return individuals[i];
    }
    void set(int i, Vector individual) {
        individuals[i] = individual;
    }
    void applyToAll(Consumer<? super Vector> c, boolean parallel) {
        Stream<Vector> stream = Arrays.stream(individuals);
        if(parallel)
            stream = stream.parallel();
        stream.forEach(c);
    }
    void parallelSort(Comparator<? super Vector> comparator) {
        Arrays.parallelSort(individuals, comparator);
    }
    void sort(Comparator<? super Vector> comparator) {
        Arrays.sort(individuals, comparator);
    }

    /**
     * Gets an iterator over the individuals which are contained into this
     * population.
     * This method is not thread-safe.
     * @return iterator over individuals
     */
    @Override
    public Iterator<Vector> iterator() {
        return new Iterator<Vector>() {
            int i = 0;
            @Override
            public boolean hasNext() {
                return i < individuals.length;
            }

            @Override
            public Vector next() {
                if(!hasNext())
                    throw new NoSuchElementException();
                return individuals[i++];
            }
        };
    }
}
