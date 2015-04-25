import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Main {
   public static void main(String[] args) {
      // Generate test cases.
      ArrayList<TestCase> tests = new ArrayList<TestCase>();
      Random rand = new Random();
      for (int i = 0; i < 1000; ++i) {
         double x = rand.nextDouble();
         double y = rand.nextDouble();
         double answer = (x < y) ? 1.0 : 0.0;
         tests.add(new TestCase(new double[] { x, y }, new double[] { answer }));
      }
      Network network = new Network(new int[] {2, 3, 1});

      for (int i = 0; i < 10; ++i) {
         network.calcError(tests.get(i));
      }
      System.exit(0);


      Scanner in = new Scanner(System.in);
      boolean quit = false;

      while (!quit) {
         System.out.print("Enter x: ");
         double x = in.nextDouble();
         System.out.print("Enter y: ");
         double y = in.nextDouble();
         double result = network.fire(new double[] {x,y})[0];
         if (result < 0.5) {
            if (x > y) System.out.println("Correct!");
            else System.out.println("WRONG!");
         } else {
            if (x < y) System.out.println("Correct!");
            else System.out.println("WRONG!");
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
}
