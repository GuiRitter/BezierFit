package io.github.guiritter.bézier_fit.gui;

import io.github.guiritter.imagecomponent.ImageComponent;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.VERTICAL;
import static java.awt.GridBagConstraints.WEST;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JFileChooser.FILES_ONLY;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author Guilherme Alan Ritter
 */
@SuppressWarnings("CallToPrintStackTrace")
public abstract class Main {

    private final JFileChooser chooser;

    private final JTextField fileField;

    public static final Font font = new Font("DejaVu Sans", 0, 12); // NOI18N

    private final JFrame frame;

    private final JSpinner magnificationSpinner;

    private final ImageComponent previewComponent;

    /**
     * Distance between graphical user interface components.
     */
    public static final int SPACE_INT;

    /**
     * Distance between graphical user interface components.
     */
    public static final Dimension SPACE_DIMENSION;

    /**
     * Half distance between graphical user interface components.
     */
    public static final int SPACE_HALF_INT;

    /**
     * Half distance between graphical user interface components.
     */
    public static final Dimension SPACE_HALF_DIMENSION;

    /**
     * Opens a file chooser.
     * @return the selected file, or null otherwise
     */
    public final File getFile() {
        chooser.setSelectedFile(null);
        if (chooser.showOpenDialog(frame) != APPROVE_OPTION) {
            return null;
        }
        return chooser.getSelectedFile();
    }

    public byte getMagnification() {
        return ((SpinnerNumberModel) magnificationSpinner.getModel()).getNumber().byteValue();
    }

    public abstract void onFileButtonPressed();

    public abstract void onMagnificationChanged(byte magnification);

    public final void setFileText(String text) {
        fileField.setText(text);
    }

    public final void setImage(BufferedImage image) {
        previewComponent.setImage(image);
        frame.pack();
    }

