package io.github.guiritter.bezier_fit;

import io.github.guiritter.bezier_fit.math.BezierCurve;
import java.awt.geom.Point2D;
import java.awt.image.WritableRaster;
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Double.max;
import static java.lang.Double.min;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Guilherme Alan Ritter
 */
public abstract class Fitter implements Runnable{

    private double angle;

    private final int colorOpaque[] = new int[]{255, 0, 255, 255};

    private final int colorTransparent[] = new int[]{0, 128, 0, 128};

    private final BezierCurve curve;

    /**
     * Time step used to compute the curve. Inverse to the amount of points
     * in the curve.
     */
    private final ConcurrentHashMap<Boolean, Double> curveStep;

    private double distance;

    private double distanceFit = POSITIVE_INFINITY;

    private double distanceMaximum;

    private double distanceMinimum;

    private double fittedDistance;

    private final Point2D fittedPointControlArray[];

    private final int fittedContinuousBuffer[][][];

    private final WritableRaster fittedContinuousRaster;

    private final int fittedDiscreteBuffer[][][];

    private final WritableRaster fittedDiscreteRaster;

    private boolean found;

    private final int heightMagnified;

    private final int heightOriginal;

    private int i;

    private final ConcurrentHashMap<Boolean, Double> jumpMaximum;

    private final byte magnification;

    private final Point2D point;

    private final Point2D pointControlArray[];

    private final HashSet<Point2D> pointCurveSet = new HashSet<>();

    private double radius;

    private static final double revolution = 2d * PI;

    private final Random RNG = new Random();

    /**
     * Time that is stepped from 0 to 1.
     */
    private double t;

    private final HashSet<Point2D> targetPointCurveSet;

    /**
     * Used to clear the curve display area.
     */
    private final int transparency[] = new int[]{0, 0, 0, 0};

    private final HashMap<Integer, HashSet<Integer>> visitedPointCurveMap = new HashMap<>();

    private HashSet<Integer> visitedPointCurveSet;

    private final int widthMagnified;

    private final int widthOriginal;

    private int x;

    private int xHigh;

    private int xLow;

    private int y;

    private int yHigh;

    private int yLow;

    public abstract void refresh(double distance);

    public static final double distanceEuclidean(Point2D point0, Point2D point1) {
        return sqrt(pow(point1.getX() - point0.getX(), 2) + pow(point1.getY() - point0.getY(), 2));
    }

    public final double distanceHausdorff(Collection<Point2D> set0, Collection<Point2D> set1) {
        distanceMaximum = NEGATIVE_INFINITY;
        for (Point2D point0 : set0) {
            distanceMinimum = POSITIVE_INFINITY;
            for (Point2D point1 : set1) {
                distance = distanceEuclidean(point0, point1);
                distanceMinimum = min(distanceMinimum, distance);
            }
            distanceMaximum = max(distanceMaximum, distanceMinimum);
        }
        return distanceMaximum;
    }

    public final double distanceMatch(Collection<Point2D> set0, Collection<Point2D> set1) {
        distanceMaximum = 0;
        for (Point2D point0 : set0) {
            found = false;
            for (Point2D point1 : set1) {
                if (pointEqual(point0, point1)) {
                    distanceMaximum--;
                    found = true;
                    break;
                }
                if (!found) {
                    distanceMaximum += 0.01;
                }
            }
        }
        return distanceMaximum;
    }

    public static boolean pointEqual(Point2D point0, Point2D point1) {
        return pointEqualX(point0, point1) && pointEqualY(point0, point1);
    }

    public static boolean pointEqualX(Point2D point0, Point2D point1) {
        return ((int) point0.getX()) == ((int) point1.getX());
    }

