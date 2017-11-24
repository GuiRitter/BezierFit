package io.github.guiritter.bézier_fit.gui;

import io.github.guiritter.imagecomponent.ImageComponentMultiple;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.VERTICAL;
import static java.awt.GridBagConstraints.WEST;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.NumberFormat;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
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
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;
import javax.swing.SpinnerNumberModel;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author Guilherme Alan Ritter
 */
@SuppressWarnings("CallToPrintStackTrace")
public abstract class Main {

    private final JButton addButton;

    private final JFileChooser chooser;

    private final JSpinner curvePointAmountSpinner;

    private final JFormattedTextField errorField;

    private final JButton fileButton;

    private final JTextField fileField;

    public static final Font font = new Font("DejaVu Sans", 0, 12); // NOI18N

    private final JFrame frame;

    private int i;

    private final JButton initButton;

    private final JFormattedTextField jumpField;

    private final JSpinner magnificationSpinner;

    private final ImageComponentMultiple previewComponent;

    private final JButton removeButton;

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

    private final JList<Double> xList;

    private final JList<Double> yList;

    public double getCurveStep() {
        return 1d / ((SpinnerNumberModel) curvePointAmountSpinner.getModel()).getNumber().doubleValue();
    }

    public final double getError() {
        return ((Number) errorField.getValue()).doubleValue();
    }

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

