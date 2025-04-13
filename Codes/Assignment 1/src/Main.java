import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String dataFolder ="Data/";
        String[] problemFiles = {
            dataFolder + "8.txt",
            dataFolder + "12.txt",
            dataFolder + "15.txt",
            dataFolder + "20.txt",
            dataFolder + "25.txt"
        };

        System.out.printf("%-18s %-10s %-15s %-10s %-50s %-12s\n", 
                          "Problem Instance", "Algorithm", "Seed Value", "Cost", "Best Solution", "Runtime (ms)");

        for (String file : problemFiles) {
            runExperiments(file);
        }
    }

    static void runExperiments(String file) {
        try {
            long seed = System.currentTimeMillis(); 
            TSPInstance tsp = new TSPInstance(file);

            // tsp.printCities();
            // tsp.printDistanceMatrix();
            
            double bestCostSA = Double.MAX_VALUE;
            List<Integer> bestTourSA = null;
            long bestRuntimeSA = 0;
    
            double bestCostTS = Double.MAX_VALUE;
            List<Integer> bestTourTS = null;
            long bestRuntimeTS = 0;
    
            for (int run = 0; run < 10; run++) {
                TSPSolver solverSA = new TSPSolver(tsp, seed);
                // + run
                double initialTemp = 1000;
                double coolingRate = 0.99;
                int maxIterationsSA = 10000;
    
                long startTime = System.currentTimeMillis();
                List<Integer> tourSA = solverSA.simulatedAnnealing(initialTemp, coolingRate, maxIterationsSA);
                long runtimeSA = System.currentTimeMillis() - startTime;
                double costSA = TSPInitialSolution.calculateTourCost(tourSA, tsp);
    
                if (costSA < bestCostSA) { 
                    bestCostSA = costSA;
                    bestTourSA = tourSA;
                    bestRuntimeSA = runtimeSA;
                }
    
                TSPSolver solverTS = new TSPSolver(tsp, seed);
                // + run 
                int maxIterationsTS = 500;
                int tabuTenure = 10;
    
                startTime = System.currentTimeMillis();
                List<Integer> tourTS = solverTS.tabuSearch(maxIterationsTS, tabuTenure);
                long runtimeTS = System.currentTimeMillis() - startTime;
                double costTS = TSPInitialSolution.calculateTourCost(tourTS, tsp);
    
                if (costTS < bestCostTS) {
                    bestCostTS = costTS;
                    bestTourTS = tourTS;
                    bestRuntimeTS = runtimeTS;
                }
            }

            System.out.printf("%-18s %-10s %-15d %-10.2f %-50s %-12d\n", 
                              file.replace(".txt", " Cities"), "SA", seed, bestCostSA, bestTourSA, bestRuntimeSA);
            System.out.printf("%-18s %-10s %-15d %-10.2f %-50s %-12d\n", 
                              file.replace(".txt", " Cities"), "Tabu", seed, bestCostTS, bestTourTS, bestRuntimeTS);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }       
}
