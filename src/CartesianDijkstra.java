import java.awt.*;
import java.util.*;
import java.util.List;

public class CartesianDijkstra {
    public static void main(String[] args) {
        List<Point> testPoints = new ArrayList<>();
        testPoints.add(new Point(1, 1));
        testPoints.add(new Point(5, 5));
        testPoints.add(new Point(-1, -5));
        testPoints.add(new Point(2, 5));

        Point startPoint = new Point(0, 0);
        Point endPoint = new Point(6, 3);

        List<Point> pat = shortestPath(startPoint, endPoint, testPoints);
        System.out.println();
    }

    public static List<Point> shortestPath(Point startPoint, Point endStop, List<Point> intermediateStops) {
        StopWithPath currentSwp = new StopWithPath(startPoint, new ArrayList<>());

        List<StopWithPath> hops = currentSwp.nextHops(intermediateStops);

        while(true) {
            HashMap<Point, StopWithPath> shortestHops = new HashMap<>();
            for (StopWithPath hop : hops) {
                List<StopWithPath> twoWayHops = hop.nextHops(intermediateStops);

                for (StopWithPath hop2 : twoWayHops) {
                    double thisDist = swpPathDist(hop2, endStop);

                    StopWithPath minSWP = shortestHops.get(hop2.stop);
                    Double minSWPDist = null;
                    if (minSWP != null) {
                        minSWPDist = swpPathDist(minSWP, endStop);
                    }

                    if (minSWPDist == null || minSWPDist > thisDist) {
                        shortestHops.put(hop2.stop, hop2);
                    }
                }
            }

            if(shortestHops.isEmpty()) {
                break;
            } else {
                hops = new ArrayList<>(shortestHops.values());
            }
        }

        StopWithPath minSwp = null;
        for(StopWithPath swp : hops) {
            double swpDist = swpPathDist(swp, endStop);
            Double minSwpDist = null;
            if(minSwp != null) {
                minSwpDist = swpPathDist(minSwp, endStop);
            }

            if(minSwpDist == null || minSwpDist > swpDist) {
                minSwp = swp;
            }
        }

        if(minSwp != null) {
            List<Point> returnPath = new ArrayList<>(minSwp.path);
            returnPath.add(minSwp.stop);
            returnPath.add(endStop);
            return returnPath;
        } else {
            return null;
        }
    }

    public static double swpPathDist(StopWithPath swp, Point end) {
        List<Point> thisPath = new ArrayList<>(swp.path);
        thisPath.add(swp.stop);
        thisPath.add(end);
        return pathDist(thisPath);
    }

    public static double pathDist(List<Point> path) {
        if(path.size() < 2) {
            return 0.0;
        }

        // Create one iterator starting from first point and one starting from second point.
        // This way we get an iterator of adjacent point pairs

        Iterator<Point> p1Iter = path.iterator();
        Iterator<Point> p2Iter = path.iterator();
        p2Iter.next();

        double dist = 0;
        while(p2Iter.hasNext()) {
            Point p1 = p1Iter.next();
            Point p2 = p2Iter.next();

            dist += p1.distance(p2);
        }

        return dist;
    }
}
