package io.github.guiritter.bézier_fit;

import io.github.guiritter.bézier_fit.math.BézierCurve;
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

    private final BézierCurve curve;

    private double distance;

    private double distanceFit = POSITIVE_INFINITY;

    private double distanceMaximum;

    private double distanceMinimum;

    private final WritableRaster fittedRaster;

    private final int heightMagnified;

    private final int heightOriginal;

    private int i;

    private final ConcurrentHashMap<Boolean, Double> jumpMaximum;

    private final byte magnification;

    private final Point2D point;

    private final Point2D pointControlArray[];

    private final Point2D fittedPointControlArray[];

    private final HashSet<Point2D> pointCurveContinuousSet = new HashSet<>();

    private final HashSet<Point2D> pointCurveDiscreteSet = new HashSet<>();

    private double radius;

    private static final double revolution = 2d * PI;

    private final Random RNG = new Random();

    /**
     * Time step used to compute the curve. Inverse to the amount of points
     * in the curve.
     */
    private final ConcurrentHashMap<Boolean, Double> curveStep;

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
        double distanceMaximumLocal = NEGATIVE_INFINITY;
        for (Point2D point0 : set0) {
            distanceMinimum = POSITIVE_INFINITY;
            for (Point2D point1 : set1) {
                distance = distanceEuclidean(point0, point1);
                distanceMinimum = min(distanceMinimum, distance);
            }
            distanceMaximumLocal = max(distanceMaximumLocal, distanceMinimum);
        }
        return distanceMaximumLocal;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            pointCurveContinuousSet.clear();
            pointCurveDiscreteSet.clear();
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
                pointCurveContinuousSet.add(new Point2D.Double(point.getX(), point.getY()));
                x = (int) point.getX();
                y = (int) point.getY();
                visitedPointCurveSet = visitedPointCurveMap.get(y);
                if (visitedPointCurveSet == null) {
                    visitedPointCurveMap.put(y, visitedPointCurveSet = new HashSet<>());
                }
                if (visitedPointCurveSet.contains(x)) {
                    continue;
                }
                pointCurveDiscreteSet.add(new Point2D.Double(x, y));
                visitedPointCurveSet.add(x);
            }
            distanceMaximum = max(
             distanceHausdorff(pointCurveDiscreteSet, targetPointCurveSet),
             distanceHausdorff(targetPointCurveSet, pointCurveDiscreteSet)
            );
            System.out.println(distanceMaximum);
            if (distanceFit > distanceMaximum) {
                distanceFit = distanceMaximum;
                for (i = 0; i < pointControlArray.length; i++) {
                    fittedPointControlArray[i].setLocation(pointControlArray[i]);
                }
                for (y = 0; y < heightMagnified; y++) {
                    for (x = 0; x < widthMagnified; x++) {
                        fittedRaster.setPixel(x, y, transparency);
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
                                fittedRaster.setPixel(x, y, colorTransparent);
                            }
                        }
                    }
                }
                for (Point2D pointF : pointCurveContinuousSet) {
                    if ((pointF.getX() < 0) || (pointF.getX() >=  widthOriginal)
                     || (pointF.getY() < 0) || (pointF.getY() >= heightOriginal)) {
                        continue;
                    }
                    fittedRaster.setPixel(
                     ((int) (pointF.getX() * ((double) magnification))),
                     ((int) (pointF.getY() * ((double) magnification))),
                     colorOpaque
                    );
                }
                refresh(distance);
            }
        }
    }

    public Fitter(
     HashSet<Point2D> targetPointCurveSet,
     int width, int height,
     Point2D pointControlArray[],
     ConcurrentHashMap<Boolean, Double> jumpMaximum,
     WritableRaster fittedRaster,
     ConcurrentHashMap<Boolean, Double> curveStep,
     byte magnification) {
        this.targetPointCurveSet = targetPointCurveSet;
         widthOriginal = width;
        heightOriginal = height;
         widthMagnified =  widthOriginal * magnification;
        heightMagnified = heightOriginal * magnification;
        this.pointControlArray = pointControlArray;
        fittedPointControlArray = new Point2D[pointControlArray.length];
        for (i = 0; i < pointControlArray.length; i++) {
            fittedPointControlArray[i] = new Point2D.Double(
             pointControlArray[i].getX(),
             pointControlArray[i].getY()
            );
        }
        this.jumpMaximum = jumpMaximum;
        this.fittedRaster = fittedRaster;
        this.curveStep = curveStep;
        this.magnification = magnification;
        curve = new BézierCurve(pointControlArray, point = new Point2D.Double());
    }
}
