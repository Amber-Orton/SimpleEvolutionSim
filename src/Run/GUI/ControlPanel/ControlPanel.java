package Run.GUI.ControlPanel;

import java.awt.BorderLayout;
import Run.World;
import Run.GUI.GUI;
import Run.GUI.UpdateableJPanel;
import Run.GUI.ControlPanel.InfoPanel.InfoPanel;
import Things.Helpers.Position;

// Control panel on the right side of the GUI
public class ControlPanel extends UpdateableJPanel {
    private final World world;
    private final TickPanel tickPanel;
    private final InfoPanel infoPanel;

    public ControlPanel(World world, GUI gui) {
        this.world = world;
        this.setLayout(new BorderLayout());


        tickPanel = new TickPanel(world, gui);
        this.add(tickPanel, BorderLayout.NORTH);

        infoPanel = new InfoPanel(world, gui);
        this.add(infoPanel, BorderLayout.CENTER);
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
}
