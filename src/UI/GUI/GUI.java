package UI.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import Main.Main;
import Main.World.World;
import Things.Thing;
import Things.Helpers.Position;
import UI.GUI.ControlPanel.ControlPanel;
import UI.GUI.ControlPanel.ButtonPanel.DebugOptionsDialog;
import UI.GUI.ControlPanel.ButtonPanel.NewWorldDialog;


public class GUI {

    private World world;
    private JFrame frame;
    private JPanel mainPanel;
    private WorldPanel worldPanel;
    private ControlPanel controlPanel;

    private Position selectedPosition;
    
    
    public void run(boolean showOptionsOnFirstOpen) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Evolution Simulator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setMinimumSize(new Dimension(625, 300));

            
            
            
            worldPanel = new WorldPanel(world, this);
            
            controlPanel = new ControlPanel(world, this);

            frame.addComponentListener(new ComponentAdapter() {
                @Override public void componentResized(ComponentEvent e) {
                    worldPanel.setSize();
                }
            });
            
            mainPanel = new JPanel(new BorderLayout());

            mainPanel.add(worldPanel, BorderLayout.WEST);
            mainPanel.add(controlPanel, BorderLayout.CENTER);
            
            frame.add(mainPanel);
            if (showOptionsOnFirstOpen) {
                NewWorldDialog.showNewWorldDialog(frame);
            } else {
                frame.setVisible(true);
            }
        });
        
        
    }

    public void click(Position position, MouseEvent e) {
        controlPanel.click(position, e);
    }

    public void update(long tickStartTime) {
        controlPanel.update();
        worldPanel.update();
    }
    
    public WorldPanel getWorldPanel() {
        return worldPanel;
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void updatePlayPauseButton() {
        controlPanel.updatePlayPauseButton();
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setWorld(World world) {
        if (this.world == null) {
            this.world = world;
            return;
        }
        this.world = world;
        worldPanel = new WorldPanel(world, this);
        controlPanel = new ControlPanel(world, this);
        mainPanel.removeAll();
        mainPanel.add(worldPanel, BorderLayout.WEST);
        mainPanel.add(controlPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
        worldPanel.repaint();
    }
}