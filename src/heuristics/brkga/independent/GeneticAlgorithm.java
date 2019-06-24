package heuristics.brkga.independent;

import heuristics.Heuristic;
import heuristics.Vector;
import heuristics.brkga.client.Configuration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
public class GeneticAlgorithm extends Heuristic {
    private final Configuration configuration;
    private final Consumer<? super Individual> individualGenerator;
    private final Population population;
    private final List<Integer> mutantsRemainingIndex;
    private final Function<? super Individual, Double> decoder;
    private final Predicate<Heuristic> stoppingCriterion;
    private final BiFunction<? super Individual, ? super Individual, Individual> crossingOver;
    private final List<Integer> mutantsSelectedIndex;
        
    private final Random rand;
    private final Comparator<? super Vector> fitnessFunction;
    
    GeneticAlgorithm(Comparator<? super Vector> fitnessFunction, Configuration configuration, BiFunction<? super Individual, ? super Individual, Individual> crossingOver, Consumer<? super Individual> individualGenerator, Function<? super Individual, Double> decoder, Predicate<Heuristic> stoppingCriterion, Random random) {
        this.stoppingCriterion = stoppingCriterion;
        this.fitnessFunction = fitnessFunction;
        this.configuration = configuration;
        this.crossingOver = crossingOver;
        this.individualGenerator = individualGenerator;
        this.decoder = decoder;
        population = new Population(individualGenerator, fitnessFunction, configuration.p, configuration.n);
        evaluateAndThenSortPopulation();
        mutantsRemainingIndex = IntStream.range((int)(configuration.pe*configuration.p)+1, configuration.p).boxed().collect(Collectors.toCollection(ArrayList::new));
        mutantsSelectedIndex = new LinkedList<>();
        if(random == null) {
            random = new Random();
            random.setSeed(System.nanoTime());
        }
        this.rand = random;
        super.updateBest(new Individual(population.get(0)));
    }
    
    private void evaluateAndThenSortPopulation() {
        population.applyToAll(individual -> individual.setValue(decoder.apply(individual)), true);
        population.sort();
    }
    
    /**
     * Evolves one epoch and updates the best individual of the population.
     * Returns true if the stopping criterion has not been met.
     * @return true if the epoche was correctly processed, false otherwise.
     */
    @Override
    public boolean iterate() {
        if(stoppingCriterion.test(this))
            return false;
        super.increaseIterations();
        int eliteSize = (int)(configuration.pe*configuration.p);
        for(int i=eliteSize; i<configuration.p; i++) {
            Individual notElite = population.get(i);
            int eliteIndex = rand.nextInt(eliteSize);
            Individual elite = population.get(eliteIndex);
            population.set(i, crossingOver.apply(elite, notElite));
        }
        int mutantsSize = (int)(configuration.pm*configuration.p);
        for(int i=0; i<mutantsSize; i++) {
            Integer removed = mutantsRemainingIndex.remove(rand.nextInt(mutantsRemainingIndex.size()));
            mutantsSelectedIndex.add(removed);
            individualGenerator.accept(population.get(removed));
        }
        mutantsRemainingIndex.addAll(mutantsSelectedIndex);
        mutantsSelectedIndex.clear();
        evaluateAndThenSortPopulation();
        Individual tmpIndividual = population.get(0);
        if(fitnessFunction.compare(tmpIndividual, super.getBestVector()) < 0)
            super.updateBest(new Individual(tmpIndividual));
        return true;
    }
}
