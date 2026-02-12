package Run.GUI.ControlPanel.InfoPanel;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;

import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import Run.GUI.GUI;
import Run.World.World;
import Things.Animal;
import Things.Egg;
import Things.Food;
import Things.Thing;
import Things.Wall;
import Things.Helpers.Position;

/**
 * Handles displaying information about Things in the GUI text areas.
 */
class ThingInfoDisplay  extends UpdatableDisplayPanel {

    private Thing currentThing;

    private World world;

    private JSplitPane splitPane;
    private JTextArea leftArea;
    private JTextArea rightArea;

    public ThingInfoDisplay(World world, GUI gui) {
        this.world = world;

        this.setLayout(new BorderLayout());

        this.leftArea = new JTextArea();
        this.leftArea.setLineWrap(true);
        this.leftArea.setWrapStyleWord(true);
        JScrollPane leftScroll = new JScrollPane(leftArea);


        this.rightArea = new JTextArea();
        this.rightArea.setLineWrap(true);
        this.rightArea.setWrapStyleWord(true);
        JScrollPane rightScroll = new JScrollPane(rightArea);


        this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll, rightScroll);
        this.splitPane.setResizeWeight(0.5);
        this.splitPane.setContinuousLayout(true);

        this.add(splitPane, BorderLayout.CENTER);
    }

    public void click(Position position, MouseEvent event) {
        currentThing = world.getThingAt(position);
        display();
    }

    public void display() {
        if (currentThing instanceof Animal) {
            showAnimalInfo((Animal) currentThing);
        } else if (currentThing instanceof Egg) {
            showEggInfo((Egg) currentThing);
        } else if (currentThing instanceof Wall) {
            showWallInfo();
        } else if (currentThing instanceof Food) {
            showFoodInfo((Food) currentThing);
        } else {
            showErrorMessage();
        }
    }

    private void showAnimalInfo(Animal animal) {
        leftArea.setText(
                "Health: " + animal.getHealth() + '/' + animal.getAnimalAttributes().getMaxHealth()
                        + "\nEnergy: " + animal.getEnergy() + '/' + animal.getAnimalAttributes().getMaxEnergy()
                        + "\nAttack Damage: " + animal.getAnimalAttributes().getAttackDamage()
                        + "\nReproduction Cost: " + animal.getAnimalAttributes().getReproductionCost()
                        + "\nFacing: " + animal.getFacingDirection()
                        + "\nParent: " + (animal.getName().contains("NoParent") ? "None" : animal.getName().split(" ")[1])
                        + "\nLast Action: " + animal.getAction());
        if (animal.getHealth() <= 0) {
            rightArea.setText("This animal is dead.");
        } else {
            rightArea.setText(animal.getAnimalAttributes().getNeuralNet().toString());
        }
    }

    private void showEggInfo(Egg egg) {
        if (!egg.isHatched()) {
            leftArea.setText(
                    "Max Health: " + egg.getAnimalAttributes().getMaxHealth()
                            + "\nMax Energy: " + egg.getAnimalAttributes().getMaxEnergy()
                            + "\nAttack Damage: " + egg.getAnimalAttributes().getAttackDamage()
                            + "\nStored Energy: " + egg.getAnimalAttributes().getReproductionCost());
            rightArea.setText(
                    "Parent: " + egg.getParent().getName()
                            + "\nCycles to Hatch: " + egg.getCyclesToHatch());
        } else {
            currentThing = egg.getChild();
            display();
        }
    }

    private void showWallInfo() {
        leftArea.setText("Wall");
        rightArea.setText("Walls are impassable barriers.");
    }

    private void showFoodInfo(Food food) {
        leftArea.setText("Energy: " + food.getEnergy());
        rightArea.setText(
                "Food is a source of energy and can be eaten by animals, \nspawns from nothing and from dead animals.");
    }

    private void showErrorMessage() {
        leftArea.setText("Something went wrong invalid selection");
        rightArea.setText("");
    }

    @Override
    public void update() {
        display();
    }
}