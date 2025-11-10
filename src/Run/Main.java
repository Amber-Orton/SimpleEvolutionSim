package Run;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Things.Nothing;
import Things.Helpers.AnimalAttributes;
import Things.Helpers.NameCreator;

public class Main {

    /*
     * world attributes
     * defaults have been provided
     */
    protected static int ANIMAL_STAT_TOTAL = 100;
    protected static int ANIMAL_ATTACK_COST = 5;
    protected static int ANIMAL_MOVE_COST = 1;
    protected static int ANIMAL_REST_ENERGY = 3;
    protected static int ANIMAL_REST_HEALTH = 2;
    protected static int EGG_HATCH_CYCLES = 3;
    protected static float MUTATION_RATE = 0.1f;
    protected static float MUTATION_FACTOR = 0.1f;
    protected static float NODE_INSERT_OR_DELETE_RATE = 0.01f;
    protected static int MAX_LAYERS = 5;
    protected static float FOOD_GROW_RATE = 0.05f;
    protected static float NEW_FOOD_ENERGY = 10;
    protected static int WORLD_WIDTH = 10;
    protected static int WORLD_HEIGHT = 10;
    protected static float INITIAL_ANIMAL_DENSITY = 0.8f;
    protected static float INITIAL_NEURAL_NET_RANDOMNESS = 0.1f;


    /**
     * other options
     */
    protected static final boolean SHOW_OPTIONS_ON_FIRST_OPEN = true;
    public static boolean IN_DEPTH_DEBUG_MODE = false;
    public static boolean autoDebug = false;


    /**
     * Critical information for running the simulation
     */
    protected static long lastTickTime;
    protected static volatile boolean play = false;
    protected static volatile int tickMillis = 0;
    protected static World world;
    protected static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    //do not change unless you know what youre doing
    public static final int NEURAL_NET_INPUT_SIZE = 18;
    public static final int NEURAL_NET_OUTPUT_SIZE = 7;

    public static void main(String[] args) {
        try {
            NameCreator.open();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        updateAttributes();
        world = createWorld();
        GUI gui = GUI.getInstanceOrChangeWorld(world);
        gui.run();
    }

    public static void updateAttributes() {
        AnimalAttributes.setWorldAttributes(ANIMAL_STAT_TOTAL, ANIMAL_ATTACK_COST, MUTATION_RATE, MUTATION_FACTOR, NODE_INSERT_OR_DELETE_RATE, MAX_LAYERS, ANIMAL_MOVE_COST, ANIMAL_REST_ENERGY, ANIMAL_REST_HEALTH, EGG_HATCH_CYCLES);
        Nothing.setWorldAttributes(FOOD_GROW_RATE, NEW_FOOD_ENERGY);
    }

    public static World createWorld() {
        return WorldCreator.createWorld(WORLD_WIDTH, WORLD_HEIGHT, MAX_LAYERS, ANIMAL_STAT_TOTAL, INITIAL_ANIMAL_DENSITY, INITIAL_NEURAL_NET_RANDOMNESS);
    }
}

