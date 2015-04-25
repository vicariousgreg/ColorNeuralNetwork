import java.util.ArrayList;

public class Main {
   public static void main(String[] args) {
      Network network = new Network(new int[] {1, 1});

      ArrayList<TestCase> tests = new ArrayList<TestCase>();
      tests.add(new TestCase(new double[] { 0.5 }, new double[] { 0.5 }));
      tests.add(new TestCase(new double[] { 0.1 }, new double[] { 0.1 }));
      tests.add(new TestCase(new double[] { 1.0 }, new double[] { 1.0 }));
      System.out.println("Network fitness: " + network.calcFitness(tests));
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
