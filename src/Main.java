public class Main {


    /*
     * world attributes
     * defaults have been provided
     */
    private static final int ANIMAL_STAT_TOTAL = 100;
    private static final int ANIMAL_ATTACK_COST = 5;
    private static final int WORLD_WIDTH = 10;
    private static final int WORLD_HEIGHT = 10;
    private static final float MUTATION_RATE = 0.1f;

    
    
    
    public static void main(String[] args) {
        //pass world attributes to the relavant palces
        AnimalAttributes.getWorldAttributes(ANIMAL_STAT_TOTAL, ANIMAL_ATTACK_COST, MUTATION_RATE);




        World world = new World(WORLD_WIDTH, WORLD_HEIGHT);
        GUI gui = GUI.getInstanceOrChangeWorld(world);
        gui.run();
    }
}

