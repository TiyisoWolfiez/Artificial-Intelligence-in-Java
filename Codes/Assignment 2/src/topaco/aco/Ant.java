package topaco.aco;

import topaco.model.Node;
import topaco.model.ProblemInstance;
import topaco.utils.DistanceUtil;

import java.util.*;

public class Ant {
    private final ProblemInstance instance;
    private final PheromoneMatrix pheromones;
    private final Random random;
    private final List<Integer> visitedNodes;
    private final Set<Integer> visitedSet;
    private int vehicleIndex;
    private double totalDistance;
    private double totalScore;

    public Ant(ProblemInstance instance, PheromoneMatrix pheromones, Random random) {
        this.instance = instance;
        this.pheromones = pheromones;
        this.random = random;
        this.vehicleIndex = 0;
        this.visitedNodes = new ArrayList<>();
        this.visitedSet = new HashSet<>();
        this.totalDistance = 0.0;
        this.totalScore = 0.0;
    }

    public void constructRoute(double[][] pheromones, double alpha, double beta, Set<Integer> visitedGlobal) {
        int n = instance.getNodes().size();
        int current = 0; // start at depot
        visitedNodes.add(current);
        visitedSet.add(current);
        visitedGlobal.add(current);

        while (true) {
            int next = selectNextNode(current, pheromones, alpha, beta, visitedGlobal);
            if (next == -1) break;
            double dist = DistanceUtil.euclidean(instance.getNode(current), instance.getNode(next));
            if (totalDistance + dist + getReturnDistance(next) > instance.getTmax()) break;
    
            totalDistance += dist;
            totalScore += instance.getNode(next).getScore();
    
            visitedNodes.add(next);
            visitedSet.add(next);
            visitedGlobal.add(next);
    
            current = next;
        }
    
        totalDistance += getReturnDistance(current);
        visitedNodes.add(0);
    }

    private double getReturnDistance(int from) {
        return DistanceUtil.euclidean(instance.getNode(from), instance.getNode(0));
    }

    private int selectNextNode(int current, double[][] pheromones, double alpha, double beta, Set<Integer> visitedGlobal) {
        List<Integer> candidates = new ArrayList<>();
        for (int i = 1; i < instance.getNodes().size(); i++) {
            if (!visitedSet.contains(i) && !visitedGlobal.contains(i)) {
                candidates.add(i);
            }
        }
    
        if (candidates.isEmpty()) return -1;
    
        double[] probabilities = new double[candidates.size()];
        double sum = 0.0;
    
        for (int i = 0; i < candidates.size(); i++) {
            int j = candidates.get(i);
            double tau = Math.pow(pheromones[current][j], alpha);
            double eta = Math.pow(getHeuristic(current, j), beta);
            probabilities[i] = tau * eta;
            sum += probabilities[i];
        }
    
        double rand = random.nextDouble() * sum;
        double cumulative = 0.0;
        for (int i = 0; i < candidates.size(); i++) {
            cumulative += probabilities[i];
            if (rand <= cumulative) {
                return candidates.get(i);
            }
        }
    
        return candidates.get(candidates.size() - 1);
    }
    

    private double getHeuristic(int from, int to) {
        Node node = instance.getNode(to);
        double score = node.getScore();
        double cost = DistanceUtil.euclidean(instance.getNode(from), node);
        return score / cost;
    }

    public List<Integer> getVisitedNodes() {
        return visitedNodes;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public int getVehicleIndex() {
        return vehicleIndex;
    }

    public void setVehicleIndex(int vehicleIndex) {
        this.vehicleIndex = vehicleIndex;
    }
    
}