    /**
     * Prints a stack trace and shows a dialog
     * displaying either a warning or an error.
     * @param ex
     * @param title
     * @param messageType
     */
    public void showDialog(Exception ex, String title, int messageType) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(frame, ex.getLocalizedMessage(), title, messageType);
    }

    /**
     * Prints a stack trace and shows a dialog
     * displaying an error.
     * @param ex
     */
    public void showError(Exception ex) {
        showDialog(ex, "error", ERROR_MESSAGE);
    }

    /**
     * Prints a stack trace and shows a dialog
     * displaying a warning.
     * @param ex
     */
    public void showWarning(Exception ex) {
        showDialog(ex, "warning", WARNING_MESSAGE);
    }

    static {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        UIManager.put("Button.font",             font);
        UIManager.put("CheckBox.font",           font);
        UIManager.put("ComboBox.font",           font);
        UIManager.put("FormattedTextField.font", font);
        UIManager.put("InternalFrame.titleFont", font);
        UIManager.put("Label.font",              font);
        UIManager.put("List.font",               font);
        UIManager.put("MenuItem.font",           font);
        UIManager.put("Spinner.font",            font);
        UIManager.put("Table.font",              font);
        UIManager.put("TableHeader.font",        font);
        UIManager.put("TextField.font",          font);
        UIManager.put("ToolTip.font",            font);
        JLabel label = new JLabel("—");
        SPACE_INT = Math.min(
         label.getPreferredSize().width,
         label.getPreferredSize().height);
        SPACE_HALF_INT = SPACE_INT / 2;
        SPACE_DIMENSION = new Dimension(SPACE_INT, SPACE_INT);
        SPACE_HALF_DIMENSION = new Dimension(SPACE_HALF_INT, SPACE_HALF_INT);
    }

    public Main() {
        frame = new JFrame("Bézier Fit");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        chooser = new JFileChooser();
        chooser.setFileSelectionMode(FILES_ONLY);

        GridBagConstraints gridBagConstraints;

        frame.getContentPane().setLayout(new GridBagLayout());

        JButton fileButton = new JButton("file:");
        fileButton.addActionListener((ActionEvent e) -> {

            onFileButtonPressed();
            /* TODO
            file = null;
            if (chooser.showOpenDialog(frame) != APPROVE_OPTION) {
            return;
            }
            if ((file = chooser.getSelectedFile()) == null) {
            return;
            }
            try {
            previewComponent.setImage(ImageIO.read(file));
            } catch (IOException ex) {
            showError(ex);
            }
            frame.pack();
            /**/
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = VERTICAL;
        gridBagConstraints.anchor = WEST;
        gridBagConstraints.insets = new Insets(SPACE_INT, SPACE_INT, 0, SPACE_HALF_INT);
        frame.getContentPane().add(fileButton, gridBagConstraints);

        fileField = new JTextField();
        fileField.setEditable(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.insets = new Insets(0, SPACE_INT, SPACE_HALF_INT, SPACE_HALF_INT);
        frame.getContentPane().add(fileField, gridBagConstraints);

        JLabel magnificationLabel = new JLabel("magnification:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.anchor = WEST;
        gridBagConstraints.insets = new Insets(SPACE_HALF_INT, SPACE_INT, 0, SPACE_HALF_INT);
        frame.getContentPane().add(magnificationLabel, gridBagConstraints);

        magnificationSpinner = new JSpinner(new SpinnerNumberModel(
         Byte.valueOf("1"),
         Byte.valueOf("1"),
         Byte.valueOf(Byte.MAX_VALUE),
         Byte.valueOf("1")));
        magnificationSpinner.addChangeListener((ChangeEvent e) -> {

            onMagnificationChanged(getMagnification());
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.insets = new Insets(0, SPACE_INT, SPACE_HALF_INT, SPACE_HALF_INT);
        frame.getContentPane().add(magnificationSpinner, gridBagConstraints);

        JLabel jumpLabel = new JLabel("maximum jump:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.anchor = WEST;
        gridBagConstraints.insets = new Insets(SPACE_HALF_INT, SPACE_HALF_INT, 0, SPACE_HALF_INT);
        frame.getContentPane().add(jumpLabel, gridBagConstraints);

        JFormattedTextField jumpField = new JFormattedTextField();
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.insets = new Insets(0, SPACE_HALF_INT, SPACE_HALF_INT, SPACE_HALF_INT);
        frame.getContentPane().add(jumpField, gridBagConstraints);

        JLabel xLabel = new JLabel("x:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.anchor = WEST;
        gridBagConstraints.insets = new Insets(SPACE_HALF_INT, SPACE_INT, 0, SPACE_HALF_INT);
        frame.getContentPane().add(xLabel, gridBagConstraints);

        JFormattedTextField xField = new JFormattedTextField();
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.insets = new Insets(0, SPACE_INT, SPACE_HALF_INT, SPACE_HALF_INT);
        frame.getContentPane().add(xField, gridBagConstraints);

        JLabel yLabel = new JLabel("y:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.anchor = WEST;
        gridBagConstraints.insets = new Insets(SPACE_HALF_INT, SPACE_HALF_INT, 0, SPACE_HALF_INT);
        frame.getContentPane().add(yLabel, gridBagConstraints);

        JFormattedTextField yField = new JFormattedTextField();
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.insets = new Insets(0, SPACE_HALF_INT, SPACE_HALF_INT, SPACE_HALF_INT);
        frame.getContentPane().add(yField, gridBagConstraints);

        JList xList = new JList<>();
        JScrollPane xPane = new JScrollPane();
        xPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
        xPane.setViewportView(xList);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(SPACE_HALF_INT, SPACE_INT, SPACE_HALF_INT, 0);
        frame.getContentPane().add(xPane, gridBagConstraints);

        JList yList = new JList<>();
        JScrollPane yPane = new JScrollPane();
        yPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        yPane.setViewportView(yList);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(SPACE_HALF_INT, 0, SPACE_HALF_INT, SPACE_HALF_INT);
        frame.getContentPane().add(yPane, gridBagConstraints);

        yPane.setVerticalScrollBar(xPane.getVerticalScrollBar());

        JLabel curvePointAmountLabel = new JLabel();
        curvePointAmountLabel.setText("point amount:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.anchor = WEST;
        gridBagConstraints.insets = new Insets(SPACE_HALF_INT, SPACE_INT, 0, SPACE_HALF_INT);
        frame.getContentPane().add(curvePointAmountLabel, gridBagConstraints);

        JSpinner curvePointAmountSpinner = new JSpinner();
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.insets = new Insets(0, SPACE_INT, SPACE_INT, SPACE_HALF_INT);
        frame.getContentPane().add(curvePointAmountSpinner, gridBagConstraints);

        JLabel errorLabel = new JLabel("error:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.anchor = WEST;
        gridBagConstraints.insets = new Insets(SPACE_HALF_INT, SPACE_HALF_INT, 0, SPACE_HALF_INT);
        frame.getContentPane().add(errorLabel, gridBagConstraints);

        JFormattedTextField errorField = new JFormattedTextField("init");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.insets = new Insets(0, SPACE_HALF_INT, SPACE_INT, SPACE_HALF_INT);
        frame.getContentPane().add(errorField, gridBagConstraints);

        previewComponent = new ImageComponent();
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 9;
        gridBagConstraints.insets = new Insets(SPACE_INT, SPACE_HALF_INT, SPACE_INT, SPACE_INT);
        frame.getContentPane().add(previewComponent, gridBagConstraints);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
