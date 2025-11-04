package Run;
import Things.AnimalAttributes;
import Things.Nothing;

public class Main {


    /*
     * world attributes
     * defaults have been provided
     */
    private static final int ANIMAL_STAT_TOTAL = 100;
    private static final int ANIMAL_ATTACK_COST = 5;
    private static final float MUTATION_RATE = 0.1f;
    private static final float MUTATION_FACTOR = 0.1f;
    private static final float NODE_INSERT_OR_DELETE_RATE = 0.01f;
    private static final int WORLD_WIDTH = 10;
    private static final int WORLD_HEIGHT = 10;
    private static final float FOOD_GROW_RATE = 0.1f;
    private static final float NEW_FOOD_ENERGY = 10;

    
    
    
    public static void main(String[] args) {
        //pass world attributes to the relavant palces
        AnimalAttributes.setWorldAttributes(ANIMAL_STAT_TOTAL, ANIMAL_ATTACK_COST, MUTATION_RATE, MUTATION_FACTOR, NODE_INSERT_OR_DELETE_RATE);
        Nothing.setWorldAttributes(FOOD_GROW_RATE, NEW_FOOD_ENERGY);





        World world = new World(WORLD_WIDTH, WORLD_HEIGHT);
        GUI gui = GUI.getInstanceOrChangeWorld(world);
        gui.run();
    }
}

