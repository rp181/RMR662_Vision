/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package squaredetection;

import java.awt.Polygon;
import org.concord.swing.QuickHull;
import java.awt.Point;
import java.util.LinkedList;

/**
 *
 * @author Phani
 */
public class Blob {

    private Point[] points;
    private Polygon p;
    private Polygon simplifiedPolygon = null;
    private int xmin, xmax, ymin, ymax;
    private boolean areaCalculated = false;

    public Blob(Point[] points) {
        this.points = points;
        computeConvexHull();
    }

    public Blob(Object[] points) {
        this.points = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            this.points[i] = (Point) points[i];
        }
        computeConvexHull();
    }

    public Blob(Point[] points, int nPoints) {
        this.points = new Point[nPoints];
        for (int i = 0; i < nPoints; i++) {
            this.points[i] = points[i];
        }
        computeConvexHull();
    }

    public boolean contains(Point p) {
        for (Point point : points) {
            if (point == p) {
                return true;
            }
        }
        return false;
    }

    private void computeConvexHull() {
        QuickHull qh = new QuickHull(points);
        Point[] result = qh.getHullPointsAsArray();
        int xPoints[] = new int[result.length];
        int yPoints[] = new int[result.length];
        for (int i = 0; i < result.length; i++) {
            xPoints[i] = result[i].x;
            yPoints[i] = result[i].y;
        }
        //<editor-fold defaultstate="collapsed" desc="Old">
        //
        //
        //        Point startpoint, p1, p2;
        //        startpoint = points[0];
        //        int i;
        //        for (i = 1; i < points.length; i++) {
        //            if (startpoint.x > points[i].x) {
        //                startpoint = points[i];
        //            }
        //        }
        //        xPoints[0] = startpoint.x;
        //        yPoints[0] = startpoint.y;
        //
        //        // now start a loop, searching for the next vertex after the first one.
        //        // in this code, we always search in the clockwise direction.
        //        int currentvertexindex = 0;
        //        Point currentvertex = new Point(xPoints[currentvertexindex], yPoints[currentvertexindex]);
        //        Point pointtocheck = null;
        //        boolean done;
        //        if (points.length == 1) {
        //            done = true;
        //        } else {
        //            done = false;
        //        }
        //        while (!done) {
        //            // loop through each point to see if it is the next vertex
        //            for (i = 0; i < points.length; i++) {
        //                pointtocheck = points[i];
        //                // can't test equal points because they don't form a line.
        //                if (!(pointtocheck.x == currentvertex.x && pointtocheck.y == currentvertex.y)) {
        //                    // check to see if all points are on right side of line
        //                    //   from currentvertex to pointtocheck.
        //                    if (allPointsOnRight(currentvertex, pointtocheck, points)) {
        //                        // pointtocheck is a vertex of polygon; break out.
        //                        break;
        //                    }
        //                }
        //            }
        //            // at this point, we should have found a vertex.
        //            // if i== points.length at this point, we didn't find a vertex, but that
        //            // should never happen!
        //
        //            // if the found vertex is the same as the first vertex, we have finished.
        //            // otherwise, add in the vertex and continue the loop.
        //            if (pointtocheck.x == xPoints[0] && pointtocheck.y == yPoints[0]) {
        //                done = true;
        //            } else {
        //                // add a new vertex, get ready for next loop iteration.
        //                currentvertexindex++;
        //                xPoints[currentvertexindex] = pointtocheck.x;
        //                yPoints[currentvertexindex] = pointtocheck.y;
        //                currentvertex = pointtocheck;
        //            }
        //        }
        //        System.out.println(currentvertexindex+" Points in Computed Hull");
        //p = new Polygon(xPoints, yPoints, currentvertexindex);
        //</editor-fold>
        p = new Polygon(xPoints, yPoints, result.length);
    }

    public Polygon getConvexHullPolygon() {
        return p;
    }

    public int getConvexArea() {
        double sum = 0;
        for (int i = 0; i < getConvexHullPolygon().npoints - 1; i++) {
            sum = sum + (getConvexHullPolygon().xpoints[i] * getConvexHullPolygon().ypoints[i + 1]) - (getConvexHullPolygon().ypoints[i] * getConvexHullPolygon().xpoints[i + 1]);
        }
        return Math.abs((int) (.5d * sum));
    }

    public int getBoundingWidth() {
        if (!areaCalculated) {
            getBoundingArea();
        }
        return (xmax - xmin);
    }

    public int getBoundingHeight() {
        if (!areaCalculated) {
            getBoundingArea();
        }
        return (ymax - ymin);
    }

    public int getAreaScore() {
        return (int) ((double) getConvexArea() / getBoundingArea() * 100);
    }

    public int getAspectRatioScore() {
        return (int) (100 * (double) (getBoundingWidth() / getBoundingHeight()) / 1.33d);
    }

    public Polygon getBoundingPolygon() {
        if (!areaCalculated) {
            getBoundingArea();
        }
        return new Polygon(new int[]{xmin - 1, xmax + 1, xmax + 1, xmin - 1}, new int[]{ymax + 1, ymax + 1, ymin - 1, ymin - 1}, 4);
    }

    public Polygon getSimplifiedPolygon() {
        if (simplifiedPolygon == null) {
            LinkedList<Point> newPoints = new LinkedList<>();

            for (int i = 0; i < getConvexHullPolygon().npoints; i++) {
                newPoints.add(new Point(getConvexHullPolygon().xpoints[i], getConvexHullPolygon().ypoints[i]));
            }

            for (int i = 0; i < newPoints.size() - 2; i++) {
                if (getSlopeDifference(newPoints.get(i), newPoints.get(i + 1), newPoints.get(i + 2)) < 1) {
                    newPoints.remove(i);
                    i = 0;
                    //System.out.println(getSlopeDifference(newPoints.get(i), newPoints.get(i + 1), newPoints.get(i + 2)) + "");
                }
            }
            int[] xpoints = new int[newPoints.size()];
            int[] ypoints = new int[newPoints.size()];
            
            for(int i = 0; i < newPoints.size(); i++){
                xpoints[i] = newPoints.get(i).x;
                ypoints[i] = newPoints.get(i).y;
            }
            simplifiedPolygon = new Polygon(xpoints, ypoints, newPoints.size());
            //simplifiedPolygon = getConvexHullPolygon();
        }
        return simplifiedPolygon;
    }

    private boolean containsAllPoints(Polygon p) {
        for (Point point : points) {
            if (!p.contains(point)) {
                return false;
            }
        }
        return true;
    }

    private double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    public int getBoundingArea() {
        xmin = Integer.MAX_VALUE;
        ymin = Integer.MAX_VALUE;

        xmax = Integer.MIN_VALUE;
        ymax = Integer.MIN_VALUE;

        for (Point p : points) {
            xmin = (int) Math.min(xmin, p.x);
            ymin = (int) Math.min(ymin, p.y);

            xmax = (int) Math.max(xmax, p.x);
            ymax = (int) Math.max(ymax, p.y);
        }
        areaCalculated = true;
        return Math.abs((xmax - xmin) * (ymin - ymax));
    }

    private static boolean allPointsOnRight(Point p1, Point p2, Point[] A) {
        for (int i = 0; i < A.length; i++) {
            if (determinantformula(p1, p2, A[i]) > 0) {
                return false;
            }
        }
        return true;
    }

    private static double determinantformula(Point p1, Point p2, Point p3) {
        return (p1.x * p2.y + p3.x * p1.y + p2.x * p3.y
                - p3.x * p2.y - p2.x * p1.y - p1.x * p3.y);
    }

    private double getSlope(Point p1, Point p2) {
        return (((double) p2.y) - ((double) p1.y)) / (((double) p2.x) - ((double) p1.x));
    }

    private double getSlopeDifference(Point p1, Point p2, Point p3) {
        return Math.abs(getSlope(p1, p2) - getSlope(p2, p3));
    }
}
