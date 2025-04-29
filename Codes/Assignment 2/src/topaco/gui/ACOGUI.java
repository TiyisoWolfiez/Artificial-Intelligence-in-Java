package topaco.gui;

import topaco.aco.ACO;
import topaco.model.ProblemInstance;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class ACOGUI extends JFrame {
    private JTextField folderField, seedField, alphaField, betaField, evapField, qField, iterField, runsField;
    private JTextArea resultArea;
    private DrawPanel drawPanel;
    private JComboBox<String> fileSelector, vehicleSelector;

    private Map<String, Map<Integer, ACO.ResultPerVehicle>> allBestResults = new HashMap<>();
    private Map<String, ProblemInstance> allInstances = new HashMap<>();
    private Map<String, Double> fileRuntimes = new HashMap<>();

    public ACOGUI() {
        setTitle("ACO Team Orienteering Problem Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(10, 2));

        folderField = new JTextField("../Data_TOP");
        seedField = new JTextField("42");
        alphaField = new JTextField("1.0");
        betaField = new JTextField("5.0");
        evapField = new JTextField("0.5");
        qField = new JTextField("100.0");
        iterField = new JTextField("100");
        runsField = new JTextField("10");

        inputPanel.add(new JLabel("Folder Path:")); inputPanel.add(folderField);
        inputPanel.add(new JLabel("Seed:")); inputPanel.add(seedField);
        inputPanel.add(new JLabel("Alpha:")); inputPanel.add(alphaField);
        inputPanel.add(new JLabel("Beta:")); inputPanel.add(betaField);
        inputPanel.add(new JLabel("Evaporation Rate:")); inputPanel.add(evapField);
        inputPanel.add(new JLabel("Pheromone Deposit (Q):")); inputPanel.add(qField);
        inputPanel.add(new JLabel("Iterations:")); inputPanel.add(iterField);
        inputPanel.add(new JLabel("Runs per File:")); inputPanel.add(runsField);

        JButton runButton = new JButton("Run ACO");
        runButton.addActionListener(this::runACO);

        fileSelector = new JComboBox<>();
        fileSelector.addActionListener(e -> showSelectedFileResults());

        vehicleSelector = new JComboBox<>();
        vehicleSelector.setEnabled(false);
        vehicleSelector.addActionListener(e -> showSelectedVehicleResults());

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel selectorPanel = new JPanel(new FlowLayout());
        selectorPanel.add(new JLabel("Select File:"));
        selectorPanel.add(fileSelector);
        selectorPanel.add(new JLabel("Select Vehicle:"));
        selectorPanel.add(vehicleSelector);

        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(runButton, BorderLayout.SOUTH);
        topPanel.add(selectorPanel, BorderLayout.NORTH);

        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);

        drawPanel = new DrawPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(resultArea), drawPanel);
        splitPane.setDividerLocation(500);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void runACO(ActionEvent e) {
        try {
            fileSelector.removeAllItems();
            vehicleSelector.removeAllItems();
            allBestResults.clear();
            allInstances.clear();

            String folderPath = folderField.getText().trim();
            long seed = Long.parseLong(seedField.getText().trim());
            System.out.println("Seed value: " + seed);
            double alpha = Double.parseDouble(alphaField.getText().trim());
            double beta = Double.parseDouble(betaField.getText().trim());
            double evap = Double.parseDouble(evapField.getText().trim());
            double q = Double.parseDouble(qField.getText().trim());
            int iterations = Integer.parseInt(iterField.getText().trim());
            int runs = Integer.parseInt(runsField.getText().trim());

            File folder = new File(folderPath);
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));

            if (files == null || files.length == 0) {
                resultArea.setText("No .txt files found in folder.");
                return;
            }

            for (File file : files) {
                String fileName = file.getName();
                Map<Integer, ACO.ResultPerVehicle> bestResults = new HashMap<>();
                ProblemInstance bestInstance = null;

                long startFileTime = System.currentTimeMillis();

                for (int run = 0; run < runs; run++) {
                    ProblemInstance instance = new ProblemInstance(file.getPath());
                    Random random = new Random(seed);
                    ACO aco = new ACO(instance, random, alpha, beta, evap, q);
                    List<ACO.ResultPerVehicle> results = aco.runForResult(iterations);

                    for (ACO.ResultPerVehicle res : results) {
                        ACO.ResultPerVehicle current = bestResults.get(res.vehicleIndex);
                        if (current == null || res.score > current.score ||
                           (res.score == current.score && res.distance < current.distance)) {
                            bestResults.put(res.vehicleIndex, res);
                            bestInstance = instance; 
                        }
                    }
                }

                long endFileTime = System.currentTimeMillis(); // <<< end timing this file
                double fileRuntimeSeconds = (endFileTime - startFileTime) / 1000.0;

                fileRuntimes.put(fileName, fileRuntimeSeconds);

                allBestResults.put(fileName, bestResults);
                allInstances.put(fileName, bestInstance);
                fileSelector.addItem(fileName);
            }

            
            if (fileSelector.getItemCount() > 0) {
                fileSelector.setSelectedIndex(0);
                showSelectedFileResults();
            }

            vehicleSelector.setEnabled(true);
            exportResultsToCSV(seed);

        } catch (Exception ex) {
            ex.printStackTrace();
            resultArea.setText("Error: " + ex.getMessage());
        } 
    }

    private void exportResultsToCSV(long seed) {
        try {
            File csvOutputFile = new File("results.csv");
            try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
                // Write header
                pw.println("File,Seed,Vehicle,Solution,BestScore,BestDistance,RuntimeSeconds");
    
                for (String fileName : allBestResults.keySet()) {
                    Map<Integer, ACO.ResultPerVehicle> bestResults = allBestResults.get(fileName);
                    Double runtime = fileRuntimes.get(fileName);
    
                    for (ACO.ResultPerVehicle res : bestResults.values()) {
                        pw.printf(
                            "%s,%d,%d,\"%s\",%.2f,%.2f,%.2f%n",
                            fileName,
                            seed, // Pass the seed here
                            res.vehicleIndex + 1,
                            res.route != null ? res.route.toString() : "",
                            res.score,
                            res.distance,
                            runtime != null ? runtime : 0.0
                        );
                    }
                }
            }
            System.out.println("Results exported to results.csv successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    

    private void showSelectedFileResults() {
        String selectedFile = (String) fileSelector.getSelectedItem();
        if (selectedFile == null) return;
    
        Map<Integer, ACO.ResultPerVehicle> bestResults = allBestResults.get(selectedFile);
        ProblemInstance instance = allInstances.get(selectedFile);
    
        if (bestResults == null || instance == null) return;
    
        vehicleSelector.removeAllItems();
        for (Integer vehicleIndex : bestResults.keySet()) {
            vehicleSelector.addItem("Vehicle " + (vehicleIndex + 1));
        }
    
        ACO.ResultPerVehicle firstVehicle = bestResults.get(0);
        updateResults(selectedFile, firstVehicle, instance);
    }
    

    private void showSelectedVehicleResults() {
        String selectedFile = (String) fileSelector.getSelectedItem();
        if (selectedFile == null) return;
    
        Map<Integer, ACO.ResultPerVehicle> bestResults = allBestResults.get(selectedFile);
        ProblemInstance instance = allInstances.get(selectedFile);
        
        if (bestResults == null || instance == null) return;
    
        int selectedVehicleIndex = vehicleSelector.getSelectedIndex();
    
        if (selectedVehicleIndex >= 0) {
            ACO.ResultPerVehicle selectedVehicle = bestResults.get(selectedVehicleIndex);
            updateResults(selectedFile, selectedVehicle, instance);
        }
    }
    

    private void updateResults(String selectedFile, ACO.ResultPerVehicle selectedVehicle, ProblemInstance instance) {
        StringBuilder sb = new StringBuilder();
        sb.append("Running on: ").append(selectedFile).append("\n\n");
        sb.append("Vehicle ").append(selectedVehicle.vehicleIndex + 1).append(":\n");
        sb.append("  Best Score: ").append(selectedVehicle.score).append("\n");
        sb.append("  Best Distance: ").append(selectedVehicle.distance).append("\n");
        sb.append("  Best Route: ").append(selectedVehicle.route).append("\n\n");

        Double runtime = fileRuntimes.get(selectedFile);
        if (runtime != null) {
            sb.append(String.format("Runtime for file: %.2f seconds\n", runtime));
        }

        resultArea.setText(sb.toString());

        drawPanel.setData(instance, selectedVehicle.route);
    }

    private static class DrawPanel extends JPanel {
        private ProblemInstance instance;
        private List<Integer> route;

        public void setData(ProblemInstance instance, List<Integer> route) {
            this.instance = instance;
            this.route = route;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (instance == null || route == null) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padding = 40;
            double scaleX = (getWidth() - 2 * padding) / getMaxX();
            double scaleY = (getHeight() - 2 * padding) / getMaxY();

            for (int i = 0; i < route.size() - 1; i++) {
                int from = route.get(i);
                int to = route.get(i + 1);

                int x1 = (int) (instance.getNode(from).getX() * scaleX) + padding;
                int y1 = (int) (instance.getNode(from).getY() * scaleY) + padding;
                int x2 = (int) (instance.getNode(to).getX() * scaleX) + padding;
                int y2 = (int) (instance.getNode(to).getY() * scaleY) + padding;

                g2.setColor(Color.BLACK);
                g2.drawLine(x1, y1, x2, y2);
            }

            for (int i = 0; i < instance.getNodes().size(); i++) {
                int x = (int) (instance.getNode(i).getX() * scaleX) + padding;
                int y = (int) (instance.getNode(i).getY() * scaleY) + padding;
                if (i == 0) g2.setColor(Color.RED); // Depot
                else g2.setColor(Color.BLUE);
                g2.fillOval(x - 5, y - 5, 10, 10);
            }
        }

        private double getMaxX() {
            return instance.getNodes().stream().mapToDouble(n -> n.getX()).max().orElse(1);
        }

        private double getMaxY() {
            return instance.getNodes().stream().mapToDouble(n -> n.getY()).max().orElse(1);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ACOGUI::new);
    }
}
