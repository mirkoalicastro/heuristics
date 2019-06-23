package heuristics.brkga.client;

/**
 * 
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
public class Configuration {
    
    /**
     * Number of independent populations.
     */
    public final int ip;
    
    /**
     * Chromosome length.
     */
    public final int n;

    /**
     * Population size.
     */
    public final int p;

    /**
     * Fraction of population to be the elite-set.
     */
    public final float pe;

    /**
     * Fraction of population to be replaced by mutants.
     */
    public final float pm;
    
    /**
     * Constructs a Config object for a genetic algorithm.
     * @param ip number of independent populations
     * @param n chromosome length
     * @param p population size
     * @param pe fraction of population to be the elite-set
     * @param pm fraction of population to be replaced by mutants
     */
    public Configuration(int n, int ip, int p, float pe, float pm) {
        this.ip = ip;
        this.n = n;
        this.p = p;
        this.pe = pe;
        this.pm = pm;
    }
}
