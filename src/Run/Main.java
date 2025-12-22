package Run;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Logger.Logger;
import Run.GUI.GUI;
import Things.Nothing;
import Things.Helpers.AnimalAttributes;
import Things.Helpers.NameCreator;

public class Main {

    /*
     * world attributes
     * defaults have been provided
     */
    protected static int ANIMAL_STAT_TOTAL = 100;
    protected static int ANIMAL_EXISTANCE_COST = 0;
    protected static int ANIMAL_ATTACK_COST = 5;
    protected static int ANIMAL_MOVE_COST = 1;
    protected static int ANIMAL_EAT_COST = 0;
    protected static int ANIMAL_TURN_COST = 0;
    protected static int ANIMAL_REST_COST = 3;
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
    public static final int[] ANIMAL_COLOR_PALETTE = new int[]{
        0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00,
        0xFF00FF, 0x00FFFF, 0xFFFFFF, 0x000000,
        0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00,
        0xFF00FF, 0x00FFFF, 0xFFFFFF, 0x000000,
    };


    /**
     * other options
    */
   protected static final boolean SHOW_OPTIONS_ON_FIRST_OPEN = false;
   public static boolean IN_DEPTH_DEBUG_MODE = false;
   public static boolean autoDebug = false;
   public static boolean LOGGING_ENABLED = true;
   
   
   /**
    * Critical information for running the simulation and GUI
   */
    public static long lastTickTime;
    // protected static long lastUpdateWorldViewTotalTime;
    // protected static long lastUpdateWorldViewActualTime;
    // protected static long lastUpdateWorldViewStartTime;
    protected static boolean waitForLongUpdateAfterTick = true;
    protected static boolean doUpdateWorldView = true;
    public static volatile boolean play = false;
    public static volatile long targetMSPT = 0;
    public static volatile int ticksToRun = -1;
    protected static World world;
    protected static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    public static final Random random = new Random();
    protected static GUI gui;
    
    //do not change unless you know what youre doing
    public static final int NEURAL_NET_INPUT_SIZE = 18;
    public static final int NEURAL_NET_OUTPUT_SIZE = 7;
    
    public static void main(String[] args) {
        try {
            NameCreator.open();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logError("Main", "Failed to open NameCreator: " + e.getMessage());
            System.exit(1);
        }
        updateAttributes();
        world = createWorld();
        gui = new GUI(world);
        gui.run();
    }
    
    public static void updateAttributes() {
        AnimalAttributes.setWorldAttributes(ANIMAL_STAT_TOTAL, ANIMAL_ATTACK_COST, MUTATION_RATE, MUTATION_FACTOR, NODE_INSERT_OR_DELETE_RATE, MAX_LAYERS, ANIMAL_EXISTANCE_COST, ANIMAL_MOVE_COST, ANIMAL_EAT_COST, ANIMAL_TURN_COST, ANIMAL_REST_COST, ANIMAL_REST_HEALTH, EGG_HATCH_CYCLES);
        Nothing.setWorldAttributes(FOOD_GROW_RATE, NEW_FOOD_ENERGY);
    }
    
    public static World createWorld() {
        return WorldCreator.createWorld(WORLD_WIDTH, WORLD_HEIGHT, MAX_LAYERS, ANIMAL_STAT_TOTAL, INITIAL_ANIMAL_DENSITY, INITIAL_NEURAL_NET_RANDOMNESS);
    }

    public static void createAndRunNewWorld() {
        Main.world = createWorld();
        gui.setWorld(world);
    }
    
    public static void pause() {
        play = false;
        gui.updatePlayPauseButton();
    }
    
    public static void play() {
        play = true;
        gui.updatePlayPauseButton();
    }
    
    public static int getWORLD_WIDTH() {
        return WORLD_WIDTH;
    }
    
    public static void setWORLD_WIDTH(int wORLD_WIDTH) {
        WORLD_WIDTH = wORLD_WIDTH;
    }
    
    public static int getWORLD_HEIGHT() {
        return WORLD_HEIGHT;
    }
    
    public static void setWORLD_HEIGHT(int wORLD_HEIGHT) {
        WORLD_HEIGHT = wORLD_HEIGHT;
    }

    public static int getMAX_LAYERS() {
        return MAX_LAYERS;
    }

    public static void setMAX_LAYERS(int maxLayers) {
        MAX_LAYERS = maxLayers;
    }

    public static float getINITIAL_ANIMAL_DENSITY() {
        return INITIAL_ANIMAL_DENSITY;
    }

    public static void setINITIAL_ANIMAL_DENSITY(float d) {
        INITIAL_ANIMAL_DENSITY = d;
    }

    public static float getINITIAL_NEURAL_NET_RANDOMNESS() {
        return INITIAL_NEURAL_NET_RANDOMNESS;
    }

    public static void setINITIAL_NEURAL_NET_RANDOMNESS(float r) {
        INITIAL_NEURAL_NET_RANDOMNESS = r;
    }

}

