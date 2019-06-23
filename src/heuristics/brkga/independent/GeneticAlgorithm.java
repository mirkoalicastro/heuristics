package heuristics.brkga.independent;

import heuristics.Evaluator;
import heuristics.FitnessFunction;
import heuristics.Heuristic;
import heuristics.brkga.client.Configuration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import heuristics.brkga.client.CrossingOver;
import heuristics.brkga.client.DNAGenerator;
import java.util.function.Predicate;
import java.util.Random;

/**
 *
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
public class GeneticAlgorithm extends Heuristic {
    private final Configuration configuration;
    private final DNAGenerator individualGenerator;
    private final Population population;
    private final List<Integer> mutantsRemainingIndex;
    private final Evaluator decoder;
    private final Predicate<Heuristic> stoppingCriterion;
    private final CrossingOver crossingOver;
    private final List<Integer> mutantsSelectedIndex;
        
    private final Random rand;
    private final FitnessFunction fitnessFunction;
    
    GeneticAlgorithm(FitnessFunction fitnessFunction, Configuration configuration, CrossingOver crossingOver, DNAGenerator sequenceGenerator, Evaluator decoder, Predicate<Heuristic> stoppingCriterion, Random random) {
        this.stoppingCriterion = stoppingCriterion;
        this.fitnessFunction = fitnessFunction;
        this.configuration = configuration;
        this.crossingOver = crossingOver;
        this.individualGenerator = sequenceGenerator;
        this.decoder = decoder;
        population = new Population(sequenceGenerator, fitnessFunction, configuration.p, configuration.n);
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
        population.applyToAll(individual -> individual.setValue(decoder.eval(individual)), true);
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
            crossingOver.crossover(elite, notElite);
        }
        int mutantsSize = (int)(configuration.pm*configuration.p);
        for(int i=0; i<mutantsSize; i++) {
            Integer removed = mutantsRemainingIndex.remove(rand.nextInt(mutantsRemainingIndex.size()));
            mutantsSelectedIndex.add(removed);
            population.get(removed).random(individualGenerator);
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
