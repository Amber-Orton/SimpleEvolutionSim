package Run;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Logger.Logger;
import Run.GUI.GUI;
import Run.World.World;
import Run.World.WorldCreator;
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







    public static int getANIMAL_REST_COST() {
        return ANIMAL_REST_COST;
    }

    public static void setANIMAL_REST_COST(int ANIMAL_REST_COST) {
        Main.ANIMAL_REST_COST = ANIMAL_REST_COST;
        updateAttributes();
    }

    public static int getANIMAL_STAT_TOTAL() {
        return ANIMAL_STAT_TOTAL;
    }

    public static void setANIMAL_STAT_TOTAL(int ANIMAL_STAT_TOTAL) {
        Main.ANIMAL_STAT_TOTAL = ANIMAL_STAT_TOTAL;
        updateAttributes();
    }

    public static int getANIMAL_EAT_COST() {
        return ANIMAL_EAT_COST;
    }

    public static void setANIMAL_EAT_COST(int ANIMAL_EAT_COST) {
        Main.ANIMAL_EAT_COST = ANIMAL_EAT_COST;
        updateAttributes();
    }

    public static int getANIMAL_TURN_COST() {
        return ANIMAL_TURN_COST;
    }

    public static void setANIMAL_TURN_COST(int ANIMAL_TURN_COST) {
        Main.ANIMAL_TURN_COST = ANIMAL_TURN_COST;
        updateAttributes();
    }

    public static int getANIMAL_REST_HEAL_AMOUNT() {
        return ANIMAL_REST_HEALTH;
    }

    public static void setANIMAL_REST_HEAL_AMOUNT(int ANIMAL_REST_HEALTH) {
        Main.ANIMAL_REST_HEALTH = ANIMAL_REST_HEALTH;
        updateAttributes();
    }

    public static int getEGG_HATCH_CYCLES() {
        return EGG_HATCH_CYCLES;
    }

    public static void setEGG_HATCH_CYCLES(int EGG_HATCH_CYCLES) {
        Main.EGG_HATCH_CYCLES = EGG_HATCH_CYCLES;
        updateAttributes();
    }

    public static float getMUTATION_RATE() {
        return MUTATION_RATE;
    }

    public static void setMUTATION_RATE(float MUTATION_RATE) {
        Main.MUTATION_RATE = MUTATION_RATE;
        updateAttributes();
    }

    public static float getMUTATION_FACTOR() {
        return MUTATION_FACTOR;
    }

    public static void setMUTATION_FACTOR(float MUTATION_FACTOR) {
        Main.MUTATION_FACTOR = MUTATION_FACTOR;
        updateAttributes();
    }

    public static float getNODE_INSERT_OR_DELETE_RATE() {
        return NODE_INSERT_OR_DELETE_RATE;
    }

    public static void setNODE_INSERT_OR_DELETE_RATE(float NODE_INSERT_OR_DELETE_RATE) {
        Main.NODE_INSERT_OR_DELETE_RATE = NODE_INSERT_OR_DELETE_RATE;
        updateAttributes();
    }

    public static float getFOOD_GROW_RATE() {
        return FOOD_GROW_RATE;
    }

    public static void setFOOD_GROW_RATE(float FOOD_GROW_RATE) {
        Main.FOOD_GROW_RATE = FOOD_GROW_RATE;
        updateAttributes();
    }

    public static float getNEW_FOOD_ENERGY() {
        return NEW_FOOD_ENERGY;
    }

    public static void setNEW_FOOD_ENERGY(float NEW_FOOD_ENERGY) {
        Main.NEW_FOOD_ENERGY = NEW_FOOD_ENERGY;
        updateAttributes();
    }

    public static int getANIMAL_EXISTENCE_COST() {
        return ANIMAL_EXISTANCE_COST;
    }

    public static void setANIMAL_EXISTENCE_COST(int ANIMAL_EXISTANCE_COST) {
        Main.ANIMAL_EXISTANCE_COST = ANIMAL_EXISTANCE_COST;
        updateAttributes();
    }

    public static int getANIMAL_ATTACK_COST() {
        return ANIMAL_ATTACK_COST;
    }

    public static void setANIMAL_ATTACK_COST(int ANIMAL_ATTACK_COST) {
        Main.ANIMAL_ATTACK_COST = ANIMAL_ATTACK_COST;
        updateAttributes();
    }

    public static int getANIMAL_MOVE_COST() {
        return ANIMAL_MOVE_COST;
    }

    public static void setANIMAL_MOVE_COST(int ANIMAL_MOVE_COST) {
        Main.ANIMAL_MOVE_COST = ANIMAL_MOVE_COST;
        updateAttributes();
    }
}

