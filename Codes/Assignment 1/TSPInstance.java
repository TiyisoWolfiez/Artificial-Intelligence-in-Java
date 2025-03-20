import java.io.*;
import java.util.*;

class TSPInstance {
    private List<int[]> cities;
    private double[][] distanceMatrix;

    public TSPInstance(String filename) throws IOException {
        cities = new ArrayList<>();
        parseFile(filename);
        computeDistanceMatrix();
    }

    private void parseFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        boolean readingNodes = false;

        while ((line = br.readLine()) != null) {
            if (line.startsWith("NODE_COORD_SECTION")) {
                readingNodes = true;
                continue;
            }
            if (line.startsWith("EOF")) break;

            if (readingNodes) {
                String[] parts = line.trim().split("\\s+");
                int id = Integer.parseInt(parts[0]);
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                cities.add(new int[]{id, x, y});
            }
        }
        br.close();
    }

    private void computeDistanceMatrix() {
        int n = cities.size();
        distanceMatrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    distanceMatrix[i][j] = calculateDistance(cities.get(i), cities.get(j));
                } else {
                    distanceMatrix[i][j] = 0;
                }
            }
        }
    }

    public double getDistance(int city1, int city2) {
        return distanceMatrix[city1 - 1][city2 - 1];
    }

    private double calculateDistance(int[] city1, int[] city2) {
        return Math.sqrt(Math.pow(city1[1] - city2[1], 2) + Math.pow(city1[2] - city2[2], 2));
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
        return cities;
    }

    public void printCities() {
        for (int[] city : cities) {
            System.out.println("City " + city[0] + ": (" + city[1] + ", " + city[2] + ")");
        }
    }
}
