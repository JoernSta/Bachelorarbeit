import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KmeansClusterer {
   public final int numClusters;
    public static  List<Cluster> clusters;
    public final int maxIterations;
    public static boolean hasChanged;
    public boolean hasAdded;

    public KmeansClusterer(int numClusters, int maxIterations) {
        this.numClusters = numClusters;
        this.maxIterations = maxIterations;
        this.clusters = new ArrayList<>(numClusters);
    }

    public void cluster(List<Point> points) {
    	 boolean pointAdded = false;
    	    for (Point point : points) {
    	        Cluster nearestCluster = null;
    	        double shortestDistance = Double.MAX_VALUE;

    	        for (Cluster cluster : clusters) {
    	            double distance = cluster.getCentroid().distance(point);
    	            if (distance < shortestDistance) {
    	                shortestDistance = distance;
    	                nearestCluster = cluster;
    	            }
    	        }

    	        if (nearestCluster != null) {
    	            nearestCluster.addPoint(point);
    	            nearestCluster.updateCentroid();
    	            pointAdded = true;
    	        }
    	    }

    	    hasChanged |= pointAdded;
    	}


    public List<Cluster> getClusters() {
        return clusters;
    }

    public void initializeClusters(List<Point> points) {
        if (points.size() < numClusters) {
            throw new IllegalArgumentException("Number of clusters cannot be greater than the number of points.");
        }

        Random random = new Random();
        for (int i = 0; i < numClusters; i++) {
            Point randomPoint = points.get(random.nextInt(points.size()));
            Cluster cluster = new Cluster(randomPoint,i);
            clusters.add(cluster);
        }
    }
    
    void calculateCluster(List<Point> points) {
    	for (Cluster cluster : clusters) {
            List<Point> clusterPoints = cluster.getPoints();
            Point centroid = cluster.getCentroid();
            boolean hasChanged = false;
            Cluster nearestCluster = null; // nearestCluster deklarieren

            for (Point point : clusterPoints) {
                double shortestDistance = Double.MAX_VALUE;

                for (Cluster otherCluster : clusters) {
                    if (otherCluster.equals(cluster)) {
                        continue;
                    }
                    double distance = otherCluster.getCentroid().distance(point);
                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        nearestCluster = otherCluster; // nearestCluster zuweisen
                    }
                }

                if (nearestCluster != null) {
                    nearestCluster.addPoint(point);
                    hasChanged = true;
                }
            }

            if (hasChanged) {
                clusterPoints.clear();
                clusterPoints.addAll(nearestCluster.getPoints());
                nearestCluster.getPoints().clear();
                nearestCluster.updateCentroid();
                cluster.updateCentroid();
            }
        }
    }
}