package topaco;

import topaco.aco.ACO;
import topaco.model.ProblemInstance;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter seed value: ");
        long seed = Long.parseLong(scanner.nextLine());

        System.out.print("Enter folder path containing problem files: ");
        String folderPath = scanner.nextLine();

        double alpha = promptDouble(scanner, "Enter alpha (default 1.0, range 0-5): ", 1.0);
        double beta = promptDouble(scanner, "Enter beta (default 5.0, range 0-10): ", 5.0);
        double evaporationRate = promptDouble(scanner, "Enter evaporation rate (default 0.5, range 0-1): ", 0.5);
        double Q = promptDouble(scanner, "Enter Q (default 100.0): ", 100.0);
        int iterations = promptInt(scanner, "Enter number of iterations per run (default 100): ", 100);
        int runsPerFile = promptInt(scanner, "Enter number of runs per file (default 10): ", 10);

        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));

        if (files == null || files.length == 0) {
            System.out.println("⚠️ No .txt files found in folder.");
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("results.csv"))) {
            writer.println("Timestamp,File,Seed,BestScore,BestDistance,BestRoute,Duration(ms)");

            for (File file : files) {
                System.out.println("\nRunning on: " + file.getName());

                double bestScore = 0;
                double bestDistance = Double.MAX_VALUE;
                List<Integer> bestRoute = null;

                long startTime = System.nanoTime();

                for (int run = 0; run < runsPerFile; run++) {
                    ProblemInstance instance = new ProblemInstance(file.getPath());
                    Random seededRandom = new Random(seed);
                    ACO aco = new ACO(instance, seededRandom, alpha, beta, evaporationRate, Q);

                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    PrintStream originalOut = System.out;
                    System.setOut(new PrintStream(outStream)); // Silence intermediate output

                    aco.run(iterations);

                    System.setOut(originalOut); // Restore output
                    String output = outStream.toString();

                    // Extract final values from the last printed lines
                    double runScore = extractDouble(output, "Total Score: ");
                    double runDistance = extractDouble(output, "Total Distance: ");
                    List<Integer> runRoute = extractRoute(output, "Final Best Route: ");

                    if (runScore > bestScore || (runScore == bestScore && runDistance < bestDistance)) {
                        bestScore = runScore;
                        bestDistance = runDistance;
                        bestRoute = runRoute;
                    }
                }

                long endTime = System.nanoTime();
                double durationMs = (endTime - startTime) / 1_000_000.0;


                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                writer.printf("%s,%s,%d,%.2f,%.2f,\"%s\",%.2f%n",
                    timestamp, file.getName(), seed, bestScore, bestDistance, bestRoute, durationMs);

            }

            System.out.println("\n Batch processing complete. Results saved to results.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double promptDouble(Scanner scanner, String prompt, double defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine();
        if (input.trim().isEmpty()) return defaultValue;
        return Double.parseDouble(input);
    }

    private static int promptInt(Scanner scanner, String prompt, int defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine();
        if (input.trim().isEmpty()) return defaultValue;
        return Integer.parseInt(input);
    }

    private static double extractDouble(String output, String key) {
        int idx = output.lastIndexOf(key);
        if (idx == -1) return 0;
        String line = output.substring(idx + key.length()).split("\n")[0].trim();
        return Double.parseDouble(line);
    }

    private static List<Integer> extractRoute(String output, String key) {
        int idx = output.lastIndexOf(key);
        if (idx == -1) return List.of();
        String raw = output.substring(idx + key.length()).split("\n")[0].trim();
        raw = raw.replaceAll("[\\[\\]\\s]", "");
        List<Integer> route = new ArrayList<>();
        for (String s : raw.split(",")) {
            if (!s.isEmpty()) route.add(Integer.parseInt(s));
        }
        return route;
    }
}
