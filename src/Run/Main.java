package Run;
import Things.AnimalAttributes;
import Things.Nothing;

public class Main {


    /*
     * world attributes
     * defaults have been provided
     */
    private static int ANIMAL_STAT_TOTAL = 100;
    private static int ANIMAL_ATTACK_COST = 5;
    private static float MUTATION_RATE = 0.1f;
    private static float MUTATION_FACTOR = 0.1f;
    private static float NODE_INSERT_OR_DELETE_RATE = 0.01f;
    private static int MAX_LAYERS = 5;
    private static int WORLD_WIDTH = 10;
    private static int WORLD_HEIGHT = 10;
    private static float FOOD_GROW_RATE = 0.1f;
    private static float NEW_FOOD_ENERGY = 10;

    public static void main(String[] args) {
        //pass world attributes to the relavant palces
        AnimalAttributes.setWorldAttributes(ANIMAL_STAT_TOTAL, ANIMAL_ATTACK_COST, MUTATION_RATE, MUTATION_FACTOR, NODE_INSERT_OR_DELETE_RATE, MAX_LAYERS);
        Nothing.setWorldAttributes(FOOD_GROW_RATE, NEW_FOOD_ENERGY);

        World world = WorldCreator.createWorld(WORLD_WIDTH, WORLD_HEIGHT, MAX_LAYERS);
        GUI gui = GUI.getInstanceOrChangeWorld(world);
        gui.run();
    }
}

