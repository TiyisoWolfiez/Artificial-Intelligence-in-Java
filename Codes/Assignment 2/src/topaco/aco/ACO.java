package topaco.aco;

import java.util.Random;
import java.util.Set;

import topaco.model.ProblemInstance;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ACO {
    private final ProblemInstance instance;
    private final PheromoneMatrix pheromones;
    private final Random random;
    private final double alpha;
    private final double beta;
    private final double evaporationRate;
    private final double Q;

    public ACO(ProblemInstance instance, Random random, double alpha, double beta, double evaporationRate, double Q) {
        this.alpha = alpha;
        this.beta = beta;
        this.evaporationRate = evaporationRate;
        this.Q = Q;
        this.instance = instance;
        this.pheromones = new PheromoneMatrix(instance.getNodes().size());
        this.random = random;
    }

    public static class ResultPerVehicle {
        public final int vehicleIndex;
        public final List<Integer> route;
        public final double score;
        public final double distance;

        public ResultPerVehicle(int vehicleIndex, List<Integer> route, double score, double distance) {
            this.vehicleIndex = vehicleIndex;
            this.route = route;
            this.score = score;
            this.distance = distance;
        }
    }

    public List<ResultPerVehicle> runForResult(int maxIterations) {
    int vehicleCount = instance.getVehicleCount();
    List<ResultPerVehicle> bestResults = new ArrayList<>();

    Set<Integer> visitedGlobal = new HashSet<>();

    for (int v = 0; v < vehicleCount; v++) {
        bestResults.add(new ResultPerVehicle(v, new ArrayList<>(), 0.0, Double.MAX_VALUE));
    }

    for (int iter = 0; iter < maxIterations; iter++) {
        List<Ant> ants = new ArrayList<>();

        for (int i = 0; i < vehicleCount; i++) {
            Ant ant = new Ant(instance, pheromones, random);
            ant.setVehicleIndex(i);
            ant.constructRoute(pheromones.getMatrix(), alpha, beta, visitedGlobal);
            visitedGlobal.addAll(ant.getVisitedNodes());
            ants.add(ant);
        }

        pheromones.evaporate(evaporationRate);

        for (Ant ant : ants) {
            List<Integer> route = ant.getVisitedNodes();
            double contribution = Q / ant.getTotalDistance();
            for (int i = 0; i < route.size() - 1; i++) {
                int from = route.get(i);
                int to = route.get(i + 1);
                pheromones.addPheromone(from, to, contribution);
            }

            ResultPerVehicle currentBest = bestResults.get(ant.getVehicleIndex());
            // Update best results based on the total score and distance
            if (ant.getTotalScore() > currentBest.score || 
               (ant.getTotalScore() == currentBest.score && ant.getTotalDistance() < currentBest.distance)) {
                bestResults.set(ant.getVehicleIndex(), 
                    new ResultPerVehicle(ant.getVehicleIndex(), new ArrayList<>(route), ant.getTotalScore(), ant.getTotalDistance()));
            }
        }
    }

    return bestResults;
}
   
}
