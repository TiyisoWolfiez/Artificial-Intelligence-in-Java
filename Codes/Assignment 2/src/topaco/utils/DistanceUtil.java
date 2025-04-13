package topaco.utils;

import topaco.model.Node;

public class DistanceUtil {
    public static double euclidean(Node a, Node b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy)
    }
}
