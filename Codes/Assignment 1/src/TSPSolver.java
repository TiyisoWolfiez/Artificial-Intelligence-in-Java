import java.util.*;

class TSPSolver {
    private TSPInstance tsp;
    private Random rand;
    private long seed;

    public TSPSolver(TSPInstance tsp, long seed) {
        this.tsp = tsp;
        this.seed = seed;
        this.rand = new Random(seed);
        // System.out.println("Seed Value: " + seed);
    }

    // ==================================================== Simulated Annealing ====================================================

    public List<Integer> simulatedAnnealing(double initialTemp, double coolingRate, int maxIterations) {
        List<Integer> currentTour = generateInitialTour();
        double currentCost = calculateCost(currentTour);
        List<Integer> bestTour = new ArrayList<>(currentTour);
        double bestCost = currentCost;
        double temperature = initialTemp;

        int iter = 0;
        while (iter < maxIterations && temperature >= 1e-3) {
            List<Integer> newTour = new ArrayList<>(currentTour);
            swapCities(newTour);
            double newCost = calculateCost(newTour);

            if (acceptMove(currentCost, newCost, temperature)) {
                currentTour = new ArrayList<>(newTour);
                currentCost = newCost;
                if (newCost < bestCost) {
                    bestTour = new ArrayList<>(newTour);
                    bestCost = newCost;
                }
            }
            temperature *= coolingRate;
            iter++;
        }
        return bestTour;
    }

    private List<Integer> generateInitialTour() {
        return TSPInitialSolution.generateRandomTour(tsp.getCities().size());
    }

    private double calculateCost(List<Integer> tour) {
        return TSPInitialSolution.calculateTourCost(tour, tsp);
    }

    private void swapCities(List<Integer> tour) {
        int size = tour.size() - 2;
        int i = rand.nextInt(size) + 1;
        int j = rand.nextInt(size) + 1;
        Collections.swap(tour, i, j);
    }

    private boolean acceptMove(double oldCost, double newCost, double temperature) {
        return newCost < oldCost || rand.nextDouble() < Math.exp((oldCost - newCost) / temperature);
    }

    // ======================================================== TABU SEARCH ========================================================

    public List<Integer> tabuSearch(int maxIterations, int tabuTenure) {
        List<Integer> currentTour = generateInitialTour();
        double currentCost = calculateCost(currentTour);
        List<Integer> bestTour = new ArrayList<>(currentTour);
        double bestCost = currentCost;
        Map<String, Integer> tabuList = new HashMap<>();

        int iter = 0;
        while (iter < maxIterations) {
            List<Integer> bestNeighbor = findBestNeighbor(currentTour, tabuList, bestCost);
            if (bestNeighbor == null) break;

            currentTour = bestNeighbor;
            currentCost = calculateCost(currentTour);
            if (currentCost < bestCost) {
                bestTour = new ArrayList<>(currentTour);
                bestCost = currentCost;
            }

            tabuList.put(moveKey(currentTour), iter + tabuTenure);
            cleanTabuList(tabuList, iter);
            iter++;
        }
        return bestTour;
    }

    private List<Integer> findBestNeighbor(List<Integer> currentTour, Map<String, Integer> tabuList, double bestCost) {
        List<List<Integer>> neighbors = generateNeighbors(currentTour);
        List<Integer> bestNeighbor = null;
        double bestNeighborCost = Double.MAX_VALUE;

        for (List<Integer> neighbor : neighbors) {
            double cost = calculateCost(neighbor);
            String moveKey = moveKey(neighbor);

            if (!tabuList.containsKey(moveKey) || cost < bestCost) {
                if (cost < bestNeighborCost) {
                    bestNeighbor = neighbor;
                    bestNeighborCost = cost;
                }
            }
        }
        return bestNeighbor;
    }

    private void cleanTabuList(Map<String, Integer> tabuList, int iter) {
        tabuList.entrySet().removeIf(entry -> entry.getValue() <= iter);
    }

    private List<List<Integer>> generateNeighbors(List<Integer> tour) {
        List<List<Integer>> neighbors = new ArrayList<>();
        int size = tour.size() - 2;

        int i = 1;
        while (i < size) {
            int j = i + 1;
            while (j <= size) {
                List<Integer> newTour = new ArrayList<>(tour);
                Collections.swap(newTour, i, j);
                neighbors.add(newTour);
                j++;
            }
            i++;
        }
        return neighbors;
    }

    private String moveKey(List<Integer> tour) {
        return tour.toString();
    }
}
