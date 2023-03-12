import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StopWithPath {
    public Point stop;
    public java.util.List<Point> path;


    public StopWithPath(Point stop, List<Point> path) {
        this.stop = stop;
        this.path = path;
    }

    public List<StopWithPath> nextHops(List<Point> stops) {
        List<StopWithPath> hops = new ArrayList<>();

        for(Point s: stops) {
            if(this.path.contains(s) || s.equals(this.stop)) {
                continue;
            }
            ArrayList<Point> path = new ArrayList<>(this.path);
            path.add(this.stop);
            hops.add(new StopWithPath(s, path));
        }
        return hops;
    }


}
