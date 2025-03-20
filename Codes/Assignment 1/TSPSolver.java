import java.util.*;
import java.util.Random;

class TSPSolver {
    private TSPInstance tsp;
    private Random rand;
    private long seed;

    public TSPSolver(TSPInstance tsp, long seed) {
        this.tsp = tsp;
        this.seed = seed;
        this.rand = new Random(seed);
        System.out.println("Seed Value: " + seed);
    }

    // ------------------------------------------ Simulated Annealing --------------------------------------------------------------

    public List<Integer> simulatedAnnealing(double initialTemp, double coolingRate, int maxIterations) {
        List<Integer> currentTour = TSPInitialSolution.generateRandomTour(tsp.getCities().size());
        double currentCost = TSPInitialSolution.calculateTourCost(currentTour, tsp);
        List<Integer> bestTour = new ArrayList<>(currentTour);
        double bestCost = currentCost;

        double temperature = initialTemp;

        for (int iter = 0; iter < maxIterations; iter++) {
            List<Integer> newTour = new ArrayList<>(currentTour);
            swapCities(newTour);

            double newCost = TSPInitialSolution.calculateTourCost(newTour, tsp);

            if (acceptMove(currentCost, newCost, temperature)) {
                currentTour = new ArrayList<>(newTour);
                currentCost = newCost;

                if (newCost < bestCost) {
                    bestTour = new ArrayList<>(newTour);
                    bestCost = newCost;
                }
            }

            temperature *= coolingRate;

            if (temperature < 1e-3) break; // Stop if temperature is too low
        }

        return bestTour;
    }

    private void swapCities(List<Integer> tour) {
        int size = tour.size() - 2; // Exclude start and end city (Node 1)
        int i = rand.nextInt(size) + 1; // Avoid first city
        int j = rand.nextInt(size) + 1;

        Collections.swap(tour, i, j);
    }

    private boolean acceptMove(double oldCost, double newCost, double temperature) {
        if (newCost < oldCost) return true;
        double probability = Math.exp((oldCost - newCost) / temperature);
        return rand.nextDouble() < probability;
    }

    // ------------------------------------------------- TABU SEARCH ---------------------------------------------------------------
    public List<Integer> tabuSearch(int maxIterations, int tabuTenure) {
        List<Integer> currentTour = TSPInitialSolution.generateRandomTour(tsp.getCities().size());
        double currentCost = TSPInitialSolution.calculateTourCost(currentTour, tsp);
        List<Integer> bestTour = new ArrayList<>(currentTour);
        double bestCost = currentCost;

        Map<String, Integer> tabuList = new HashMap<>();

        for (int iter = 0; iter < maxIterations; iter++) {
            List<List<Integer>> neighbors = generateNeighbors(currentTour);
            List<Integer> bestNeighbor = null;
            double bestNeighborCost = Double.MAX_VALUE;

            for (List<Integer> neighbor : neighbors) {
                double cost = TSPInitialSolution.calculateTourCost(neighbor, tsp);
                String moveKey = moveKey(neighbor);

                if (!tabuList.containsKey(moveKey) || cost < bestCost) { // Aspiration criterion
                    if (cost < bestNeighborCost) {
                        bestNeighbor = neighbor;
                        bestNeighborCost = cost;
                    }
                }
            }

            if (bestNeighbor == null) break;

            currentTour = bestNeighbor;
            currentCost = bestNeighborCost;

            if (currentCost < bestCost) {
                bestTour = new ArrayList<>(currentTour);
                bestCost = currentCost;
            }

            tabuList.put(moveKey(currentTour), iter + tabuTenure);

            // Remove expired tabu moves
            Iterator<Map.Entry<String, Integer>> iterator = tabuList.entrySet().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getValue() <= iter) {
                    iterator.remove();
                }
            }

        }

        return bestTour;
    }

    private List<List<Integer>> generateNeighbors(List<Integer> tour) {
        List<List<Integer>> neighbors = new ArrayList<>();
        int size = tour.size() - 2;

        for (int i = 1; i < size; i++) {
            for (int j = i + 1; j <= size; j++) {
                List<Integer> newTour = new ArrayList<>(tour);
                Collections.swap(newTour, i, j);
                neighbors.add(newTour);
            }
        }
        return neighbors;
    }

    private String moveKey(List<Integer> tour) {
        return tour.toString();
    }
}
