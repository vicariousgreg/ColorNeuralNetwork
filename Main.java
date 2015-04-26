import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Main {
   public static void main(String[] args) {
      ////////////////
      /* TEST CASES */
      ////////////////

      Random rand = new Random();
      final int kNumTests = 5000;
      final int kNumFringe = 100;

      // Generate test cases.
      ArrayList<TestCase> tests = new ArrayList<TestCase>();
      for (int i = 0; i < kNumTests; ++i) {
         double x = rand.nextDouble() * 2 - 1.0;
         double y = rand.nextDouble() * 2 - 1.0;
         double answer = (Double.compare(x, y) < 0) ? 1.0 : 0.0;
         tests.add(new TestCase(new double[] { x, y }, new double[] { answer }));
      }
      // Add in fringe cases
      for (int i = 0; i < kNumFringe; ++i) {
         double x = rand.nextDouble() * 2 - 1.0;
         double y = x - 0.01;
         tests.add(new TestCase(new double[] { x, y }, new double[] { 0.0 }));
         y = x + 0.01;
         tests.add(new TestCase(new double[] { x, y }, new double[] { 1.0 }));
      }

      // Generate validation set test cases.
      ArrayList<TestCase> validation = new ArrayList<TestCase>();
      for (int i = 0; i < kNumTests; ++i) {
         double x = rand.nextDouble() * 2 - 1.0;
         double y = rand.nextDouble() * 2 - 1.0;
         double answer = (Double.compare(x, y) < 0) ? 1.0 : 0.0;
         validation.add(new TestCase(new double[] { x, y }, new double[] { answer }));
      }
      // Add in fringe cases
      for (int i = 0; i < kNumFringe; ++i) {
         double x = rand.nextDouble() * 2 - 1.0;
         double y = x - 0.01;
         validation.add(new TestCase(new double[] { x, y }, new double[] { 0.0 }));
         y = x + 0.01;
         validation.add(new TestCase(new double[] { x, y }, new double[] { 1.0 }));
      }

      //////////////
      /* LEARNING */
      //////////////

      // Create the network.
      int[] layerSizes = new int[] {2, 3, 3, 1};
      Network network = new Network(layerSizes);

      boolean acceptable = false;
      int stale = 0;
      final int kStaleThreshold = 100;
      double testError = calcTotalTestError(tests, network);
      double prevTestError = 10000.0;
      double percentCorrect = calcPercentCorrect(tests, network);
      double prevPercentCorrect = 0;
      final double kAcceptableTestError = 5;
      final double kPercentCorrectThreshold = 95;

      System.out.println("Total test error before learning: " + testError);
      System.out.println("Passing percentage: %" + percentCorrect);

      
      /*
      for (int i = 0; i < tests.size(); ++i) {
         TestCase test = tests.get(i);
         double beforeError = Math.abs(network.calcTestError(test));
         network.learn(test);
         double afterError = Math.abs(network.calcTestError(test));
      }

      System.out.println("Total test error after learning: " + calcTotalTestError(tests, network));
      System.out.println("Passing percentage: %" + calcPercentCorrect(tests, network));

      System.exit(0);
      */

      // Teach the network until the error is acceptable.
      while (!acceptable) {
         // Teach the network using the tests.
         for (int i = 0; i < tests.size(); ++i) {
            network.learn(tests.get(i));
         }

         testError = calcTotalTestError(validation, network);
         percentCorrect = calcPercentCorrect(validation, network);

         acceptable = testError < kAcceptableTestError && percentCorrect > kPercentCorrectThreshold;

         // Determine if the network needs to be reset.
         if (!acceptable && (stale > kStaleThreshold || prevTestError > testError + 25)) {
            network = new Network(layerSizes);
            stale = 0;
//            System.out.println("===RESET===");
            System.out.print(".");
         } else if (Double.compare(testError, prevTestError) == 0 ||
                    Double.compare(percentCorrect, prevPercentCorrect) == 0) {
            ++stale;
         } else {
            System.out.println("Test error: " + testError);
            System.out.println("PercentCorrect: " + percentCorrect);
            System.out.println();
            stale = 0;
         }
            System.out.println("Test error: " + testError);
            System.out.println("PercentCorrect: " + percentCorrect);
            System.out.println();
         prevTestError = testError;
         prevPercentCorrect = percentCorrect;
      }

      System.out.println("Total test error after learning: " + calcTotalTestError(validation, network));
      System.out.println("Passing percentage: %" + calcPercentCorrect(validation, network));


      /////////////////
      /* INTERACTION */
      /////////////////


      Scanner in = new Scanner(System.in);
      boolean quit = false;

      System.out.println("\nAsk me!");

      while (!quit) {
         System.out.println("\nEnter x and y: ");
         String line = in.nextLine();
         if (line.contains("quit")) quit = true;
         else {
            String[] tokens = line.trim().split("\\s+");
            try {
               double x = Double.parseDouble(tokens[0]);
               double y = Double.parseDouble(tokens[1]);
               double[] output = network.fire(new double[] {x, y});

               if (x < y && output[0] > 0.5 ||
                   x > y && output[0] < 0.5) {
                  System.out.println("Successful guess!");
               } else {
                  System.out.println("Unsuccessful guess!");
                  System.out.println("  Got: " + output[0]);
                  if (x < y)
                     System.out.println("  Expected: 1.0");
                  else
                     System.out.println("  Expected: 0.0");
               }

            } catch (Exception e) {
               System.out.println("Invalid input!");
            }
         }
      }
   }

   public static String arrayToString(double[] arr) {
      StringBuilder sb = new StringBuilder("{ ");

      for (int i = 0; i < arr.length - 1; ++i) {
         sb.append(arr[i] + ", ");
      }
      sb.append(arr[arr.length - 1] + " }");

      return sb.toString();
   }

   public static double calcTotalTestError(ArrayList<TestCase> tests, Network network) {
      double totalTestError = 0.0;
      for (int i = 0; i < tests.size(); ++i) {
         totalTestError += Math.abs(network.calcTestError(tests.get(i)));
      }
      return totalTestError;
   }

   public static double calcPercentCorrect(ArrayList<TestCase> tests, Network network) {
      int correct = 0;

      for (int i = 0; i < tests.size(); ++i) {
         TestCase test = tests.get(i);
         double[] output = network.fire(test.inputs);
         boolean passed = true;

         for (int j = 0; j < output.length; ++j) {
            boolean outLow = Double.compare(output[j], 0.5) < 0;
            boolean expLow = Double.compare(test.outputs[j], 0.5) < 0;

            if (outLow != expLow) passed = false;
         }

         if (passed) ++correct;
      }

      return 100.0 * (double) correct / tests.size();
   }
}
