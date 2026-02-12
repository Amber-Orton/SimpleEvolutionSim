package UI.GUI.ControlPanel.ButtonPanel;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import Main.Main;

public class NewWorldDialog {

    public static void showNewWorldDialog(Component parentRef) {
        parentRef.setVisible(false);
        Window mainWin = (parentRef instanceof Window) ? (Window) parentRef : SwingUtilities.getWindowAncestor(parentRef);
        JDialog dlg = new JDialog(mainWin, "Create New World", Dialog.ModalityType.APPLICATION_MODAL);

        JPanel content = new JPanel(new GridLayout(0, 2, 8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        content.add(new JLabel("World Width (recommend 5-200):"));
        JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(Main.getWORLD_WIDTH(), 1, Integer.MAX_VALUE, 1));
        content.add(widthSpinner);

        content.add(new JLabel("World Height (recommend 5-200):"));
        JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(Main.getWORLD_HEIGHT(), 1, Integer.MAX_VALUE, 1));
        content.add(heightSpinner);

        content.add(new JLabel("Initial Max Neural Net Layers (recommend 1-20):"));
        JSpinner layersSpinner = new JSpinner(new SpinnerNumberModel(Main.getMAX_LAYERS(), 1, Integer.MAX_VALUE, 1));
        content.add(layersSpinner);

        content.add(new JLabel("Initial Animal Density (0.00 - 1.00):"));
        JSpinner densitySpinner = new JSpinner(new SpinnerNumberModel((double) Main.getINITIAL_ANIMAL_DENSITY(), 0.0, 1.0, 0.01));
        ((JSpinner.NumberEditor) densitySpinner.getEditor()).getFormat().setMinimumFractionDigits(2);
        content.add(densitySpinner);

        content.add(new JLabel("Initial Neural Net Randomness (0.00 - 1.00):"));
        JSpinner randomnessSpinner = new JSpinner(new SpinnerNumberModel((double) Main.getINITIAL_NEURAL_NET_RANDOMNESS(), 0.0, 1.0, 0.01));
        ((JSpinner.NumberEditor) randomnessSpinner.getEditor()).getFormat().setMinimumFractionDigits(2);
        content.add(randomnessSpinner);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        JButton create = new JButton("Create");
        buttons.add(cancel);
        buttons.add(create);

        JPanel wrapper = new JPanel(new GridLayout(2, 1, 0, 8));
        wrapper.add(content);
        wrapper.add(buttons);

        cancel.addActionListener(ev -> {dlg.dispose(); parentRef.setVisible(true);});

        create.addActionListener(ev -> {
            int newW = ((Number) widthSpinner.getValue()).intValue();
            int newH = ((Number) heightSpinner.getValue()).intValue();
            int newMaxLayers = ((Number) layersSpinner.getValue()).intValue();
            float newDensity = ((Number) densitySpinner.getValue()).floatValue();
            float newRandomness = ((Number) randomnessSpinner.getValue()).floatValue();

            Main.setWORLD_WIDTH(newW);
            Main.setWORLD_HEIGHT(newH);
            Main.setMAX_LAYERS(newMaxLayers);
            Main.setINITIAL_ANIMAL_DENSITY(newDensity);
            Main.setINITIAL_NEURAL_NET_RANDOMNESS(newRandomness);
            Main.updateAttributes();

            Main.createAndRunNewWorld();

            dlg.dispose();
            parentRef.setVisible(true);
        });

        dlg.setContentPane(wrapper);
        dlg.pack();
        dlg.setLocationRelativeTo(mainWin);
        dlg.setResizable(false);
        dlg.setVisible(true);
    }
}
