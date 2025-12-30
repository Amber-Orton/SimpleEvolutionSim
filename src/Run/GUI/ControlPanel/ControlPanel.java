package Run.GUI.ControlPanel;

import java.awt.BorderLayout;


import Run.GUI.GUI;
import Run.GUI.UpdateableJPanel;
import Run.GUI.ControlPanel.ButtonPanel.ButtonPanel;
import Run.GUI.ControlPanel.InfoPanel.InfoPanel;
import Run.World.World;
import Things.Helpers.Position;

// Control panel on the right side of the GUI
public class ControlPanel extends UpdateableJPanel {
    private final TickPanel tickPanel;
    private final InfoPanel infoPanel;
    private final ButtonPanel buttonPanel;

    public ControlPanel(World world, GUI gui) {
        this.setLayout(new BorderLayout());


        tickPanel = new TickPanel(world, gui);
        this.add(tickPanel, BorderLayout.NORTH);

        infoPanel = new InfoPanel(world, gui);
        this.add(infoPanel, BorderLayout.CENTER);

        buttonPanel = new ButtonPanel(world, gui);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    public void click(Position position) {
        infoPanel.click(position);
    }

    public void updateAfterTick() {
        tickPanel.updateAfterTick();
        infoPanel.updateAfterTick();
    }

    public void updatePlayPauseButton() {
        tickPanel.updatePlayPauseButton();
    }

    public InfoPanel getInfoPanel() {
        return infoPanel;
    }
}
