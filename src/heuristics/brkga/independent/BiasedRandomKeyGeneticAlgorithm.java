package heuristics.brkga.independent;

import heuristics.Heuristic;
import heuristics.Vector;
import heuristics.brkga.client.Configuration;
import java.util.ArrayList;
import java.util.Collections;
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
public class BiasedRandomKeyGeneticAlgorithm extends Heuristic {
    private final Consumer<? super Vector> individualGenerator;
    private final Population population;
    private final List<Integer> notElites;
    private final Function<? super Vector, Double> decoder;
    private final Predicate<Heuristic> stoppingCriterion;
    private final BiFunction<? super Vector, ? super Vector, Vector> crossingOver;
        
    private final Random rand;
    private final Comparator<? super Vector> fitnessFunction;
    private final int eliteSize;
    private final int mutantsSize;
    
    BiasedRandomKeyGeneticAlgorithm(Comparator<? super Vector> fitnessFunction, Configuration config, BiFunction<? super Vector, ? super Vector, Vector> crossingOver, Consumer<? super Vector> individualGenerator, Function<? super Vector, Double> decoder, Predicate<Heuristic> stoppingCriterion, Random random) {
        this.stoppingCriterion = stoppingCriterion;
        this.fitnessFunction = fitnessFunction;
        this.crossingOver = crossingOver;
        this.individualGenerator = individualGenerator;
        this.decoder = decoder;
        population = new Population(config.populationSize, config.chromosomeLength);
        population.applyToAll(individualGenerator, true);
        evaluateAndThenSortPopulation();
        eliteSize = (int)(config.eliteFraction*config.populationSize);
        mutantsSize = (int)(config.mutantFraction*config.populationSize);
        notElites = IntStream.range(eliteSize, config.populationSize).boxed().collect(Collectors.toCollection(ArrayList::new));
        if(random == null) {
            random = new Random();
            random.setSeed(System.nanoTime());
        }
        this.rand = random;
        super.updateBest(new Vector(population.get(0)));
    }
    
    private void evaluateAndThenSortPopulation() {
        population.applyToAll(individual -> individual.setValue(decoder.apply(individual)), true);
        population.sort(fitnessFunction);
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
        Collections.shuffle(notElites, rand);
        for(int j=0; j<mutantsSize; j++)
            individualGenerator.accept(population.get(notElites.get(j)));
        for(int j=mutantsSize; j<notElites.size(); j++) {
            int notEliteIndex = notElites.get(j);
            Vector notElite = population.get(notEliteIndex);
            int eliteIndex = rand.nextInt(eliteSize);
            Vector elite = population.get(eliteIndex);
            population.set(notEliteIndex, crossingOver.apply(elite, notElite));
        }
        evaluateAndThenSortPopulation();
        Vector tmpVector = population.get(0);
        if(fitnessFunction.compare(tmpVector, super.getBestVector()) < 0)
            super.updateBest(new Vector(tmpVector));
        return true;
    }
}
