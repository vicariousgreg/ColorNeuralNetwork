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
    * Fires the neural network and returns output.
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
    * Fires the neural network and returns all neuron outputs.
    * @param input input signals
    * @return all neuron output signals
    */
   public ArrayList<double[]> getOutputs(double[] input) {
      ArrayList<double[]> outputs = new ArrayList<double[]>();
      double[] output = null;

      // Thread input through network layers.
      for (Neuron[] layer : layers) {
         output = new double[layer.length];

         // Process input and catch output.
         for (int i = 0; i < layer.length; ++i) {
            output[i] = layer[i].fire(input);
         }

         outputs.add(output);

         // Set up input for next layer.
         input = output;
      }

      return outputs;
   }

   /**
    * Runs a test and calculates the total error of a test.
    * @param test test to calculate error for
    * @return total error
    */
   public double calcTestError(TestCase test) {
      ArrayList<double[]> outputs = getOutputs(test.inputs);
      double[] error = calcError(outputs.get(outputs.size() - 1), test.outputs);

      double totalError = 0.0;
      for (int i = 0; i < error.length; ++i) {
         totalError += error[i];
      }
      return totalError;
   }

   /**
    * Calculates the error of the network given actual and expected output
    * @param actual network output
    * @param expected expected output
    * @return error
    */
   public double[] calcError(double[] actual, double[] expected) {
      double[] errors = new double[actual.length];

      // Calculate test error for each output neuron.
      for (int i = 0; i < actual.length; ++i) {
         errors[i] = actual[i] *
                     (1 - actual[i]) *
                     (expected[i] - actual[i]);
      }

      // Return the errors.
      return errors;
   }

   /**
    * Teaches the network using a test case.
    * @param test test case
    */
   public void learn(TestCase test) {
      // Learning constant.
      final double kN = 1.0;
      final double kGammaBias = 1.0;

      // Calculate network outputs
      ArrayList<double[]> outputs = getOutputs(test.inputs);

      // Calculate error for output layer
      double[] target = test.outputs;
      double[] output = outputs.get(outputs.size() - 1);
      double[] error = calcError(output, target);

      // Backpropagate through layers.
      for (int layerIndex = layers.size() - 1; layerIndex >= 0; --layerIndex) {
         output = (layerIndex > 0)
            ? outputs.get(layerIndex - 1)
            : test.inputs;
         Neuron[] currLayer = layers.get(layerIndex);

         int previousLength = (layerIndex > 0)
            ? layers.get(layerIndex - 1).length
            : test.inputs.length;

         double[][] weights = new double[previousLength][currLayer.length];

         // Adjust current layer weights and biases.
         for (int currIndex = 0; currIndex < currLayer.length; ++currIndex) {
            // Adjust weights.
            for (int prevIndex = 0; prevIndex < previousLength; ++prevIndex) {
               weights[prevIndex][currIndex] =
                  currLayer[currIndex].adjustWeight(prevIndex,
                     kN * error[currIndex] *
                          output[prevIndex]);
            }
            // Adjust bias.
            currLayer[currIndex].adjustBias(kGammaBias * error[currIndex]);
         }

         if (layerIndex == 0) break;

         // Calculate hidden layer error.
         double[] newError = new double[previousLength];
         for (int prevIndex = 0; prevIndex < previousLength; ++prevIndex) {
            double sigma = 0.0;

            // Calculate error sigma.
            for (int currIndex = 0; currIndex < currLayer.length; ++currIndex) {
               sigma += error[currIndex] * weights[prevIndex][currIndex];
            }

            newError[prevIndex] = output[prevIndex] * (1 - output[prevIndex]) * sigma;
         }
         error = newError;
      }
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
