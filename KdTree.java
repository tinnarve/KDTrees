package KdTrees;
/*************************************************************************
 *************************************************************************/

import java.util.Arrays;

import edu.princeton.cs.algs4.*;

public class KdTree {
    private static final double xMin = 0.0;
    private static final double xMax = 1.0;
    private static final double yMin = 0.0;
    private static final double yMax = 1.0;

    private Node root;
    private int size;

    private static class Node {
        private Point2D p;
        private RectHV rect;
        private Node lb; // left/bottom subtree
        private Node rt; // right/top subtree

        public Node(Point2D p, RectHV rect) {
            this.p = p;
            this.rect = rect;
        }
    }

    private Node insert(Node node, Point2D point, double xMin, double yMin, double xMax, double yMax, boolean vertical) {
        // Insert point into an empty location
        if (node == null) {
            size++;
            RectHV rect = new RectHV(xMin, yMin, xMax, yMax);
            return new Node(point, rect);
        }
        // If the point already exists return it
        if (node.p.x() == point.x() && node.p.y() == point.y()) return node;

        // If the point doesn't exist look recursively for the place where it belongs.
        if (vertical) {
            // If the current node is vertical compare x-coordinates.
            double compareX = point.x() - node.p.x();
            if (compareX < 0) node.lb = insert(node.lb, point, xMin, yMin, node.p.x(), yMax, false);
            else node.rt = insert(node.rt, point, node.p.x(), yMin, xMax, yMax, false);
        }
        else {
            // If the current node is vertical compare y-coordinates.
            double compareY = point.y() - node.p.y();
            if (compareY < 0) node.lb = insert(node.lb, point, xMin, yMin, node.p.y(), yMax, true);
            else node.rt = insert(node.rt, point, node.p.y(), yMin, xMax, yMax, true);
        }

        return node;
    }

    private boolean contains(Node node, Point2D point, boolean vertical) {
        // Node not found, return false
        if (node == null) return false;
        // Node found, return true
        else if (node.p.x() == point.x() && node.p.y() == point.y()) return true;
        // Recursive traversal through tree
        else if (vertical && point.x() < node.p.x() || !vertical && point.y() < node.p.y()) {
            // move to left/bottom subtree of node if the point is smaller than the node.point
            return contains(node.lb, point, !vertical);
        }
        // if the point is bigger move to the right/top subtree
        else return contains(node.rt, point, !vertical);
    }

    private void draw(Node node, boolean vertical) {
        if (node == null) return;
        // Draw current node
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius();
        node.p.draw();
        // Draw partition line
        StdDraw.setPenRadius();
        if (vertical) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.p.x(), node.rect.ymin(), node.p.x(), node.rect.ymax());
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.p.y());
        }
        // Recursively traverse tree
        draw(node.rt, !vertical);
        draw(node.lb, !vertical);
    }

    private void range(Node node, RectHV rect, SET<Point2D> set) {
        if (node == null) return;
        //
        if (rect.contains(node.p)) {
            set.add(node.p);
        }
        //
        if (rect.intersects(node.rect)) {
            range(node.rt, rect, set);
            range(node.lb, rect, set);
        }
    }

    private Point2D nearest(Node node, Point2D point, Point2D nearestPoint, boolean vertical) {
        // No more points to go through, return nearestPoint
        if (node == null) return nearestPoint;
        // Update nearestPoint if the current point is closer than the one previously found
        if (node.p.distanceSquaredTo(point) < nearestPoint.distanceSquaredTo(point)) nearestPoint = node.p;
        // Recursively traverse the tree to search for a closer point
        if (node.rect.distanceSquaredTo(point) < nearestPoint.distanceSquaredTo(point)) {
            if ((vertical && point.x() < node.p.x()) || (!vertical && point.y() < node.p.y())) {
                // Checks left/bottom subtree first if point is smaller than node.p
                nearestPoint = nearest(node.lb, point, nearestPoint, !vertical);
                nearestPoint = nearest(node.rt, point, nearestPoint, !vertical);
            }
            else {
                // Checks right/top subtree first if point is bigger than node.p
                nearestPoint = nearest(node.rt, point, nearestPoint, !vertical);
                nearestPoint = nearest(node.lb, point, nearestPoint, !vertical);
            }
        }
        return nearestPoint;
    }

    // construct an empty set of points
    public KdTree() {
        root = null;
        size = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
        root = insert(root, p, xMin, yMin, xMax, yMax, true);
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        return contains(root, p, true);
    }

    // draw all the points to standard draw
    public void draw() {
        draw(root, true);
    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        SET<Point2D> set = new SET<>();
        range(root, rect, set);
        return set;
    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        if (root == null) return null;
        return nearest(root, p, root.p, true);
    }

    /*******************************************************************************
     * Test client
     ******************************************************************************/
    public static void main(String[] args) {
        In in = new In();
        Out out = new Out();
        int nrOfRectangles = in.readInt();
        int nrOfPointsCont = in.readInt();
        int nrOfPointsNear = in.readInt();
        RectHV[] rectangles = new RectHV[nrOfRectangles];
        Point2D[] pointsCont = new Point2D[nrOfPointsCont];
        Point2D[] pointsNear = new Point2D[nrOfPointsNear];
        for (int i = 0; i < nrOfRectangles; i++) {
            rectangles[i] = new RectHV(in.readDouble(), in.readDouble(),
                    in.readDouble(), in.readDouble());
        }
        for (int i = 0; i < nrOfPointsCont; i++) {
            pointsCont[i] = new Point2D(in.readDouble(), in.readDouble());
        }
        for (int i = 0; i < nrOfPointsNear; i++) {
            pointsNear[i] = new Point2D(in.readDouble(), in.readDouble());
        }
        KdTree set = new KdTree();
        for (int i = 0; !in.isEmpty(); i++) {
            double x = in.readDouble(), y = in.readDouble();
            set.insert(new Point2D(x, y));
        }
        for (int i = 0; i < nrOfRectangles; i++) {
            // Query on rectangle i, sort the result, and print
            Iterable<Point2D> ptset = set.range(rectangles[i]);
            int ptcount = 0;
            for (Point2D p : ptset)
                ptcount++;
            Point2D[] ptarr = new Point2D[ptcount];
            int j = 0;
            for (Point2D p : ptset) {
                ptarr[j] = p;
                j++;
            }
            Arrays.sort(ptarr);
            out.println("Inside rectangle " + (i + 1) + ":");
            for (j = 0; j < ptcount; j++)
                out.println(ptarr[j]);
        }
        out.println("Contain test:");
        for (int i = 0; i < nrOfPointsCont; i++) {
            out.println((i + 1) + ": " + set.contains(pointsCont[i]));
        }

        out.println("Nearest test:");
        for (int i = 0; i < nrOfPointsNear; i++) {
            out.println((i + 1) + ": " + set.nearest(pointsNear[i]));
        }

        out.println();
    }
}
