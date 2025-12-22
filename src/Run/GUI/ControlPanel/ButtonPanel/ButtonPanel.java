package Run.GUI.ControlPanel.ButtonPanel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;

import Run.World;
import Run.GUI.GUI;

public class ButtonPanel extends JPanel {

    private GUI gui;

    public ButtonPanel(World world, GUI gui) {
        this.gui = gui;

        this.setLayout(new GridLayout(2, 3, 6, 6));
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6));
        JButton paintButton = new JButton("Paint");
        JButton moreWorldInfoButton = new JButton("More World Info");
        JButton saveWorldButton = new JButton("Save World");
        JButton newWorldButton = new JButton("New World");
        JButton worldOptionsButton = new JButton("World Options");
        JButton debugOptionsButton = new JButton("Debug Options");

        paintButton.addActionListener(paintButtonClicked);
        moreWorldInfoButton.addActionListener(moreWorldInfoButtonClicked);
        saveWorldButton.addActionListener(saveWorldButtonClicked);
        newWorldButton.addActionListener(newWorldButtonClicked);
        worldOptionsButton.addActionListener(worldOptionsButtonClicked);
        debugOptionsButton.addActionListener(debugOptionsButtonClicked);

        this.add(paintButton);
        this.add(moreWorldInfoButton);
        this.add(saveWorldButton);
        this.add(newWorldButton);
        this.add(worldOptionsButton);
        this.add(debugOptionsButton);
    }

    private final java.awt.event.ActionListener paintButtonClicked = e -> {
        // gui.getControlPanel().getInfoPanel().setToPaintOptions();
    };

    private final java.awt.event.ActionListener moreWorldInfoButtonClicked = e -> {
        // gui.getControlPanel().getInfoPanel().setToWorldInfo();
    };

    private final java.awt.event.ActionListener saveWorldButtonClicked = e -> {
        
    };

    private final java.awt.event.ActionListener newWorldButtonClicked = e -> {
        NewWorldDialog.showNewWorldDialog(gui.getFrame());
    };

    private final java.awt.event.ActionListener worldOptionsButtonClicked = e -> {
        
    };

    private final java.awt.event.ActionListener debugOptionsButtonClicked = e -> {
        
    };
}
