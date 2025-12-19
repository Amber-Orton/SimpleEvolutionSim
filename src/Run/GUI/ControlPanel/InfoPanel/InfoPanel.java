package Run.GUI.ControlPanel.InfoPanel;

import java.awt.BorderLayout;
import javax.swing.border.TitledBorder;
import Run.World;
import Run.GUI.GUI;
import Run.GUI.UpdateableJPanel;
import Things.Helpers.Position;

public class InfoPanel extends UpdateableJPanel {

    private World world;
    private InfoPanelVersions currentVersion;

    private ThingInfoDisplay thingInfoDisplay;
    private TitledBorder border;
    
    public InfoPanel(World world, GUI gui) {
        this.currentVersion = InfoPanelVersions.ThingInfo;
        this.world = world;

        this.border = new TitledBorder("Select to see info");
        this.setBorder(border);
        thingInfoDisplay = new ThingInfoDisplay(world, gui);
        this.setLayout(new BorderLayout());

        this.add(thingInfoDisplay, BorderLayout.CENTER);
    }

    @Override
    public void updateAfterTick() {
        switch (currentVersion) {
            case InfoPanelVersions.ThingInfo:
                thingInfoDisplay.updateAfterTick();
                break;
            case InfoPanelVersions.PaintOptions:
                throw new UnsupportedOperationException("PaintOptions not implemented yet.");
                // break;
            case InfoPanelVersions.WorldInfo:
                throw new UnsupportedOperationException("WorldInfo not implemented yet.");
                // break;
            default:
                throw new IllegalStateException("Unexpected value: " + currentVersion);

        }
    }

    public void click(Position position) {
        if (currentVersion == InfoPanelVersions.ThingInfo) {
            thingInfoDisplay.click(position);
            border.setTitle("Info - " + world.getThingAt(position).getName());
            this.repaint();
        }
    }
}