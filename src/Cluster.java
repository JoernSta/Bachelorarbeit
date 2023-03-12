import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Cluster {
    private Point centroid;
    private List<Point> points;
    private final int id;
    

    public Cluster(Point centroid,int id) {
        this.centroid = centroid;
        this.points = new ArrayList<>();
        this.id = id;
    }
    
    public int getId() {
        return id;
    }

    public Point getCentroid() {
        return centroid;
    }


    public void setCentroid(Point centroid) {
        this.centroid = centroid;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public void clear() {
        points.clear();
    }

    public void calculateCentroid() {
        if (points.isEmpty()) {
            return;
        }

        int sumX = 0;
        int sumY = 0;

        for (Point point : points) {
            sumX += point.getX();
            sumY += point.getY();
        }

        int avgX = sumX / points.size();
        int avgY = sumY / points.size();

        centroid = new Point(avgX, avgY);
    }
    
    public void updateCentroid() {
    	if (points.isEmpty()) {
            return; // keine Punkte im Cluster vorhanden, Centroid bleibt unverändert
        }
        int sumX = 0;
        int sumY = 0;
        for (Point point : points) {
            sumX += point.getX();
            sumY += point.getY();
        }
        centroid = new Point(sumX / points.size(), sumY / points.size());
    }

}
