package Run.GUI.ControlPanel.ButtonPanel;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import java.util.Hashtable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import Run.Main;

public class WorldOptionsDialog {
    public static void showWorldOptionsDialog(Component parentRef) {
        Window mainWin = (parentRef instanceof Window) ? (Window) parentRef : SwingUtilities.getWindowAncestor(parentRef);
        JDialog dlg = new JDialog(mainWin, "Create New World", Dialog.ModalityType.MODELESS);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel wrapper = new JPanel();
        JScrollPane scrollPane = new JScrollPane(wrapper);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        wrapper.add(BorderedSliderFactory.createBorderedSlider(
            "Animal Stats Total",
            50,
            200,
            1.0,
            null,
            (double) Main.getANIMAL_STAT_TOTAL(),
            1.0,
            (newValue) -> {
                Main.setANIMAL_STAT_TOTAL(newValue.intValue());
            }
        ));

        wrapper.add(BorderedSliderFactory.createBorderedSlider(
            "Animal Existenence Cost",
            0,
            10,
            0.0,
            null,
            (double) Main.getANIMAL_EXISTENCE_COST(),
            1.0,
            (newValue) -> {
                Main.setANIMAL_EXISTENCE_COST(newValue.intValue());
            }
        ));

        wrapper.add(BorderedSliderFactory.createBorderedSlider(
            "Animal Attack Cost",
            0,
            10,
            0.0,
            null,
            (double) Main.getANIMAL_ATTACK_COST(),
            1.0,
            (newValue) -> {
                Main.setANIMAL_ATTACK_COST(newValue.intValue());
            }
        ));

        wrapper.add(BorderedSliderFactory.createBorderedSlider(
            "Animal Move Cost",
            0,
            10,
            0.0,
            null,
            (double) Main.getANIMAL_MOVE_COST(),
            1.0,
            (newValue) -> {
                Main.setANIMAL_MOVE_COST(newValue.intValue());
            }
        ));

        wrapper.add(BorderedSliderFactory.createBorderedSlider(
            "Animal Eat Cost",
            0,
            10,
            0.0,
            null,
            (double) Main.getANIMAL_EAT_COST(),
            1.0,
            (newValue) -> {
                Main.setANIMAL_EAT_COST(newValue.intValue());
            }
        ));

        wrapper.add(BorderedSliderFactory.createBorderedSlider(
            "Animal Turn Cost",
            0,
            10,
            0.0,
            null,
            (double) Main.getANIMAL_TURN_COST(),
            1.0,
            (newValue) -> {
                Main.setANIMAL_TURN_COST(newValue.intValue());
            }
        ));

        wrapper.add(BorderedSliderFactory.createBorderedSlider(
            "Animal Rest Cost",
            0,
            10,
            0.0,
            null,
            (double) Main.getANIMAL_REST_COST(),
            1.0,
            (newValue) -> {
                Main.setANIMAL_REST_COST(newValue.intValue());
            }
        ));

        wrapper.add(BorderedSliderFactory.createBorderedSlider(
            "Animal Rest Heal Amount",
            0,
            10,
            0.0,
            null,
            (double) Main.getANIMAL_REST_HEAL_AMOUNT(),
            1.0,
            (newValue) -> {
                Main.setANIMAL_REST_HEAL_AMOUNT(newValue.intValue());
            }
        ));

        wrapper.add(BorderedSliderFactory.createBorderedSlider(
            "Egg Hatch Ticks",
            1,
            20,
            1.0,
            null,
            (double) Main.getEGG_HATCH_CYCLES(),
            1.0,
            (newValue) -> {
                Main.setEGG_HATCH_CYCLES(newValue.intValue());
            }
        ));

        wrapper.add(BorderedSliderFactory.createBorderedSlider(
            "Mutation Rate",
            0,
            1,
            0.0,
            1.0,
            Main.getMUTATION_RATE(),
            0.01,
            (newValue) -> {
                Main.setMUTATION_RATE(newValue.floatValue());
            }
        ));

        wrapper.add(BorderedSliderFactory.createBorderedSlider(
            "Mutation Factor",
            0,
            1,
            0.0,
            1.0,
            Main.getMUTATION_FACTOR(),
            0.01,
            (newValue) -> {
                Main.setMUTATION_FACTOR(newValue.floatValue());
            }
        ));

        wrapper.add(BorderedSliderFactory.createBorderedSlider(
            "Node Insert/Delete Rate",
            0,
            1,
            0.0,
            1.0,
            Main.getNODE_INSERT_OR_DELETE_RATE(),
            0.01,
            (newValue) -> {
                Main.setNODE_INSERT_OR_DELETE_RATE(newValue.floatValue());
            }
        ));

        wrapper.add(BorderedSliderFactory.createBorderedSlider(
            "Max Neural Net Layers",
            2,
            50,
            1.0,
            null,
            (double) Main.getMAX_LAYERS(),
            1.0,
            (newValue) -> {
                Main.setMAX_LAYERS(newValue.intValue());
            }
        ));

        wrapper.add(BorderedSliderFactory.createBorderedSlider(
            "Food Grow Rate",
            0,
            1,
            0.0,
            1.0,
            Main.getFOOD_GROW_RATE(),
            0.01,
            (newValue) -> {
                Main.setFOOD_GROW_RATE(newValue.floatValue());
            }
        ));

        wrapper.add(BorderedSliderFactory.createBorderedSlider(
            "New Food Energy",
            1,
            20,
            0.0,
            null,
            (double) Main.getNEW_FOOD_ENERGY(),
            1.0,
            (newValue) -> {
                Main.setNEW_FOOD_ENERGY(newValue.intValue());
            }
        ));

        dlg.setContentPane(scrollPane);
        dlg.pack();
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dlg.setSize(375, dlg.getHeight());
        dlg.setLocationRelativeTo(mainWin);
        dlg.setVisible(true);
    }
}






