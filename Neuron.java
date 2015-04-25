import java.util.Random;

/**
 * Represents a neural network node.
 */
public class Neuron {
   /** Number of inputs. */
   private int numInputs;
   /** Array of weight values. */
   private double[] weights;
   /** Bias weight. */
   private double bias;

   /**
    * Randomized constructor.
    * Randomizes weights and bias.
    *
    * @param numInputs number of node inputs.
    */
   public Neuron(int numInputs) {
      this.numInputs = numInputs;
      this.weights = new double[numInputs];
      randomize();
   }

   /**
    * Explicit constructor.
    * Initializes weights and bias.
    * @param weights initial weights
    * @param bias initial bias
    */
   private Neuron(double[] weights, double bias) {
      this.numInputs = weights.length;
      this.weights = new double[weights.length];
      System.arraycopy(weights, 0, this.weights, 0, weights.length);
      this.bias = bias;
   }

   /**
    * Randomizes the weights and bias.
    */
   public void randomize() {
      Random rand = new Random();
      // Randomize weights.
      for (int i = 0; i < numInputs; ++i) {
         this.weights[i] = rand.nextDouble() * 2 - 1;
      }
      this.bias = rand.nextDouble() * 2 - 1;
   }

   /**
    * Fires the neuron.
    * @param inputs array of input signals
    * @return output signal
    */
   public double fire(double[] inputs) {
      // Ensure input is of proper length.
      if (inputs.length == numInputs) {
         double x = 0.0;

         // Calculate sigmoid input.
         for (int i = 0; i < numInputs; ++i) {
            x += inputs[i] * weights[i];
         }
         x += bias;

         // Calculate signal output.
         double result = Sigmoid.calculate(x);
         //System.out.println("          OUTPUT: " + result);
         return result;
      }

      // If not, let's just return 0.0 for now.
      return 0.0;
   }

   /**
    * Getter for weights.
    * @return weights
    */
   public double[] getWeights() {
      return weights;
   }

   /**
    * Getter for bias.
    * @return bias
    */
   public double getBias() {
      return bias;
   }

   /**
    * Clones this neuron.
    * @return clone
    */
   public Neuron clone() {
      return new Neuron(this.weights, this.bias);
   }

   /**
    * Prints the neuron's weights and bias.
    */
   public void print() {
      System.out.println("        NEURON: ");
      System.out.println("          WEIGHTS: " + Main.arrayToString(weights));
      System.out.println("          BIAS: " + bias);
   }
}
