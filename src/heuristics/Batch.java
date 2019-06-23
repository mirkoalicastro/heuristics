package heuristics;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * A multi-threading manager for heuristics
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 * @see Heuristic
 */
public class Batch {
    private Comparator<? super Heuristic> bestHeuristicComparatorMin;
    private Comparator<? super Heuristic> bestHeuristicComparatorMax;
    private Heuristic[] heuristics;
    private FitnessFunction fitnessFunction;
    
    /**
     * Sets the heuristics.
     * Each heuristic should be initialized with a different seed.
     * The array length will determine the number of threads to create when
     * iterating.
     * @param heuristics the heuristics array
     */
    public void setHeuristics(Heuristic[] heuristics) {
        this.heuristics = heuristics;
    }

    /**
     * Returns the heuristics that this class is currently managing.
     * @return the heuristics array
     */
    public Heuristic[] getHeuristics() {
        return heuristics;
    }

    /**
     * Sets the fitness function type, i.e., specifies if the objective function
     * must be minimized or maximized.
     * @param fitnessFunction the fitness function
     */
    public void setFitnessFunction(FitnessFunction fitnessFunction) {
        this.bestHeuristicComparatorMin = (h1, h2) -> {
            int comp = fitnessFunction.compare(h1.getBestVector(), h2.getBestVector());
            if(comp != 0)
                return comp;
            return Integer.compare(h1.getIterationsForBestVector(), h2.getIterationsForBestVector());
        };
        this.bestHeuristicComparatorMax = (h1, h2) -> {
            int comp = fitnessFunction.compare(h1.getBestVector(), h2.getBestVector());
            if(comp != 0)
                return comp;
            return Integer.compare(h2.getIterationsForBestVector(), h1.getIterationsForBestVector());
        };
        this.fitnessFunction = fitnessFunction;
    }

    /**
     * Returns the fitness function type.
     * @return the fitness function type
     */
    public FitnessFunction getFitnessFunction() {
        return fitnessFunction;
    }
    
    /**
     * Performs a parallel iteration over the currently managed heuristics.
     * @see Heuristic#iterate() 
     * @throws InterruptedException
     */
    public void iterate() throws InterruptedException {
        iterate(1);
    }
    
    /**
     * Performs parallel iterations over the currently managed heuristics.
     * @param iterations the number of iterations that has to be performed
     * @see Heuristic#iterate() 
     * @throws InterruptedException 
     */
    public synchronized void iterate(Integer iterations) throws InterruptedException {
        /* Note: don't use parallel stream because we want to force the parallel
           execution of the evolution of all independent heuristics.
        */
        Thread[] threads = new Thread[heuristics.length];
        for(int i=0; i<threads.length; i++) {
            threads[i] = new MyThread(i, iterations);
            threads[i].start();
        }
        for(Thread thread: threads)
            thread.join();
    }
    
    /**
     * Performs a parallel undefined number of iterations over the currently
     * managed heuristics.
     * Heuristics will be stopped by their stopping criteria.
     * @throws InterruptedException 
     * @see Heuristic#iterateUntilStoppingCriterion() 
     */
    public void iterateUntilStoppingCriterion() throws InterruptedException {
        iterate(null);
    }
    
    /**
     * Returns the vector that represents the solution with the best objective
     * function value, among the heuristics array.
     * @return the best vector
     */
    public Vector getBestVector() {
        return IntStream.range(0, heuristics.length)
                .mapToObj(i -> heuristics[i].getBestVector())
                .sorted(fitnessFunction)
                .findFirst()
                .get();
    }

    /**
     * Returns the vector that represents the solution with the best objective
     * function value, of a specified heuristic.
     * @param index the index of the heuristic
     * @return the best vector of that heuristic
     */
    public Vector getBestVectorAt(int index) {
        return heuristics[index].getBestVector();
    }

    /**
     * Returns the number of iterations at which the <i>index</i>-th heuristic
     * reached its best vector.
     * @param index the index of the heuristic
     * @return the number of iterations for the best vector of that heuristic.
     */
    public int getIterationsForBestVectorAt(int index) {
        return heuristics[index].getIterationsForBestVector();
    }

    /**
     * Returns the minimum number of iterations at which a heuristic reached
     * the best vector, among the heuristics array.
     * @return the minimum number of iterations for the best vector
     */
    public int getIterationsForBestVector() {
        return Arrays.stream(heuristics)
                .sorted(bestHeuristicComparatorMin)
                .findFirst().get()
                .getIterationsForBestVector();
    }
    
    /**
     * Returns the total maximum number of iterations performed by a heuristic
     * that reached the best vector, among the heuristics array.
     * @return the total maximum number of iterations
     */
    public int getTotalIterationsForBestVector() {
        return Arrays.stream(heuristics)
                .sorted(bestHeuristicComparatorMax)
                .findFirst().get()
                .getIterations();
    }
    
    /**
     * Returns the history (objective function value and milliseconds at which
     * that value was setted) of a heuristic that reached the best vector,
     * among the heuristics array.
     * @return the history
     */
    public Map<Double, Long> getHistoryOfBest() {
        return Arrays.stream(heuristics)
                .sorted(bestHeuristicComparatorMin)
                .findFirst().get().getHistory();
    }
    private class MyThread extends Thread {
        final int index;
        final Integer iterations;
        MyThread(int index, Integer iterations) {
            this.iterations = iterations;
            this.index = index;
        }
        @Override
        public void run() {
            synchronized(heuristics[index]) {
                if(iterations == null)
                    heuristics[index].iterateUntilStoppingCriterion();
                else
                    for(int e=0; e<iterations && !isInterrupted(); e++)
                        if(!heuristics[index].iterate())
                            return;
            }
        }
    }
}
