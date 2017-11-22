package io.github.guiritter.bézier_fit;

import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.imageio.ImageIO;

/**
 *
 * @author Guilherme Alan Ritter
 */
public class Main {

    private static final LinkedList<BufferedImage> targetImageList;

    static {
        targetImageList = new LinkedList<>();

        io.github.guiritter.bézier_fit.gui.Main GUI = new io.github.guiritter.bézier_fit.gui.Main() {

            private File file;

            private int height;

            private BufferedImage image;

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
