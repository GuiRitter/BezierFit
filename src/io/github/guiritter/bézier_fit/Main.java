package io.github.guiritter.bézier_fit;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.imageio.ImageIO;

/**
 *
 * @author Guilherme Alan Ritter
 */
public class Main {

    private static Fitter fitter;

    static final io.github.guiritter.bézier_fit.gui.Main GUI;

    private static Double jumpMaximum;

    private static Point2D pointArray[];

    private static final LinkedList<BufferedImage> targetImageList;

    private static boolean targetMatrix[][];

    static {
        targetImageList = new LinkedList<>();

        GUI = new io.github.guiritter.bézier_fit.gui.Main() {

            private int color[];

            private File file;

            private BufferedImage fittedImage;

            private WritableRaster fittedRaster;

            private int height;

            /**
             * Time step used to compute the curve. Inverse to the amount of points
             * in the curve.
             */
            private final Wrapper<Double> step = new Wrapper<>();

            private BufferedImage targetImage;

            private WritableRaster targetRaster;

            private int width;

            private int x;

            private int y;

            @Override
            public void onCurveStepChanged(double curveStep) {
            }

            @Override
            public void onFileButtonPressed() {
                try {
                    file = getFile();
                    if (file == null) {
                        return;
                    }
                    targetImage = ImageIO.read(file);
                    if (targetImage == null) {
                        return;
                    }
                    width = targetImage.getWidth();
                    height = targetImage.getHeight();
                    targetMatrix = new boolean[height][width];
                    targetRaster = targetImage.getRaster();
                    color = targetRaster.getPixel(0, 0, (int[]) null);
                    for (y = 0; y < height; y++) {
                        for (x = 0; x < width; x++) {
                            targetRaster.getPixel(x, y, color);
                            targetMatrix[y][x] = color[0] > 127;
                        }
                    }
                    fittedImage = new BufferedImage(width, height, TYPE_INT_ARGB);
                    fittedRaster = fittedImage.getRaster();
                    setImage(targetImage, fittedImage);
                    try {
                        setFileText(file.getCanonicalPath());
                    } catch (IOException ex) {
                        setFileText(file.getAbsolutePath());
                    }
                    targetImageList.clear();
                    targetImageList.add(targetImage);
                    setMagnification((byte) 1);
                } catch (IOException ex) {
                    showError(ex);
                }
            }

            @Override
            public void onInitButtonPressed() {
                jumpMaximum = getJumpMaximum();
                if (jumpMaximum == null) {
                    showWarning("please insert a maximum jump value");
                    return;
                }
                pointArray = getPointArray();
                if (pointArray.length < 2) {
                    showWarning("please insert at least two points");
                    return;
                }
                if (targetMatrix == null) {
                    showWarning("please insert an image");
                    return;
                }
                setEnabled(false);
                step.value = getCurveStep();
                fitter = new Fitter(
                 targetMatrix,
                 pointArray,
                 fittedRaster,
                 step,
                 getMagnification()
                ){
                    @Override
                    public void refresh() {
                        GUI.refresh();
                    }
                };
                (new Thread(fitter, "fitter")).start();
            }

            @Override
            public void onMagnificationChanged(Byte magnification) {
                if (targetImageList.isEmpty()) {
                    return;
                }
                if (magnification == (targetImageList.size() + 1)) {
                    width = magnification * targetImageList.getFirst().getWidth();
                    height = magnification * targetImageList.getFirst().getHeight();
                    targetImage = new BufferedImage(width, height, TYPE_BYTE_GRAY);
                    fittedImage = new BufferedImage(width, height, TYPE_INT_ARGB);
                    fittedRaster = fittedImage.getRaster();
                    for (y = 0; y < height; y++) {
                        for (x = 0; x < width; x++) {
                            targetImage.setRGB(x, y, targetImageList.getFirst().getRGB(x / magnification, y / magnification));
                        }
                    }
                    targetImageList.addLast(targetImage);
                }
                setImage(targetImageList.get(magnification - 1), fittedImage);
            }
        };
    }

    public static void main(String[] args) {
    }
}
