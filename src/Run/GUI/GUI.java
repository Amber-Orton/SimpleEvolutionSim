package Run.GUI;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import Run.World;
import Run.GUI.ControlPanel.ControlPanel;


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
            
            
            
            worldPanel = new WorldPanel(this, world);
            
            controlPanel = new ControlPanel(world, this);
            
            mainPanel = new JPanel(new BorderLayout());
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
}