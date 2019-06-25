package heuristics.brkga.client;

/**
 * 
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
public class Configuration {
    
    /**
     * Number of independent populations.
     */
    public final int populations;
    
    /**
     * Chromosome length.
     */
    public final int chromosomeLength;

    /**
     * Population size.
     */
    public final int populationSize;

    /**
     * Fraction of population to be the elite-set.
     */
    public final float eliteFraction;

    /**
     * Fraction of population to be replaced by mutants.
     */
    public final float mutantFraction;
    
    /**
     * Constructs a Config object for a genetic algorithm.
     * @param ip number of independent populations
     * @param n chromosome length
     * @param p population size
     * @param pe fraction of population to be the elite-set
     * @param pm fraction of population to be replaced by mutants
     */
    public Configuration(int n, int ip, int p, float pe, float pm) {
        this.populations = ip;
        this.chromosomeLength = n;
        this.populationSize = p;
        this.eliteFraction = pe;
        this.mutantFraction = pm;
    }
}
