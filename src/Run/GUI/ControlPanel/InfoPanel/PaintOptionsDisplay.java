package Run.GUI.ControlPanel.InfoPanel;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

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
    
    public PaintOptionsDisplay(World world, GUI gui) {
        this.world = world;

        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();

        leftPanel.setLayout(new GridLayout(2, 3));

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
        
        leftPanel.add(wallButton);
        leftPanel.add(removeButton);
        leftPanel.add(cloneSelectedButton);
        leftPanel.add(foodButton);
        leftPanel.add(randomAnimalButton);
        leftPanel.add(randomEggButton);

        JScrollPane rightScroll = new JScrollPane(rightPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightScroll);
        this.add(splitPane);
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
            System.out.println("Painting at " + position);
            paintOption.paint(position);
        } else if (event.getButton() == MouseEvent.BUTTON1 && Main.getSelectedThing() != null && (paintOption == null || paintOption instanceof PaintCloneOf)) {
            cloneSelectedButton.setBackground(Color.GREEN);
            paintOption = new PaintCloneOf(world, Main.getSelectedThing());
            cloneSelectedButton.setText("Clone Selected");
        }
    }

    @Override
    public void updateAfterTick() {
        // No dynamic content to update
    }
}
