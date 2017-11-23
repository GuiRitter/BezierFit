package io.github.guiritter.bézier_fit;

import io.github.guiritter.bézier_fit.math.BézierCurve;
import java.awt.geom.Point2D;
import java.awt.image.WritableRaster;
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Double.max;
import static java.lang.Double.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Guilherme Alan Ritter
 */
public abstract class Fitter implements Runnable{

    private final int color[];

    private final BézierCurve curve;

    private double distance;

    private double distanceMaximum;

    private double distanceMaximumFittedToTarget;

    private double distanceMaximumTargetToFitted;

    private double distanceMinimum;

    private final WritableRaster fittedRaster;

    private final int heightMagnified;

    private final int heightOriginal;

    private final byte magnification;

    private final Point2D point;

    private final Point2D pointArray[];

    private final HashSet<Point2D> pointSet;

    private final boolean targetMatrix[][];

    /**
     * Time step used to compute the curve. Inverse to the amount of points
     * in the curve.
     */
    private final Wrapper<Double> step;

    /**
     * Time that is stepped from 0 to 1.
     */
    private double t;

    /**
     * Used to clear the curve display area.
     */
    private final int transparency[] = new int[]{0, 0, 0, 0};

    private final HashMap<Integer, HashSet<Integer>> visitedPointMap;

    private HashSet<Integer> visitedPointSet;

    private final int widthMagnified;

    private final int widthOriginal;

    private int x;

    private int xHigh;

    private int xLow;

    private int y;

    private int yHigh;

    private int yLow;

    public abstract void refresh();

    public static final double distanceEuclidean(double x0, double y0, double x1, double y1) {
        return sqrt(pow(x1 - x0, 2) + pow(y1 - y0, 2));
    }

    @Override
    public void run() {
        for (y = 0; y < heightMagnified; y++) {
            for (x = 0; x < widthMagnified; x++) {
                fittedRaster.setPixel(x, y, transparency);
            }
        }
        pointSet.clear();
        visitedPointMap.clear();
        for (t = 0; t <= 1; t += step.value) {
            curve.op(t);
            x = (int) point.getX();
            y = (int) point.getY();
            if ((x < 0) || (x >=  widthOriginal)
             || (y < 0) || (y >= heightOriginal)) {
                continue;
            }
            visitedPointSet = visitedPointMap.get(y);
            if (visitedPointSet == null) {
                visitedPointMap.put(y, visitedPointSet = new HashSet<>());
            }
            if (visitedPointSet.contains(x)) {
                continue;
            }
            pointSet.add(new Point2D.Double(point.getX(), point.getY()));
            visitedPointSet.add(x);
            xLow = x * magnification;
            xHigh = (x + 1) * magnification;
            yLow = y * magnification;
            yHigh = (y + 1) * magnification;
            for (y = yLow; y < yHigh; y++) {
                for (x = xLow; x < xHigh; x++) {
                    fittedRaster.setPixel(x, y, color);
                }
            }
        }
        refresh();
        // Hausdorff distance {
        distanceMaximumFittedToTarget = NEGATIVE_INFINITY;
        for (Point2D pointD : pointSet) {
            distanceMinimum = POSITIVE_INFINITY;
            for (y = 0; y < heightOriginal; y++) {
                for (x = 0; x < widthOriginal; x++) {
                    if (!targetMatrix[y][x]) {
                        continue;
                    }
                    distance = distanceEuclidean(x, y, pointD.getX(), pointD.getY());
                    distanceMinimum = min(distanceMinimum, distance);
                }
            }
            distanceMaximumFittedToTarget = max(distanceMaximumFittedToTarget, distanceMinimum);
        }
        distanceMaximumTargetToFitted = NEGATIVE_INFINITY;
        for (y = 0; y < heightOriginal; y++) {
            for (x = 0; x < widthOriginal; x++) {
                if (!targetMatrix[y][x]) {
                    continue;
                }
                distanceMinimum = POSITIVE_INFINITY;
                for (Point2D pointD : pointSet) {
                    distance = distanceEuclidean(x, y, pointD.getX(), pointD.getY());
                    distanceMinimum = min(distanceMinimum, distance);
                }
                distanceMaximumTargetToFitted = max(distanceMaximumTargetToFitted, distanceMinimum);
            }
        }
        distanceMaximum = max(distanceMaximumFittedToTarget, distanceMaximumTargetToFitted);
        System.out.println(distanceMaximum);
        // } Hausdorff distance
        /* TODO
        while (!Thread.interrupted()) {
        }
        /**/
    }

    public Fitter(
     boolean targetMatrix[][],
     Point2D pointArray[],
     WritableRaster fittedRaster,
     Wrapper<Double> step,
     byte magnification) {
        this.targetMatrix = targetMatrix;
        this.widthOriginal = targetMatrix[0].length;
        this.heightOriginal = targetMatrix.length;
         widthMagnified =  widthOriginal * magnification;
        heightMagnified = heightOriginal * magnification;
        this.pointArray = pointArray;
        this.fittedRaster = fittedRaster;
        this.step = step;
        this.magnification = magnification;

        color = new int[]{0, 128, 0, 128};
        curve = new BézierCurve(pointArray, point = new Point2D.Double());
        pointSet = new HashSet<>();
        visitedPointMap = new HashMap<>();
    }
}
