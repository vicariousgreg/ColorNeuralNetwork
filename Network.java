import java.util.ArrayList;

/**
 * Represents a neural network.
 */
public class Network {
   /** Network neuron layers. */
   private ArrayList<Neuron[]> layers;

   /**
    * Constructor.
    * @param layerSizes number of neurons per layer (0 is input size)
    */
   public Network(int[] layerSizes) {
      layers = new ArrayList<Neuron[]>();

      // Create each layer
      for (int i = 1; i < layerSizes.length; ++i) {
         Neuron[] layer = new Neuron[layerSizes[i]];

         // Initialize neurons using previous layer size.
         for (int j = 0; j < layer.length; ++j) {
            layer[j] = new Neuron(layerSizes[i-1]);
         }

         layers.add(layer);
      }
   }

   /**
    * Fires the neural network.
    * @param input input signals
    * @return output signals
    */
   public double[] fire(double[] input) {
      System.out.println("  Firing network!");
      System.out.println("    Input: " + Main.arrayToString(input));

      double[] output = null;
      // Thread input through network layers.
      for (Neuron[] layer : layers) {
         output = new double[layer.length];

         // Process input and catch output.
         for (int i = 0; i < layer.length; ++i) {
            output[i] = layer[i].fire(input);
         }

         // Set up input for next layer.
         input = output;
      }

      System.out.println("    Output: " + Main.arrayToString(output));
      return output;
   }

   /**
    * Calculates the fitness of the network given a set of test cases.
    * @param tests test cases
    * @return fitness between 0.0 and 1.0
    */
   public double calcFitness(ArrayList<TestCase> tests) {
      double totalError = 0.0;

      // Run each tests and sum up total error.
      for (TestCase test : tests) {
         System.out.println("Running test!");
         System.out.println("Expecting: " + Main.arrayToString(test.outputs));
         double[] output = fire(test.inputs);
         double error = 0.0;

         // Calculate test error.
         for (int i = 0; i < output.length; ++i) {
            error += Math.abs(output[i] - test.outputs[i]);
         }

         // Divide test error by number of outputs and add to total.
         totalError += error / output.length;
      }

      // Return the average fitness.
      return 1 - (totalError / tests.size());
   }
}