    public static boolean pointEqualY(Point2D point0, Point2D point1) {
        return ((int) point0.getY()) == ((int) point1.getY());
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            pointCurveSet.clear();
            visitedPointCurveMap.clear();
            for (i = 0; i < pointControlArray.length; i++) {
                angle = revolution * RNG.nextDouble();
                radius = jumpMaximum.get(true) * RNG.nextDouble();
                pointControlArray[i].setLocation(
                 fittedPointControlArray[i].getX() + (cos(angle) * radius),
                 fittedPointControlArray[i].getY() + (sin(angle) * radius)
                );
            }
            for (t = 0; t <= 1; t += curveStep.get(true)) {
                curve.op(t);
                x = (int) point.getX();
                y = (int) point.getY();
                visitedPointCurveSet = visitedPointCurveMap.get(y);
                if (visitedPointCurveSet == null) {
                    visitedPointCurveMap.put(y, visitedPointCurveSet = new HashSet<>());
                }
                if (visitedPointCurveSet.contains(x)) {
                    continue;
                }
                pointCurveSet.add(new Point2D.Double(x, y));
                visitedPointCurveSet.add(x);
            }
            distanceMaximum = max(
             distanceHausdorff(pointCurveSet, targetPointCurveSet),
             distanceHausdorff(targetPointCurveSet, pointCurveSet)
            );
//            distanceMaximum
//             = distanceMatch(pointCurveSet, targetPointCurveSet)
//             + distanceMatch(targetPointCurveSet, pointCurveSet);
            if (distanceFit > distanceMaximum) {
                distanceFit = distanceMaximum;
                fittedDistance = distanceFit;
                for (i = 0; i < pointControlArray.length; i++) {
                    fittedPointControlArray[i].setLocation(pointControlArray[i]);
                }
                for (y = 0; y < heightMagnified; y++) {
                    for (x = 0; x < widthMagnified; x++) {
                        fittedDiscreteBuffer[y][x] = transparency;
                    }
                }
                for (int yV : visitedPointCurveMap.keySet()) {
                    for (int xV : visitedPointCurveMap.get(yV)) {
                        if ((xV < 0) || (xV >=  widthOriginal)
                         || (yV < 0) || (yV >= heightOriginal)) {
                            continue;
                        }
                        xLow = xV * magnification;
                        xHigh = (xV + 1) * magnification;
                        yLow = yV * magnification;
                        yHigh = (yV + 1) * magnification;
                        for (y = yLow; y < yHigh; y++) {
                            for (x = xLow; x < xHigh; x++) {
                                fittedDiscreteBuffer[y][x] = colorTransparent;
                            }
                        }
                    }
                }
                for (y = 0; y < heightMagnified; y++) {
                    for (x = 0; x < widthMagnified; x++) {
                        fittedDiscreteRaster.setPixel(x, y, fittedDiscreteBuffer[y][x]);
                    }
                }
            }
            for (y = 0; y < heightMagnified; y++) {
                for (x = 0; x < widthMagnified; x++) {
                    fittedContinuousBuffer[y][x] = transparency;
                }
            }
            for (t = 0; t <= 1; t += curveStep.get(true)) {
                curve.op(t);
                if ((point.getX() < 0) || (point.getX() >=  widthOriginal)
                 || (point.getY() < 0) || (point.getY() >= heightOriginal)) {
                    continue;
                }
                fittedContinuousBuffer
                 [(int) (point.getY() * ((double) magnification))]
                 [(int) (point.getX() * ((double) magnification))]
                 = colorOpaque;
            }
            for (y = 0; y < heightMagnified; y++) {
                for (x = 0; x < widthMagnified; x++) {
                    fittedContinuousRaster.setPixel(x, y, fittedContinuousBuffer[y][x]);
                }
            }
            refresh(fittedDistance);
        }
    }

    public Fitter(
     HashSet<Point2D> targetPointCurveSet,
     int width, int height,
     Point2D pointControlArray[],
     Point2D fittedPointControlArray[],
     ConcurrentHashMap<Boolean, Double> jumpMaximum,
     WritableRaster fittedDiscreteRaster,
     WritableRaster fittedContinuousRaster,
     ConcurrentHashMap<Boolean, Double> curveStep,
     byte magnification) {
        this.targetPointCurveSet = targetPointCurveSet;
         widthOriginal = width;
        heightOriginal = height;
         widthMagnified =  widthOriginal * magnification;
        heightMagnified = heightOriginal * magnification;
        this.pointControlArray = pointControlArray;
        this.fittedPointControlArray = fittedPointControlArray;
        this.jumpMaximum = jumpMaximum;
        this.fittedDiscreteRaster = fittedDiscreteRaster;
        this.fittedContinuousRaster = fittedContinuousRaster;
        this.curveStep = curveStep;
        this.magnification = magnification;
        curve = new BezierCurve(pointControlArray, point = new Point2D.Double());
        fittedContinuousBuffer = new int[heightMagnified][widthMagnified][4];
        fittedDiscreteBuffer = new int[heightMagnified][widthMagnified][4];
    }
}
