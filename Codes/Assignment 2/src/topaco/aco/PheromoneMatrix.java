package topaco.aco;

public class PheromoneMatrix {
    private final double[][] pheromones;
    private final int size;
    private final double initialPheromone = 0.1;

    public PheromoneMatrix(int size) {
        this.size = size;
        pheromones = new double[size][size];
        initialize();
    }

    private void initialize() {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                pheromones[i][j] = initialPheromone;
    }

    public double get(int i, int j) {
        return pheromones[i][j];
    }

    public double[][] getMatrix() {
        return pheromones;
    }

    public void evaporate(double evaporationRate) {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                pheromones[i][j] *= (1 - evaporationRate);
    }

    public void addPheromone(int i, int j, double amount) {
        pheromones[i][j] += amount;
        pheromones[j][i] += amount;
    }

    public int getSize() {
        return size;
    }
}
