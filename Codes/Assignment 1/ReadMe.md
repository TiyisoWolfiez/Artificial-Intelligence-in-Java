# Traveling Salesman Problem (TSP) Solver

This project implements **Simulated Annealing** and **Tabu Search** algorithms to solve the Traveling Salesman Problem (TSP). The TSP is a classic optimization problem where the goal is to find the shortest possible route that visits each city exactly once and returns to the origin city.

---

## 🔧 Setup & Compilation

### Prerequisites
- Ensure you have **Java (JDK 11 or later)** installed on your system.

### Compilation
To compile the project, navigate to the project directory and run the following command:

```sh
javac -d out src/*.java
```

### 🚀 Running Program
To run the program, navigate to the `out` directory and run the following command:

```sh
cd out
```

```sh
java Main
```
<!-- ### navigate to the `out` directory and run the following command:

```sh
cd out
``` -->

### 📦 Creating an Executable JAR
To create an executable JAR file:

```sh
jar cfe tsp_solver.jar Main -C out . -C Data .

```

### 🏃 Running the JAR
To run the program from the JAR file:
```sh
java -jar tsp_solver.jar
```

## 📝 Output
The program will print the best-found TSP route and its total cost to the console.

---------------------------------------------------- THE END, THANK YOU! --------------------------------------------------
