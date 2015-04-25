import java.util.ArrayList;
import java.util.Random;

/**
 * Genetic Algorithm class for neural networks.
 */
public class GeneticAlgorithm {
   private static final int kPopulationSize = 50;
   private static final int kTournamentSize = 5;
   private static final double kMutationRate = 0.1;
   private static final boolean kElitism = true;

   /** Random number generator. */
   private Random rand;

   /** Test suite for fitness testing. */
   private ArrayList<TestCase> tests;
   /** Current population. */
   private ArrayList<Network> population;

   public GeneticAlgorithm(int[] layerSizes, ArrayList<TestCase> tests) {
      this.rand = new Random();
      this.tests = tests;
      genPopulation(layerSizes);
   }

   /**
    * Runs the genetic algorithm for a given number of generations and returns
    * the best individual in the last generation.
    * @param numGenerations number of generations to run
    * @return best individual in last generation
    */
   public Network run(int numGenerations) {
      while (numGenerations-- > 0) {
         evolve();
      }

      return getBest();
   }

   /**
    * Creates a randomly generated initial population.
    * @param layerSizes network neuron layer sizes
    */
   private void genPopulation(int[] layerSizes) {
      population = new ArrayList<Network>();

      // Generate individuals and calculate fitness.
      for (int i = 0; i < kPopulationSize; ++i) {
         population.add(new Network(layerSizes));
      }
   }

   /**
    * Gets the best individual in the population.
    * @return best individual
    */
   private Network getBest() {
      Network best = population.get(0);
      double bestFitness = population.get(0).calcFitness(tests);

      for (int i = 1; i < kPopulationSize; ++i) {
         Network individual = population.get(i);
         double fitness = individual.calcFitness(tests);

         if (fitness > bestFitness) {
            best = individual;
            bestFitness = fitness;
         }
      }

      return best;
   }

   /**
    * Evolves the population.
    * Replaces the population with the next generation.
    */
   private void evolve() {
      ArrayList<Network> generation = new ArrayList<Network>();

      if (kElitism) {
         generation.add(getBest());
      }

      while (generation.size() < kPopulationSize) {
         generation.add(tournament().mutate(kMutationRate));
      }
      this.population = generation;
      printPopulation();
   }

   /**
    * Performs a tournament and returns the winner.
    * @return tournament winner
    */
   private Network tournament() {
      Network best = null;
      double bestFitness = -1.0;

      // Generate tournament population.
      for (int i = 0; i < kTournamentSize; ++i) {
         Network individual = population.get(rand.nextInt(kPopulationSize));

         // Calculate fitness and determine if best.
         double fitness = individual.calcFitness(tests);
         if (fitness > bestFitness) {
            best = individual;
            bestFitness = fitness;
         }
      }

      //System.out.println("TOURNAMENT BEST: " + best.calcFitness(tests));
      return best;
   }

   private void printPopulation() {
      System.out.println("GENERATION:");
      for (int i = 0; i < kPopulationSize; ++i) {
         System.out.println("  " + population.get(i) + ": " + population.get(i).calcFitness(tests));
      }
      System.out.println("  BEST:" + getBest().calcFitness(tests));
   }
}