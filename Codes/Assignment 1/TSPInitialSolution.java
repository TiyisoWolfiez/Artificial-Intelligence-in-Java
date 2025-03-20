import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class TSPInitialSolution {
    public static List<Integer> generateRandomTour(int numCities) {
        List<Integer> tour = new ArrayList<>();
        for (int i = 2; i <= numCities; i++) { // Start from 2 since 1 is fixed as start/end
            tour.add(i);
        }
        Collections.shuffle(tour);
        tour.add(0, 1); // Ensure Node 1 is the start
        tour.add(1); // Ensure Node 1 is the end
        return tour;
    }

    public static double calculateTourCost(List<Integer> tour, TSPInstance tsp) {
        double cost = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            cost += tsp.getDistance(tour.get(i), tour.get(i + 1));
        }
        return cost;
    }

    public static void printTour(List<Integer> tour) {
        System.out.println("Initial Tour: " + tour);
    }
}