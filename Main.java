import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Main {
   public static void main(String[] args) {
      Random rand = new Random();

      // Generate test cases.
      ArrayList<TestCase> tests = new ArrayList<TestCase>();
      for (int i = 0; i < 5000; ++i) {
         double x = rand.nextDouble() * 2 - 1.0;
         double y = rand.nextDouble() * 2 - 1.0;
         double answer = (x < y) ? 1.0 : 0.0;
         tests.add(new TestCase(new double[] { x, y }, new double[] { answer }));
      }
      // Add in fringe cases
      for (int i = 0; i < 50; ++i) {
         double x = rand.nextDouble() * 2 - 1.0;
         double y = x - 0.001;
         double answer = (x < y) ? 1.0 : 0.0;
         tests.add(new TestCase(new double[] { x, y }, new double[] { answer }));
      }

      // Generate validation set test cases.
      ArrayList<TestCase> validation = new ArrayList<TestCase>();
      for (int i = 0; i < 1000; ++i) {
         double x = rand.nextDouble() * 2 - 1.0;
         double y = rand.nextDouble() * 2 - 1.0;
         double answer = (x < y) ? 1.0 : 0.0;
         validation.add(new TestCase(new double[] { x, y }, new double[] { answer }));
      }

      Network network = new Network(new int[] {2, 3, 1});

      boolean acceptable = false;
      int stale = 0;
      double prevPercentCorrect = calcPercentCorrect(tests, network);
      double percentCorrect = 0.0;

      System.out.println("Average test error before learning: " + calcAverageTestError(tests, network));
      System.out.println("Passing percentage: %" + calcPercentCorrect(tests, network));


      while (!acceptable) {
         for (int i = 0; i < tests.size(); ++i) {
            TestCase test = tests.get(i);
            ArrayList<double[]> outputs = network.getOutputs(test.inputs);
//            double[] errors = network.calcError(outputs.get(outputs.size() - 1), test.outputs);
//            double oldError = 0.0;
//            for (int eIndex = 0; eIndex < errors.length; ++eIndex) {
//               oldError += errors[eIndex];
//            }

            network.learn(test);

//            outputs = network.getOutputs(test.inputs);
//            errors = network.calcError(outputs.get(outputs.size() - 1), test.outputs);
//            double newError = 0.0;
//            for (int eIndex = 0; eIndex < errors.length; ++eIndex) {
//               newError += errors[eIndex];
//            }
         }
         percentCorrect = calcPercentCorrect(tests, network);
         System.out.println("Passing percentage: %" + percentCorrect);
         acceptable = percentCorrect > 99.7;
         if (stale > 100 || prevPercentCorrect > percentCorrect + 10) {
            network = new Network(new int[] {2, 3, 1});
         } else if (Double.compare(prevPercentCorrect, percentCorrect) == 0) {
            ++stale;
         }
         prevPercentCorrect = percentCorrect;
      }

      System.out.println("Average test error after learning: " + calcAverageTestError(tests, network));
      System.out.println("Passing percentage: %" + calcPercentCorrect(tests, network));


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

   public static double calcAverageTestError(ArrayList<TestCase> tests, Network network) {
      double totalTestError = 0.0;
      for (int i = 0; i < tests.size(); ++i) {
         totalTestError += Math.abs(network.calcTestError(tests.get(i)));
      }
      return totalTestError / tests.size();
   }

   public static double calcPercentCorrect(ArrayList<TestCase> tests, Network network) {
      int correct = 0;

      for (int i = 0; i < tests.size(); ++i) {
         TestCase test = tests.get(i);
         double[] output = network.fire(test.inputs);
         boolean passed = true;

         for (int j = 0; j < output.length; ++j) {
            boolean outLow = output[j] < 0.5;
            boolean expLow = test.outputs[j] < 0.5;

            if (outLow != expLow) passed = false;
         }

         if (passed) ++correct;
      }

      return 100.0 * (double) correct / tests.size();
   }
}
