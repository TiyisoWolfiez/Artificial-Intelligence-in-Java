package topaco.model;

import java.io.*;
import java.util.*;

public class ProblemInstance {
    private int vehicleCount;
    private double tmax;
    private List<Node> nodes;

    public ProblemInstance(String filePath) {
        nodes = new ArrayList<>();
        readInstance(filePath);
    }

    private void readInstance(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            int n = 0;
            while (scanner.hasNext()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("n")) {
                    n = Integer.parseInt(line.split("\\s+")[1]);
                } else if (line.startsWith("m")) {
                    vehicleCount = Integer.parseInt(line.split("\\s+")[1]);
                } else if (line.startsWith("tmax")) {
                    tmax = Double.parseDouble(line.split("\\s+")[1]);
                } else if (!line.isEmpty() && Character.isDigit(line.charAt(0))) {
                    String[] parts = line.split("\\s+");
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);
                    int score = Integer.parseInt(parts[2]);
                    nodes.add(new Node(x, y, score));
                }
            }

            if (nodes.size() != n) {
                System.err.println("Warning: node count mismatch (expected " + n + ", got " + nodes.size() + ")");
            }

        } catch (Exception e) {
            System.err.println("Failed to read file: " + e.getMessage());
        }
    }

    public int getVehicleCount() {
        return vehicleCount;
    }

    public double getTmax() {
        return tmax;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public Node getNode(int index) {
        return nodes.get(index);
    }

    public int getScore(int index) {
        return nodes.get(index).getScore();
    }
}
