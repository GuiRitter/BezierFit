package io.github.guiritter.bézier_fit;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;
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

    private static final Fitter fitter;

    private static Double jumpMaximum;

    private static Point2D pointArray[];

    private static final LinkedList<BufferedImage> targetImageList;

    private static boolean targetMatrix[][];

    static {
        targetImageList = new LinkedList<>();

        fitter = new Fitter();

        io.github.guiritter.bézier_fit.gui.Main GUI = new io.github.guiritter.bézier_fit.gui.Main() {

            private int color[];

            private File file;

            private int height;

            private BufferedImage image;

            private WritableRaster raster;

            private int width;

            private int x;

            private int y;

            @Override
            public void onFileButtonPressed() {
                try {
                    file = getFile();
                    if (file == null) {
                        return;
                    }
                    image = ImageIO.read(file);
                    if (image == null) {
                        return;
                    }
                    width = image.getWidth();
                    height = image.getHeight();
                    targetMatrix = new boolean[height][width];
                    raster = image.getRaster();
                    color = raster.getPixel(0, 0, (int[]) null);
                    for (y = 0; y < height; y++) {
                        for (x = 0; x < width; x++) {
                            raster.getPixel(0, 0, color);
                            targetMatrix[y][x] = color[0] > 127;
                        }
                    }
                    setImage(image);
                    setFileText(file.getName());
                    targetImageList.clear();
                    targetImageList.add(image);
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
                }
                pointArray = getPointArray();
                if (pointArray.length < 2) {
                    showWarning("please insert at least two points");
                }
                if (targetMatrix == null) {
                    showWarning("please insert an image");
                }
                setEnabled(false);
                (new Thread(fitter)).start();
            }

            @Override
            public void onMagnificationChanged(Byte magnification) {
                if (targetImageList.isEmpty()) {
                    return;
                }
                if (magnification == (targetImageList.size() + 1)) {
                    width = magnification * targetImageList.getFirst().getWidth();
                    height = magnification * targetImageList.getFirst().getHeight();
                    image = new BufferedImage(width, height, TYPE_BYTE_GRAY);
                    for (y = 0; y < height; y++) {
                        for (x = 0; x < width; x++) {
                            image.setRGB(x, y, targetImageList.getFirst().getRGB(x / magnification, y / magnification));
                        }
                    }
                    targetImageList.addLast(image);
                }
                setImage(targetImageList.get(magnification - 1));
            }
        };
    }

    public static void main(String[] args) {
    }
}