class BorderedSliderFactory {
    /**
     * Create a JPanel containing a JSlider with a border and a label showing the current value.
     * @param title The title for the border
     * @param min The minimum value on the slider
     * @param max The maximum value on the slider
     * @param maximumOverValue The maximum allowed value above the max parameter, null if any allowed, cannot be less than max
     * @param maximumUnderValue The maximum allowed value below the min parameter, null if any allowed, cannot be greater than min
     * @param initial The initial value
     * @param step The step size for the slider
     * @param onChange A Consumer that handles value changes
     * @return A JPanel containing the slider, label, and text field
     */
    protected static JPanel createBorderedSlider(String title, int min, int max, Double maximumUnderValue, Double maximumOverValue, double initial, double step, Consumer<Double> onChange) {
        JPanel panel = new JPanel();
        TitledBorder border = new TitledBorder(title);
        panel.setBorder(border);

        // Determine scaling as sliders only support integer values
        BigDecimal bdStep = BigDecimal.valueOf(step).stripTrailingZeros();
        int scalePow = Math.max(0, bdStep.scale());
        int scale = 1;
        for (int i = 0; i < scalePow; i++) scale *= 10;

        final int fScale = scale;

        int scaledMin = min * scale;
        int scaledMax = max * scale;
        int scaledInitial = (int) Math.round(initial * scale);
        int scaledStep = Math.max(1, (int) Math.round(step * scale));

        JSlider slider = new JSlider(scaledMin, scaledMax, scaledInitial);

        // compute major tick spacing
        double realRange = (double) (max - min);
        double rawMajorReal = realRange / 4.0; // aim for 4 major segments
        // round major to nearest multiple of step
        double majorReal = Math.max(step, Math.round(rawMajorReal / step) * step);
        int scaledMajor = Math.max(scaledStep, (int) Math.max(1, Math.round(majorReal * fScale)));
        slider.setMajorTickSpacing(scaledMajor);
        slider.setMinorTickSpacing(scaledStep);
        slider.setPaintTicks(true);

        // create DecimalFormat for labels and text field
        StringBuilder pattern = new StringBuilder("#");
        if (scale > 1) {
            pattern.append('.');
            for (int i = 0; i < scalePow; i++) pattern.append('0');
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());

        // create a label table with correct formatting
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        for (int i = 0; i <= 4; i++) {
            double labelReal = min + (realRange * i / 4.0);
            int labelScaled = (int) Math.round(labelReal * fScale);
            labelTable.put(labelScaled, new JLabel(df.format(labelReal)));
        }
        slider.setLabelTable(labelTable);
        slider.setPaintLabels(true);

        JLabel valueLabel = new JLabel("Value:");
        JTextField textField = new JTextField(df.format((double) initial), 6);

        slider.addChangeListener(e -> {
            double realValue = ((double) slider.getValue()) / fScale;
            onChange.accept(realValue);
            textField.setText(df.format(realValue));
        });
        textField.addActionListener(e -> {
            try {
                double parsed = Double.parseDouble(textField.getText());
                int v = (int) Math.round(parsed * fScale);
                if (maximumUnderValue != null && ((double) v) / fScale < maximumUnderValue) {
                    v = scaledMin;
                    textField.setText(df.format(((double) v) / fScale));
                }
                if (maximumOverValue != null && ((double) v) / fScale > maximumOverValue) {
                    v = scaledMax;
                    textField.setText(df.format(((double) v) / fScale));
                }
                onChange.accept(((double) v) / fScale);

                if (v < scaledMin) slider.setValue(scaledMin);
                else if (v > scaledMax) slider.setValue(scaledMax);
                else slider.setValue(v);
            } catch (NumberFormatException ex) {
                textField.setText(df.format(((double) slider.getValue()) / fScale));
            }
        });

        panel.add(slider);
        panel.add(valueLabel);
        panel.add(textField);

        return panel;
    }
}