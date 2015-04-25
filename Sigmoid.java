import java.util.HashMap;
import java.util.Random;

/**
 * Models the Sigmoid function using precalculated values to speed up computation.
 */
public class Sigmoid {
   /**
    * Map of x*10 values to Sigmoid outputs.
    * Inputs are multiplied by ten so they are discrete.
    */
   private static HashMap<Integer, Double> precalculated;
   static {
      precalculated = new HashMap<Integer, Double>();

      for (int i = -100; i <= 100; ++i) {
         precalculated.put(i, trueCalculate((double) i / 10));
      }
   }

   /**
    * Performs an actual Sigmoid calculation.
    * @param x input value
    * @return sigmoid value
    */
   public static double trueCalculate(double x) {
      return 1 / (1 + Math.exp(-x));
   }

   /**
    * Performs interpolation to estimate the sigmoid function.
    * @param x input value
    * @return estimated sigmoid value
    */
   public static double calculate(double x) {
      if (x > 10.0) return 1.0;
      if (x < -10.0) return -1.0;

      int x0 = (int) Math.floor(x * 10);
      int x1 = (int) Math.ceil(x * 10);
      double y0 = precalculated.get(x0);
      double y1 = precalculated.get(x1);
      return interpolate(x, y0, y1, (double)x0 / 10, (double)x1 / 10);
   }

   /**
    * Calculates a linear interpolation.
    * @param x input
    * @param y0 left y value
    * @param y1 right y value
    * @param x0 left x value
    * @param x1 right x value
    * @return interpolated result
    */
   private static double interpolate(double x, double y0, double y1,
                                     double x0, double x1) {
      return y0 + ((y1 - y0) * ((x - x0) / (x1 - x0)));
   }
}
