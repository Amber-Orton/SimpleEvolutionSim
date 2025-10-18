package Run;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI {
    private World world;
    private static GUI instance;
    private JPanel[][] gridPanels; // 2D array to hold the grid panels


    /** 
     * Singleton pattern.
     * If an instance already exists, change its world to the new one
     */
    public static GUI getInstanceOrChangeWorld(World world) {
        if (instance == null) {
            instance = new GUI(world);
        }
        else {
            instance.world = world;
        }
        return instance;
    }

    private GUI(World world) {
        this.world = world;
    }

    public void run() {
        
        // Create the main frame
        JFrame frame = new JFrame("Simple Evolution Sim");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(new BorderLayout());

        // Create the grid panel
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(world.getWidth(), world.getHeight()));
        gridPanels = new JPanel[world.getWidth()][world.getHeight()];

        // Initialize the grid with panels
        for (int row = 0; row < world.getWidth(); row++) {
            for (int col = 0; col < world.getHeight(); col++) {
                JPanel panel = new JPanel();
                gridPanels[row][col] = panel;
                gridPanel.add(panel);
            }
        }
        
        // Add the grid panel to the frame
        frame.add(gridPanel, BorderLayout.CENTER);
        updateWorldView();

        // Create a control panel with a button to tick
        JPanel controlPanel = new JPanel();
        JButton tickButton = new JButton("Tick");
        controlPanel.add(tickButton);

        // Add action listener to the button
        tickButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                world.tick();
                updateWorldView();
            }
        });

        // Add the control panel to the frame and set visible
        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    /** 
     * update the world with up to date Things
     */ 
    private void updateWorldView() {
        Color[][] grid = world.getGridOfColors();
        for (int row = 0; row < world.getWidth(); row++) {
            for (int col = 0; col < world.getHeight(); col++) {
                gridPanels[row][col].setBackground(grid[row][col]);//potential efficincy gain: only updating changed cells? store previous grid? maybe too much extra storage?
            }
        }
    }
}