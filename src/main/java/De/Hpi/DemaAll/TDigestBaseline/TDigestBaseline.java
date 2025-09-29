package De.Hpi.DemaAll.TDigestBaseline;

import com.tdunning.math.stats.TDigest;

import java.util.*;

public class TDigestBaseline {
    public static void main(String[] args) {
        // Variables to accumulate the error values
        double totalMAE = 0;
        double totalRMSE = 0;
        int numRuns = 100;

        // Run t-Digest 100 times and calculate the errors
        for (int run = 0; run < numRuns; run++) {
            Random random = new Random();
            List<Integer> data = new ArrayList<>();
            for (float i = 0; i < 10000; i++) {
                data.add(random.nextInt(10000000) + 1);
            }

            // Sort data for accurate quantile computation
            Collections.sort(data);
            // Compute real quantiles
            double real50th = getRealQuantile(data, 0.5);

            // t-Digest initialization
            TDigest tDigest = TDigest.createDigest(100);
            for (int value : data) {
                tDigest.add(value);
            }
            // t-Digest quantiles
            double t50th = tDigest.quantile(0.5);

            // Calculate the MAE and RMSE for this run
            double mae = calculateMAE(real50th, t50th);
            double rmse = calculateRMSE(real50th, t50th);

            // Accumulate the error values
            totalMAE += mae;
            totalRMSE += rmse;

            System.out.printf("\nRun %d - t-Digest Quantiles:\n", run + 1);
            System.out.println("  50th Percentile: " + t50th);
            System.out.println("  MAE: " + mae + ", RMSE: " + rmse);
        }

        // Calculate and output the average MAE and RMSE over 100 runs
        double avgMAE = totalMAE / numRuns;
        double avgRMSE = Math.sqrt(totalRMSE / numRuns);

        System.out.println("\nAverage Errors over " + numRuns + " runs:");
        System.out.println("  Average MAE: " + avgMAE);
        System.out.println("  Average RMSE: " + avgRMSE);
    }

    // Compute the exact quantile from sorted data
    private static double getRealQuantile(List<Integer> sortedData, double quantile) {
        int index = (int) Math.ceil(quantile * sortedData.size()) - 1;
        return sortedData.get(Math.max(0, Math.min(index, sortedData.size() - 1)));
    }

    // Method to calculate MAE (Mean Absolute Error)
    private static double calculateMAE(double realQuantile, double estimatedQuantile) {
        return Math.abs(realQuantile - estimatedQuantile) ;
    }

    // Method to calculate RMSE (Root Mean Square Error)
    private static double calculateRMSE(double realQuantile, double estimatedQuantile) {
        double error = Math.abs(realQuantile - estimatedQuantile) ;
        return error * error;
    }
}
