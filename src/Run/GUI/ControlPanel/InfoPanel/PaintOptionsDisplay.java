package Run.GUI.ControlPanel.InfoPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

import Run.Main;
import Run.GUI.GUI;
import Run.Paint.PaintCloneOf;
import Run.Paint.PaintFood;
import Run.Paint.PaintNothing;
import Run.Paint.PaintOption;
import Run.Paint.PaintRandomAnimal;
import Run.Paint.PaintRandomEgg;
import Run.Paint.PaintWall;
import Run.World.World;
import Things.Helpers.Position;

public class PaintOptionsDisplay extends UpdatableDisplayPanel {
    private PaintOption paintOption;
    private JButton cloneSelectedButton;
    private List<JButton> paintButtons = new ArrayList<>();
    private World world;
    private ThingInfoDisplay thingInfoDisplay;
    
    public PaintOptionsDisplay(World world, GUI gui) {
        this.world = world;

        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createRightPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setContinuousLayout(true);
        splitPane.setResizeWeight(0.0); // left keeps its size, right takes extra space
        splitPane.setDividerLocation(150);

        leftPanel.setMinimumSize(new Dimension(150, 100));
        rightPanel.setMinimumSize(new Dimension(200, 150));

        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1));
    
        JButton wallButton = new JButton("Wall");
        wallButton.addActionListener(e -> { paintOption = new PaintWall(world); resetColors(); wallButton.setBackground(Color.GREEN); });
        
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> { paintOption = new PaintNothing(world); resetColors(); removeButton.setBackground(Color.GREEN); });
        
        cloneSelectedButton = new JButton("Clone Selected");
        cloneSelectedButton.addActionListener(e -> {
            resetColors();
            if (Main.getSelectedThing() != null) {
                paintOption = new PaintCloneOf(world, Main.getSelectedThing());
                cloneSelectedButton.setBackground(Color.GREEN);
            } else {
                paintOption = null;
                cloneSelectedButton.setBackground(Color.RED);
                cloneSelectedButton.setText("Select a Thing first!");
            }
        });
        
        JButton foodButton = new JButton("Food");
        foodButton.addActionListener(e -> { paintOption = new PaintFood(world); resetColors(); foodButton.setBackground(Color.GREEN); });
        
        JButton randomAnimalButton = new JButton("Random Animal");
        randomAnimalButton.addActionListener(e -> { paintOption = new PaintRandomAnimal(world); resetColors(); randomAnimalButton.setBackground(Color.GREEN); });
        
        JButton randomEggButton = new JButton("Random Egg");
        randomEggButton.addActionListener(e -> { paintOption = new PaintRandomEgg(world); resetColors(); randomEggButton.setBackground(Color.GREEN); });
        
        paintButtons.add(randomEggButton);
        paintButtons.add(wallButton);
        paintButtons.add(removeButton);
        paintButtons.add(cloneSelectedButton);
        paintButtons.add(foodButton);
        paintButtons.add(randomAnimalButton);
        
        panel.add(wallButton);
        panel.add(removeButton);
        panel.add(cloneSelectedButton);
        panel.add(foodButton);
        panel.add(randomAnimalButton);
        panel.add(randomEggButton);

        return panel;
    }

    private JPanel createRightPanel() {
        thingInfoDisplay = new ThingInfoDisplay(world, null);
        thingInfoDisplay.setMinimumSize(new Dimension(200, 150));
        thingInfoDisplay.setPreferredSize(new Dimension(300, 200));

        JTextPane paintInfoPanel = new JTextPane();
        paintInfoPanel.setEditable(false);
        paintInfoPanel.setText("Right Click to paint, Left Click to select.");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(thingInfoDisplay, BorderLayout.CENTER);
        panel.add(paintInfoPanel, BorderLayout.SOUTH);
        panel.setMinimumSize(new Dimension(200, 150));
        return panel;
    }

    private void resetColors() {
        for (JButton button : paintButtons) {
            button.setBackground(null);
        }
    }

    @Override
    public void click(Position position, MouseEvent event) {
        if (event.getButton() == MouseEvent.BUTTON3) {
            if (paintOption == null) return;
            paintOption.paint(position);
        } else if (event.getButton() == MouseEvent.BUTTON1 && Main.getSelectedThing() != null && (paintOption == null || paintOption instanceof PaintCloneOf)) {
            cloneSelectedButton.setBackground(Color.GREEN);
            paintOption = new PaintCloneOf(world, Main.getSelectedThing());
            cloneSelectedButton.setText("Clone Selected");
            thingInfoDisplay.click(position, event);
        }
    }

    @Override
    public void update() {
        thingInfoDisplay.update();
    }
}
