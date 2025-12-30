package Run.GUI.ControlPanel.InfoPanel;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.border.TitledBorder;

import Logger.Logger;
import Run.GUI.GUI;
import Run.GUI.UpdateableJPanel;
import Run.World.World;
import Things.Helpers.Position;

public class InfoPanel extends UpdateableJPanel {

    private World world;
    private GUI gui;
    private InfoPanelVersions currentVersion;

    private UpdateableJPanel panelInUse;
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
    public void updateAfterTick() {
        panelInUse.updateAfterTick();
    }

    public void click(Position position) {
        if (currentVersion == InfoPanelVersions.ThingInfo) {
            try {
                ((ThingInfoDisplay) panelInUse).click(position);
            } catch (ClassCastException e) {
                Logger.logError("InfoPanel clicked: ", e.getMessage());
                throw new IllegalStateException("Panel in use is not ThingInfoDisplay");
            }
            border.setTitle("Info - " + world.getThingAt(position).getName());
            this.repaint();
        }
    }


    public InfoPanelVersions getCurrentVersion() {
        return currentVersion;
    }

    public void setTo(InfoPanelVersions version) {
        this.currentVersion = version;

        this.removeAll();
        UpdateableJPanel newPanel;
        switch (version) {
            case InfoPanelVersions.ThingInfo:
                newPanel = new ThingInfoDisplay(world, gui);
                border.setTitle("Select to see info");
                break;
            case InfoPanelVersions.PaintOptions:
                throw new UnsupportedOperationException("PaintOptions not implemented yet.");
            case InfoPanelVersions.WorldInfo:
                newPanel = new WorldInfoDisplay(world, gui);
                border.setTitle("World Information");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + version);
        }

        panelInUse = newPanel;
        this.add(panelInUse, BorderLayout.CENTER);
        panelInUse.revalidate();
        this.revalidate();
        this.repaint();
    }
}