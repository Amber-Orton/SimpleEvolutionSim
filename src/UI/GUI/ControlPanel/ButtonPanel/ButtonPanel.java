package UI.GUI.ControlPanel.ButtonPanel;

import java.awt.GridLayout;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

import Main.World.World;
import UI.GUI.GUI;
import UI.GUI.ControlPanel.InfoPanel.InfoPanelVersions;

public class ButtonPanel extends JPanel {

    private GUI gui;
    private List<JButton> buttons = new ArrayList<>();

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

        buttons.add(paintButton);
        buttons.add(moreWorldInfoButton);
        buttons.add(saveWorldButton);
        buttons.add(newWorldButton);
        buttons.add(worldOptionsButton);
        buttons.add(debugOptionsButton);

        this.add(paintButton);
        this.add(moreWorldInfoButton);
        this.add(saveWorldButton);
        this.add(newWorldButton);
        this.add(worldOptionsButton);
        this.add(debugOptionsButton);
    }

    private final java.awt.event.ActionListener paintButtonClicked = e -> {
        if (gui.getControlPanel().getInfoPanel().getCurrentVersion() != InfoPanelVersions.PaintOptions) {
            gui.getControlPanel().getInfoPanel().setTo(InfoPanelVersions.PaintOptions);
            resetButtonColors();
            ((JButton) e.getSource()).setBackground(java.awt.Color.GREEN);
        } else {
            gui.getControlPanel().getInfoPanel().setTo(InfoPanelVersions.ThingInfo);
            ((JButton) e.getSource()).setBackground(null);
        }
    };

    private final java.awt.event.ActionListener moreWorldInfoButtonClicked = e -> {
        if (gui.getControlPanel().getInfoPanel().getCurrentVersion() != InfoPanelVersions.WorldInfo) {
            gui.getControlPanel().getInfoPanel().setTo(InfoPanelVersions.WorldInfo);
            resetButtonColors();
            ((JButton) e.getSource()).setBackground(java.awt.Color.GREEN);
        } else {
            gui.getControlPanel().getInfoPanel().setTo(InfoPanelVersions.ThingInfo);
            ((JButton) e.getSource()).setBackground(null);
        }
    };

    private void resetButtonColors() {
        for (JButton button : buttons) {
            button.setBackground(null);
        }
    }

    private final java.awt.event.ActionListener saveWorldButtonClicked = e -> {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement world saving and loading
    };

    private final java.awt.event.ActionListener newWorldButtonClicked = e -> {
        NewWorldDialog.showNewWorldDialog(gui.getFrame());
    };

    private final java.awt.event.ActionListener worldOptionsButtonClicked = e -> {
        WorldOptionsDialog.showWorldOptionsDialog(gui.getFrame());
    };

    private final java.awt.event.ActionListener debugOptionsButtonClicked = e -> {
        DebugOptionsDialog.showDebugOptionsDialog(gui.getFrame());
    };
}
