package topaco.model;

public class Node {
    private final double x;
    private final double y;
    private final int score;

    public Node(double x, double y, int score) {
        this.x = x;
        this.y = y;
        this.score = score;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getScore() {
        return score;
    }
}
