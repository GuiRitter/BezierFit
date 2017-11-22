package io.github.guiritter.bézier_fit;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Guilherme Alan Ritter
 */
public class Main {

    public static void main(String[] args) {
        io.github.guiritter.bézier_fit.gui.Main GUI = new io.github.guiritter.bézier_fit.gui.Main() {

            private File file;

            @Override
            public void onFileButtonPressed() {
                try {
                    file = getFile();
                    setImage(ImageIO.read(file));
                    setFileText(file.getName());
                } catch (IOException ex) {
                    showError(ex);
                }
            }

            @Override
            public void onMagnificationChanged(byte magnification) {
                // TODO
            }
        };
    }
}