    public final Double getJumpMaximum() {
        try {
            return ((Number) jumpField.getValue()).doubleValue();
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public byte getMagnification() {
        return ((SpinnerNumberModel) magnificationSpinner.getModel()).getNumber().byteValue();
    }

    public Point2D[] getPointArray() {
        Point2D pointArray[] = new Point2D[((DefaultListModel<Double>) (xList.getModel())).getSize()];
        for (i = 0; i < pointArray.length; i++) {
            pointArray[i] = new Point2D.Double(
             ((DefaultListModel<Double>) (xList.getModel())).get(i),
             ((DefaultListModel<Double>) (yList.getModel())).get(i)
            );
        }
        return pointArray;
    }

    public abstract void onCurveStepChanged(double curveStep);

    public abstract void onFileButtonPressed();

    public abstract void onInitButtonPressed();

    public abstract void onMagnificationChanged(Byte magnification);

    public final void refresh() {
        frame.revalidate();
        frame.repaint();
    }

    public final void refresh(Point2D pointControlArray[], double distance) {
        for (i = 0; i < pointControlArray.length; i++) {
            ((DefaultListModel<Double>) (xList.getModel())).set(i, pointControlArray[i].getX());
            ((DefaultListModel<Double>) (yList.getModel())).set(i, pointControlArray[i].getY());
        }
        errorField.setValue(distance);
        refresh();
    }

    public final void setEnabled(boolean enabled) {
        fileButton.setEnabled(enabled);
        initButton.setEnabled(enabled);
        magnificationSpinner.setEnabled(enabled);
        addButton.setEnabled(enabled);
        removeButton.setEnabled(enabled);
    }

    public final void setFileText(String text) {
        fileField.setText(text);
    }

    /**
     * Sets the preview image and adjusts the interface.
     * @param targetImage
     */
    public final void setImage(BufferedImage targetImage) {
        previewComponent.images.set(0, targetImage);
        previewComponent.update();
        frame.pack();
        frame.setLocationRelativeTo(null);
        refresh();
    }

    /**
     * Sets the preview image and adjusts the interface.
     * @param targetImage
     * @param fittedImage
     */
    public final void setImage(BufferedImage targetImage, BufferedImage fittedImage) {
        previewComponent.images.set(1, fittedImage);
        setImage(targetImage);
    }

    public final void setMagnification(byte magnification) {
        magnificationSpinner.setValue(magnification);
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

    /**
     * Shows a dialog displaying a warning.
     * @param message
     */
    public void showWarning(String message) {
        JOptionPane.showMessageDialog(frame, message, "warning", WARNING_MESSAGE);
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

        fileButton = new JButton("file:");
        fileButton.addActionListener((ActionEvent e) -> {

            onFileButtonPressed();
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = VERTICAL;
        gridBagConstraints.anchor = WEST;
        gridBagConstraints.insets = new Insets(SPACE_INT, SPACE_INT, 0, SPACE_HALF_INT);
        frame.getContentPane().add(fileButton, gridBagConstraints);

        initButton = new JButton("init");
        initButton.addActionListener((ActionEvent e) -> {

            onInitButtonPressed();
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = VERTICAL;
        gridBagConstraints.anchor = EAST;
        gridBagConstraints.insets = new Insets(SPACE_INT, SPACE_INT, 0, SPACE_HALF_INT);
        frame.getContentPane().add(initButton, gridBagConstraints);

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
        ((JSpinner.DefaultEditor) magnificationSpinner.getEditor()).getTextField().setEditable(false);
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

        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(Integer.MAX_VALUE);

        jumpField = new JFormattedTextField(format);
        jumpField.setValue(5);
//        jumpField.addPropertyChangeListener("value", new OutputListener(outputMinimumXField, outputMinimumX, outputMinimumX, outputMaximumX, fitX, width));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.insets = new Insets(0, SPACE_HALF_INT, SPACE_HALF_INT, SPACE_HALF_INT);
        frame.getContentPane().add(jumpField, gridBagConstraints);

        xList = new JList<>(new DefaultListModel<>());
        xList.setSelectionMode(SINGLE_SELECTION);
        ((DefaultListCellRenderer) xList.getCellRenderer()).setHorizontalAlignment(CENTER);
        JScrollPane xPane = new JScrollPane(xList);
        xPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(SPACE_HALF_INT, SPACE_INT, SPACE_HALF_INT, 0);
        frame.getContentPane().add(xPane, gridBagConstraints);

        yList = new JList<>(new DefaultListModel<>());
        yList.setSelectionMode(SINGLE_SELECTION);
        ((DefaultListCellRenderer) yList.getCellRenderer()).setHorizontalAlignment(CENTER);
        JScrollPane yPane = new JScrollPane(yList);
        yPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(SPACE_HALF_INT, 0, SPACE_HALF_INT, SPACE_HALF_INT);
        frame.getContentPane().add(yPane, gridBagConstraints);

        yList.setSelectionModel(xList.getSelectionModel());
        yPane.setVerticalScrollBar(xPane.getVerticalScrollBar());

        JLabel xLabel = new JLabel("x:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.anchor = WEST;
        gridBagConstraints.insets = new Insets(SPACE_HALF_INT, SPACE_INT, 0, SPACE_HALF_INT);
        frame.getContentPane().add(xLabel, gridBagConstraints);

        JFormattedTextField xField = new JFormattedTextField(format);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.insets = new Insets(0, SPACE_INT, SPACE_HALF_INT, SPACE_HALF_INT);
        frame.getContentPane().add(xField, gridBagConstraints);

        JLabel yLabel = new JLabel("y:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.anchor = WEST;
        gridBagConstraints.insets = new Insets(SPACE_HALF_INT, SPACE_HALF_INT, 0, SPACE_HALF_INT);
        frame.getContentPane().add(yLabel, gridBagConstraints);

        JFormattedTextField yField = new JFormattedTextField(format);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.insets = new Insets(0, SPACE_HALF_INT, SPACE_HALF_INT, SPACE_HALF_INT);
        frame.getContentPane().add(yField, gridBagConstraints);

        addButton = new JButton("add");
        addButton.addActionListener((ActionEvent e) -> {

            i = xList.getSelectedIndex();
            if (i < 0) {
                ((DefaultListModel<Double>) (xList.getModel())).addElement(((Number) xField.getValue()).doubleValue());
                ((DefaultListModel<Double>) (yList.getModel())).addElement(((Number) yField.getValue()).doubleValue());
            } else {
                ((DefaultListModel<Double>) (xList.getModel())).add(i, ((Number) xField.getValue()).doubleValue());
                ((DefaultListModel<Double>) (yList.getModel())).add(i, ((Number) yField.getValue()).doubleValue());
            }
            frame.revalidate();
            frame.repaint();
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.insets = new Insets(SPACE_HALF_INT, SPACE_INT, SPACE_HALF_INT, SPACE_HALF_INT);
        frame.getContentPane().add(addButton, gridBagConstraints);

        removeButton = new JButton("remove");
        removeButton.addActionListener((ActionEvent e) -> {

            i = xList.getSelectedIndex();
            if (i > -1) {
                ((DefaultListModel<Double>) (xList.getModel())).remove(i);
                ((DefaultListModel<Double>) (yList.getModel())).remove(i);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.insets = new Insets(SPACE_HALF_INT, SPACE_HALF_INT, SPACE_HALF_INT, SPACE_HALF_INT);
        frame.getContentPane().add(removeButton, gridBagConstraints);

        JLabel curvePointAmountLabel = new JLabel();
        curvePointAmountLabel.setText("curve density:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.anchor = WEST;
        gridBagConstraints.insets = new Insets(SPACE_HALF_INT, SPACE_INT, 0, SPACE_HALF_INT);
        frame.getContentPane().add(curvePointAmountLabel, gridBagConstraints);

        curvePointAmountSpinner = new JSpinner(new SpinnerNumberModel(100, 0, Long.MAX_VALUE, 1));
        curvePointAmountSpinner.addChangeListener((ChangeEvent e) -> {

            onCurveStepChanged(getCurveStep());
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.insets = new Insets(0, SPACE_INT, SPACE_INT, SPACE_HALF_INT);
        frame.getContentPane().add(curvePointAmountSpinner, gridBagConstraints);

        //* TODO test
        ((DefaultListModel<Double>) (xList.getModel())).addElement(0d);
        ((DefaultListModel<Double>) (xList.getModel())).addElement(0d);
        ((DefaultListModel<Double>) (xList.getModel())).addElement(0d);
        ((DefaultListModel<Double>) (yList.getModel())).addElement(3d);
        ((DefaultListModel<Double>) (yList.getModel())).addElement(4d);
        ((DefaultListModel<Double>) (yList.getModel())).addElement(5d);
        /**/

        JLabel errorLabel = new JLabel("error:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.anchor = WEST;
        gridBagConstraints.insets = new Insets(SPACE_HALF_INT, SPACE_HALF_INT, 0, SPACE_HALF_INT);
        frame.getContentPane().add(errorLabel, gridBagConstraints);

        errorField = new JFormattedTextField(format);
        errorField.setEditable(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.insets = new Insets(0, SPACE_HALF_INT, SPACE_INT, SPACE_HALF_INT);
        frame.getContentPane().add(errorField, gridBagConstraints);

        previewComponent = new ImageComponentMultiple();
        previewComponent.images.add(null);
        previewComponent.images.add(null);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 10;
        gridBagConstraints.insets = new Insets(SPACE_INT, SPACE_HALF_INT, SPACE_INT, SPACE_INT);
        frame.getContentPane().add(previewComponent, gridBagConstraints);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
