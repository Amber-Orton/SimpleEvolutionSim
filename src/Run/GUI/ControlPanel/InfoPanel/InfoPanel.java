package Run.GUI.ControlPanel.InfoPanel;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;

import javax.swing.border.TitledBorder;

import Run.GUI.GUI;
import Run.GUI.UpdatableJPanel;
import Run.World.World;
import Things.Helpers.Position;

public class InfoPanel extends UpdatableJPanel {

    private World world;
    private GUI gui;
    private InfoPanelVersions currentVersion;

    private UpdatableDisplayPanel panelInUse;
    private TitledBorder border;
    
    public InfoPanel(World world, GUI gui) {
        this.currentVersion = InfoPanelVersions.ThingInfo;
        this.world = world;
        this.gui = gui;

        this.border = new TitledBorder("Select to see info");
        this.setBorder(border);
        panelInUse = new ThingInfoDisplay(world, gui);
        this.setLayout(new BorderLayout());

        this.add(panelInUse, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panelInUse.setSize(getWidth(), getHeight());
            }
        });
    }

    @Override
    public void update() {
        panelInUse.update();
    }

    public void click(Position position, MouseEvent event) {
        panelInUse.click(position, event);
        if (currentVersion == InfoPanelVersions.ThingInfo) {
            border.setTitle("Info - " + world.getThingAt(position).getName());
        }
        this.repaint();
    }


    public InfoPanelVersions getCurrentVersion() {
        return currentVersion;
    }

    public void setTo(InfoPanelVersions version) {
        this.currentVersion = version;

        this.removeAll();
        switch (version) {
            case InfoPanelVersions.ThingInfo:
                panelInUse = new ThingInfoDisplay(world, gui);
                border.setTitle("Select to see info");
                break;
            case InfoPanelVersions.PaintOptions:
                panelInUse = new PaintOptionsDisplay(world, gui);
                border.setTitle("Paint");
                break;
            case InfoPanelVersions.WorldInfo:
                panelInUse = new WorldInfoDisplay(world, gui);
                border.setTitle("World Information");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + version);
        }

        this.add(panelInUse, BorderLayout.CENTER);
        panelInUse.revalidate();
        this.revalidate();
        this.repaint();
    }
}