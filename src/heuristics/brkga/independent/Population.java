package heuristics.brkga.independent;

import heuristics.Vector;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Stream;
import heuristics.brkga.client.DnaGenerator;

/**
 * A population made up by individuals
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
public class Population implements Iterable<Individual> {
    private final Individual[] individuals;
    private final Comparator<? super Individual> comparator;
    Population(Consumer<? super Individual> consumer, Comparator<? super Individual> comparator, int p, int n) {
        individuals = new Individual[p];
        for(int i=0; i<individuals.length; i++) {
            individuals[i] = new Individual(n);
            consumer.accept(individuals[i]);
        }
        this.comparator = comparator;
    }
    Individual get(int i) {
        return individuals[i];
    }
    void set(int i, Individual individual) {
        individuals[i] = individual;
    }
    void applyToAll(Consumer<? super Individual> c, boolean parallel) {
        Stream<Individual> stream = Arrays.stream(individuals);
        if(parallel)
            stream = stream.parallel();
        stream.forEach(c);
    }
    void parallelSort() {
        Arrays.parallelSort(individuals, comparator);
    }
    void sort() {
        Arrays.sort(individuals, comparator);
    }

    /**
     * Gets an iterator over the individuals which are contained into this
     * population.
     * This method is not thread-safe.
     * @return iterator over individuals
     */
    @Override
    public Iterator<Individual> iterator() {
        return new Iterator<Individual>() {
            int i = 0;
            @Override
            public boolean hasNext() {
                return i < individuals.length;
            }

            @Override
            public Individual next() {
                if(!hasNext())
                    throw new NoSuchElementException();
                return individuals[i++];
            }
        };
    }
}
