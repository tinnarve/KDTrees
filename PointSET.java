package KdTrees;
/****************************************************************************
 *  Compilation:  javac PointSET.java
 *  Execution:    
 *  Dependencies:
 *  Author:
 *  Date:
 *
 *  Data structure for maintaining a set of 2-D points, 
 *    including rectangle and nearest-neighbor queries
 *
 *************************************************************************/

import java.util.Arrays;
import java.util.Set;

import edu.princeton.cs.algs4.*;

public class PointSET {
    private SET<Point2D> points;
    // construct an empty set of points
    public PointSET() {
        points = new SET<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return size() == 0;
    }

    // number of points in the set
    public int size() {
        return points.size();
    }

    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) { points.add(p); }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        return points.contains(p);
    }

    // draw all the points to standard draw
    public void draw() {
        for (Point2D p: points)
            StdDraw.point(p.x(), p.y());
    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        SET<Point2D> range = new SET<>();
        for (Point2D p: points)
            if (rect.contains(p))
                range.add(p);
        return range;
    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        if (points.size() == 0)
            return null;
        Point2D near = null;
        for (Point2D point: points) {
            if (near == null)
                near = point;
            if (near.distanceSquaredTo(p) > point.distanceSquaredTo(p))
                near = point;
        }
        return near;
    }

    public static void main(String[] args) {
        In in = new In();
        Out out = new Out();
        int N = in.readInt(), C = in.readInt(), T = 50;
        Point2D[] queries = new Point2D[C];
        KdTree tree = new KdTree();
        out.printf("Inserting %d points into tree\n", N);
        for (int i = 0; i < N; i++) {
            tree.insert(new Point2D(in.readDouble(), in.readDouble()));
        }
        out.printf("tree.size(): %d\n", tree.size());
        out.printf("Testing `nearest` method, querying %d points\n", C);

        for (int i = 0; i < C; i++) {
            queries[i] = new Point2D(in.readDouble(), in.readDouble());
            out.printf("%s: %s\n", queries[i], tree.nearest(queries[i]));
        }
        for (int i = 0; i < T; i++) {
            for (int j = 0; j < C; j++) {
                tree.nearest(queries[j]);
            }
        }
    }

}
