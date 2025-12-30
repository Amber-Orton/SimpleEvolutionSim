package Run.GUI.ControlPanel;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;

import Run.GUI.GUI;
import Run.GUI.UpdatableJPanel;
import Run.GUI.ControlPanel.ButtonPanel.ButtonPanel;
import Run.GUI.ControlPanel.InfoPanel.InfoPanel;
import Run.World.World;
import Things.Helpers.Position;

// Control panel on the right side of the GUI
public class ControlPanel extends UpdatableJPanel {
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

    public void click(Position position, MouseEvent e) {
        infoPanel.click(position, e);
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
