package heuristics.brkga.independent;

import heuristics.Batch;
import heuristics.Heuristic;
import heuristics.Vector;
import heuristics.brkga.client.Configuration;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Consumer;

/**
 *
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
public class BiasedRandomKeyGeneticAlgorithmBatch extends Batch {

    /**
     * Constructs a multi-thread genetic algorithm composed by the specified
     * number of independent populations.
     * The CrossingOver object, DNAGenerator object and Decoder object are
     * shared by all the threads. It is your own responsibility to implement
     * synchronized methods or methods which don't arise race conditions.
     * This multi-threading manager creates the specified number of heuristic
     * objects using a different seed for each of it, i.e., using <tt>seed+i</tt>
     * where <tt>seed</tt> is given and <tt>i</tt> is the index of the heuristic
     * in the internal sorting.
     * @param fitnessFunction the fitness function type
     * @param config the Config object
     * @param heredityRule the CrossingOver used by all populations
     * @param individualGenerator the SequenceGenerator used by all populations
     * @param decoder the Decoder used by all populations
     * @param stoppingCriterion the predicate that returns true if the stopping
     * criterion has been met
     * @param seed the seed to utilize for random calls.
     */
    public BiasedRandomKeyGeneticAlgorithmBatch(Comparator<? super Vector> fitnessFunction, Configuration config, BiFunction<? super Vector, ? super Vector, Vector> heredityRule, Consumer<? super Vector> individualGenerator, Function<? super Vector, Double> decoder, Predicate<Heuristic> stoppingCriterion, long seed) {
        if(config.ip < 1)
            throw new IllegalArgumentException("At least 1 thread");
        BiasedRandomKeyGeneticAlgorithm[] geneticAlgorithms = new BiasedRandomKeyGeneticAlgorithm[config.ip];
        for(int i=0; i<config.ip; i++) {
            Random random = new Random();
            random.setSeed(seed+i);
            geneticAlgorithms[i] = new BiasedRandomKeyGeneticAlgorithm(fitnessFunction, config, heredityRule, individualGenerator, decoder, stoppingCriterion, random);
        }
        super.setVectorComparator(fitnessFunction);
        super.setHeuristics(geneticAlgorithms);
    }
}
