package topaco.aco;

import java.util.Random;
import topaco.model.ProblemInstance;
import java.util.ArrayList;
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

    public void run(int maxIterations) {
        double bestScore = 0.0;
        List<Integer> bestRoute = null;
        double bestDistance = 0.0;
    
        for (int iter = 0; iter < maxIterations; iter++) {
            List<Ant> ants = new ArrayList<>();
    
            for (int i = 0; i < instance.getVehicleCount(); i++) {
                Ant ant = new Ant(instance, pheromones, random);
                ant.setVehicleIndex(i);
                ant.constructRoute(pheromones.getMatrix(), alpha, beta);
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
    
                if (ant.getTotalScore() > bestScore) {
                    bestScore = ant.getTotalScore();
                    bestRoute = new ArrayList<>(route);
                    bestDistance = ant.getTotalDistance();
                }
            }
    
            System.out.println("Iteration " + (iter + 1) + " best score: " + bestScore);
        }
    
        System.out.println("\n🏁 Final Best Route: " + bestRoute);
        System.out.println("Total Distance: " + bestDistance);
        System.out.println("Total Score: " + bestScore);
    }    

    public static class Result {
        public final List<Integer> route;
        public final double score;
        public final double distance;
    
        public Result(List<Integer> route, double score, double distance) {
            this.route = route;
            this.score = score;
            this.distance = distance;
        }
    }
    
    public Result runForResult(int maxIterations) {
        double bestScore = 0.0;
        List<Integer> bestRoute = null;
        double bestDistance = 0.0;
    
        for (int iter = 0; iter < maxIterations; iter++) {
            List<Ant> ants = new ArrayList<>();
    
            for (int i = 0; i < instance.getVehicleCount(); i++) {
                Ant ant = new Ant(instance, pheromones, random);
                ant.setVehicleIndex(i);
                ant.constructRoute(pheromones.getMatrix(), alpha, beta);
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
    
                if (ant.getTotalScore() > bestScore) {
                    bestScore = ant.getTotalScore();
                    bestRoute = new ArrayList<>(route);
                    bestDistance = ant.getTotalDistance();
                }
            }
        }
    
        return new Result(bestRoute, bestScore, bestDistance);
    }
}
