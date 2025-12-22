package Run.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import Run.GUI.ControlPanel.ControlPanel;
import Run.World.World;


public class GUI {

    private World world;
    private JFrame frame;
    private JPanel mainPanel;
    private WorldPanel worldPanel;
    private ControlPanel controlPanel;
    
    public GUI(World world) {
        this.world = world;
    }
    
    public void run() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Evolution Simulator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            
            
            
            worldPanel = new WorldPanel(world, this);
            
            controlPanel = new ControlPanel(world, this);
            
            mainPanel = new JPanel(new BorderLayout());

            mainPanel.addComponentListener(new ComponentAdapter() {
                @Override public void componentResized(ComponentEvent e) {
                    worldPanel.setMaximumSize(new Dimension((int)(mainPanel.getWidth() * 0.75), mainPanel.getHeight()));
                    controlPanel.setMinimumSize(new Dimension((int)(mainPanel.getWidth() * 0.1), mainPanel.getHeight()));
                }
            });


            mainPanel.add(worldPanel, BorderLayout.CENTER);
            mainPanel.add(controlPanel, BorderLayout.EAST);
            
            frame.add(mainPanel);
            frame.setVisible(true);
        });
        
        
    }

    public void updateAfterTick(long tickStartTime) {
        controlPanel.updateAfterTick();
        worldPanel.updateAfterTick();
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
        this.world = world;
        worldPanel = new WorldPanel(world, this);
        controlPanel = new ControlPanel(world, this);
        mainPanel.removeAll();
        mainPanel.add(worldPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.EAST);
        mainPanel.revalidate();
        mainPanel.repaint();
        worldPanel.repaint();
    }
}