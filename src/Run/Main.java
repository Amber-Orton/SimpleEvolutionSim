package Run;
import Things.AnimalAttributes;
import Things.Nothing;

public class Main {

    /*
     * world attributes
     * defaults have been provided
     */
    protected static int ANIMAL_STAT_TOTAL = 100;
    protected static int ANIMAL_ATTACK_COST = 5;
    protected static float MUTATION_RATE = 0.1f;
    protected static float MUTATION_FACTOR = 0.1f;
    protected static float NODE_INSERT_OR_DELETE_RATE = 0.01f;
    protected static int MAX_LAYERS = 5;
    protected static int WORLD_WIDTH = 10;
    protected static int WORLD_HEIGHT = 10;
    protected static float INITIAL_ANIMAL_DENSITY = 0.1f;
    protected static float FOOD_GROW_RATE = 0.01f;
    protected static float NEW_FOOD_ENERGY = 10;

    public static void main(String[] args) {
        //pass world attributes to the relavant palces
        updateAttributes();

        // Use INITIAL_ANIMAL_DENSITY when creating the world
        World world = WorldCreator.createWorld(WORLD_WIDTH, WORLD_HEIGHT, MAX_LAYERS, INITIAL_ANIMAL_DENSITY);
        GUI gui = GUI.getInstanceOrChangeWorld(world);
        gui.run();
    }

    public static void updateAttributes() {
        AnimalAttributes.setWorldAttributes(ANIMAL_STAT_TOTAL, ANIMAL_ATTACK_COST, MUTATION_RATE, MUTATION_FACTOR, NODE_INSERT_OR_DELETE_RATE, MAX_LAYERS);
        Nothing.setWorldAttributes(FOOD_GROW_RATE, NEW_FOOD_ENERGY);
    }
}

