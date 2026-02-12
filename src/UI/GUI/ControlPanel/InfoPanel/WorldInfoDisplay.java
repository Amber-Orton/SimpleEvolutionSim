package UI.GUI.ControlPanel.InfoPanel;

import javax.swing.text.JTextComponent;

import Main.World.World;
import Things.Animal;
import Things.Egg;
import Things.Food;
import Things.Wall;
import UI.GUI.GUI;

public class WorldInfoDisplay extends UpdatableDisplayPanel {

    private JTextComponent infoArea;
    private World world;

    public WorldInfoDisplay(World world, GUI gui) {
        this.world = world;
        this.infoArea = new javax.swing.JTextArea();
        this.infoArea.setEditable(false);
        displayWorldInfo();

        this.add(infoArea);
    }

    private void displayWorldInfo() {
        StringBuilder info = new StringBuilder();
        info.append("World Size: ").append(world.getWidth()).append(" x ").append(world.getHeight()).append("\n");
        info.append("Things: ").append(world.getThings().size()).append("\n");
        info.append("Animals: ").append(world.getThings(Animal.class).size()).append("\n");
        info.append("Foods: ").append(world.getThings(Food.class).size()).append("\n");
        info.append("Walls: ").append(world.getThings(Wall.class).size()).append("\n");
        info.append("Eggs: ").append(world.getThings(Egg.class).size()).append(" (some may be hidden)\n");

        infoArea.setText(info.toString());
    }

    public void update() {
        displayWorldInfo();
    }

    @Override
    public void click(Things.Helpers.Position position, java.awt.event.MouseEvent event) {
        // TODO: add selected thing info display on right
    }
}
