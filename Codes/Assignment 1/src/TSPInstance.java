import java.io.*;
import java.util.*;

class TSPInstance {
    private final List<int[]> cities = new ArrayList<>();
    private double[][] distanceMatrix;

    public TSPInstance(String filename) throws IOException {
        parseFile(filename);
        computeDistanceMatrix();
    }

    private void parseFile(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean readingNodes = false;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("NODE_COORD_SECTION")) {
                    readingNodes = true;
                    continue;
                }
                if (line.startsWith("EOF")) break;

                if (readingNodes) addCity(line.trim().split("\\s+"));
            }
        }
    }

    private void addCity(String[] parts) {
        if (parts.length >= 3) {
            cities.add(new int[]{
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2])
            });
        }
    }

    private void computeDistanceMatrix() {
        int n = cities.size();
        distanceMatrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double dist = calculateDistance(cities.get(i), cities.get(j));
                distanceMatrix[i][j] = distanceMatrix[j][i] = dist;
            }
        }
    }

    public double getDistance(int city1, int city2) {
        return distanceMatrix[city1 - 1][city2 - 1];
    }

    private double calculateDistance(int[] city1, int[] city2) {
        int dx = city1[1] - city2[1];
        int dy = city1[2] - city2[2];
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double[][] getDistanceMatrix() {
        return distanceMatrix;
    }

    public void printDistanceMatrix() {
        System.out.println("Distance Matrix:");
        for (double[] row : distanceMatrix) {
            for (double d : row) {
                System.out.printf("%.2f ", d);
            }
            System.out.println();
        }
    }

    public List<int[]> getCities() {
        return Collections.unmodifiableList(cities);
    }

    public void printCities() {
        for (int[] city : cities) {
            System.out.printf("City %d: (%d, %d)%n", city[0], city[1], city[2]);
        }
    }
}
