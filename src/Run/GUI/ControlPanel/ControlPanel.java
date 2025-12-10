package Run.GUI.ControlPanel;

import java.awt.BorderLayout;
import Run.World;
import Run.GUI.GUI;
import Run.GUI.UpdateableJPanel;
import Things.Helpers.Position;

// Control panel on the right side of the GUI
public class ControlPanel extends UpdateableJPanel {
    private final World world;
    private final TickPanel tickPanel;

    public ControlPanel(World world, GUI gui) {
        this.world = world;
        this.setLayout(new BorderLayout());


        tickPanel = new TickPanel(world, gui);
        this.add(tickPanel, BorderLayout.NORTH);
    }

    public void click(Position position) {
        // TODO: placeholder
    }

    public void updateAfterTick() {
        tickPanel.updateAfterTick();
    }

    public void updatePlayPauseButton() {
        tickPanel.updatePlayPauseButton();
    }
}
