import java.util.ArrayList;
import java.util.Random;

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
    * Explicit constructor.
    * @param layers neuron layers
    */
   private Network(ArrayList<Neuron[]> layers) {
      this.layers = layers;
   }

   /**
    * Fires the neural network.
    * @param input input signals
    * @return output signals
    */
   public double[] fire(double[] input) {
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

      return output;
   }

   /**
    * Calculates the error of the network given a set of test cases.
    * @param test test case
    * @return error
    */
   public double[] calcError(TestCase test) {
      double[] output = fire(test.inputs);
      double[] errors = new double[output.length];

      // Calculate test error for each output neuron.
      for (int i = 0; i < output.length; ++i) {
         errors[i] = output[i] *
                     (1 - output[i]) *
                     (test.outputs[i] - output[i]);
      }

      System.out.println("Calculating error.");
      //print();
      System.out.println("  Test:");
      System.out.println("    Input: " + Main.arrayToString(test.inputs));
      System.out.println("    Expected output: " + Main.arrayToString(test.outputs));
      System.out.println("  Got: " + Main.arrayToString(output));
      System.out.println("  Error: " + Main.arrayToString(errors));
      System.out.println();


      // Return the errors.
      return errors;
   }

   /**
    * Clones this network.
    * @return cloned network
    */
   public Network clone() {
      ArrayList<Neuron[]> newLayers = new ArrayList<Neuron[]>();

      for (Neuron[] layer : layers) {
         Neuron[] newLayer = new Neuron[layer.length];

         for (int i = 0; i < layer.length; ++i) {
            newLayer[i] = layer[i].clone();
         }
         newLayers.add(newLayer);
      }

      return new Network(newLayers);
   }

   /**
    * Prints the network.
    */
   public void print() {
      System.out.println("  Network");

      for (Neuron[] layer : layers) {
         System.out.println("      LAYER");
         for (int i = 0; i < layer.length; ++i) {
            layer[i].print();
         }
      }
   }
}
