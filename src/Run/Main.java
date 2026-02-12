package Run;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Logger.Logger;
import Run.GUI.GUI;
import Run.GUI.ControlPanel.ButtonPanel.DebugOptionsDialog;
import Run.World.World;
import Run.World.WorldCreator;
import Things.Nothing;
import Things.Thing;
import Things.Helpers.AnimalAttributes;
import Things.Helpers.NameCreator;
import Things.Helpers.Position;

public class Main {

    /*
     * world attributes
     * defaults have been provided
     * can be changed at runtime
     */
    private static int ANIMAL_STAT_TOTAL = 100;
    private static int ANIMAL_EXISTANCE_COST = 0;
    private static int ANIMAL_ATTACK_COST = 5;
    private static int ANIMAL_MOVE_COST = 1;
    private static int ANIMAL_EAT_COST = 0;
    private static int ANIMAL_TURN_COST = 0;
    private static int ANIMAL_REST_COST = 3;
    private static int ANIMAL_REST_HEALTH = 2;
    private static int EGG_HATCH_CYCLES = 3;
    private static float MUTATION_RATE = 0.1f;
    private static float MUTATION_FACTOR = 0.1f;
    private static float NODE_INSERT_OR_DELETE_RATE = 0.01f;
    private static int MAX_LAYERS = 5;
    private static float FOOD_GROW_RATE = 0.05f;
    private static float NEW_FOOD_ENERGY = 10;
    private static int WORLD_WIDTH = 10;
    private static int WORLD_HEIGHT = 10;
    
    private static float INITIAL_ANIMAL_DENSITY = 0.8f;
    private static float INITIAL_NEURAL_NET_RANDOMNESS = 0.1f;
    public static final int[] ANIMAL_COLOR_PALETTE = new int[]{
        0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00,
        0xFF00FF, 0x00FFFF, 0xFFFFFF, 0x000000,
        0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00,
        0xFF00FF, 0x00FFFF, 0xFFFFFF, 0x000000,
    };


    /**
     * other options
    */
   private static final boolean SHOW_OPTIONS_ON_FIRST_OPEN = false; // set to true to show world options dialog on first open, false to go straight to sim with default settings as defined above.
   private static boolean START_IN_INDEPTH_DEBUG_MODE = false;
   private static boolean START_IN_AUTO_DEBUG_MODE = false;
   private static boolean START_WITH_LOGGING_ENABLED = true;
   

   /**
    * Critical information for running the simulation and GUI
   */
    public static long lastTickTime;
    // protected static long lastUpdateWorldViewTotalTime;
    // protected static long lastUpdateWorldViewActualTime;
    // protected static long lastUpdateWorldViewStartTime;
    private static boolean waitForLongUpdateAfterTick = true;
    private static boolean doUpdateWorldView = true;
    
    private static volatile boolean play = false;
    private static volatile long targetMSPT = 0;
    private static volatile int ticksToRun = -1;
    protected static World world;
    protected static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final Random random = new Random();
    private static final GUI gui = new GUI();
    private static Position selectedPosition;
    private static Thing selectedThing;
    
    //do not change unless you know what youre doing
    public static final int NEURAL_NET_INPUT_SIZE = 18;
    public static final int NEURAL_NET_OUTPUT_SIZE = 7;
    
    public static void main(String[] args) {
        Logger.initialize();
        
        try {
            NameCreator.open();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logError("Main", "Failed to open NameCreator: " + e.getMessage());
            System.exit(1);
        }
        updateAttributes();
        if (SHOW_OPTIONS_ON_FIRST_OPEN) {
            WORLD_HEIGHT = 1;
            WORLD_WIDTH = 1;
        }
        world = createWorld();
        gui.setWorld(world);
        gui.run(SHOW_OPTIONS_ON_FIRST_OPEN);
    }
    
    public static void updateAttributes() {
        AnimalAttributes.setWorldAttributes(ANIMAL_STAT_TOTAL, ANIMAL_ATTACK_COST, MUTATION_RATE, MUTATION_FACTOR, NODE_INSERT_OR_DELETE_RATE, MAX_LAYERS, ANIMAL_EXISTANCE_COST, ANIMAL_MOVE_COST, ANIMAL_EAT_COST, ANIMAL_TURN_COST, ANIMAL_REST_COST, ANIMAL_REST_HEALTH, EGG_HATCH_CYCLES);
        Nothing.setWorldAttributes(FOOD_GROW_RATE, NEW_FOOD_ENERGY);
    }
    
    public static World createWorld() {
        return WorldCreator.createWorld(WORLD_WIDTH, WORLD_HEIGHT, MAX_LAYERS, ANIMAL_STAT_TOTAL, INITIAL_ANIMAL_DENSITY, INITIAL_NEURAL_NET_RANDOMNESS);
    }

    public static void createAndRunNewWorld() {
        world = createWorld();
        gui.setWorld(world);
    }

    public static void updateAfterTick(long startTime) {
        gui.update(startTime);
        if (Logger.isAutoDebug()) {
            System.out.println(Logger.getMostRecentDebugInfo(Logger.isInDepthDebugMode()? 100 : 10));
        }
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
    
    
    
    
    public static GUI getGui() {
        return gui;
    }
    
    public static ExecutorService getExecutorService() {
        return executorService;
    }
    
    public static boolean isPlay() {
        return play;
    }
    
    public static void setPlay(boolean playFlag) {
        Main.play = playFlag;
    }
    
    public static long getTargetMSPT() {
        return targetMSPT;
    }
    
    public static void setTargetMSPT(long mspt) {
        Main.targetMSPT = mspt;
    }
    
    public static int getTicksToRun() {
        return ticksToRun;
    }
    
    public static void setTicksToRun(int ticks) {
        Main.ticksToRun = ticks;
    }

    public static Random getRandom() {
        return random;
    }
    
    public static boolean isSTART_IN_INDEPTH_DEBUG_MODE() {
        return START_IN_INDEPTH_DEBUG_MODE;
    }
    
    public static void setSTART_IN_INDEPTH_DEBUG_MODE(boolean sTART_IN_INDEPTH_DEBUG_MODE) {
        START_IN_INDEPTH_DEBUG_MODE = sTART_IN_INDEPTH_DEBUG_MODE;
    }
    
    public static boolean isSTART_IN_AUTO_DEBUG_MODE() {
        return START_IN_AUTO_DEBUG_MODE;
    }
    
    public static void setSTART_IN_AUTO_DEBUG_MODE(boolean sTART_IN_AUTO_DEBUG_MODE) {
        START_IN_AUTO_DEBUG_MODE = sTART_IN_AUTO_DEBUG_MODE;
    }
    
    public static boolean isSTART_WITH_LOGGING_ENABLED() {
        return START_WITH_LOGGING_ENABLED;
    }
    
    public static void setSTART_WITH_LOGGING_ENABLED(boolean sTART_WITH_LOGGING_ENABLED) {
        START_WITH_LOGGING_ENABLED = sTART_WITH_LOGGING_ENABLED;
    }
    
    public static boolean isWaitForLongUpdateAfterTick() {
        return waitForLongUpdateAfterTick;
    }

    public static void setWaitForLongUpdateAfterTick(boolean waitForLongUpdateAfterTick) {
        Main.waitForLongUpdateAfterTick = waitForLongUpdateAfterTick;
    }

    public static boolean isDoUpdateWorldView() {
        return doUpdateWorldView;
    }

    public static void setDoUpdateWorldView(boolean doUpdateWorldView) {
        Main.doUpdateWorldView = doUpdateWorldView;
    }
    
    public static Position getSelectedPosition() {
        return selectedPosition;
    }

    public static void setSelectedPosition(Position selectedPosition) {
        Main.selectedPosition = selectedPosition;
        Main.selectedThing = selectedPosition != null ? Main.world.getThingAt(selectedPosition) : null;
    }

    public static Thing getSelectedThing() {
        return selectedThing;
    }
    
    public static void setSelectedThing(Thing selectedThing) {
        Main.selectedThing = selectedThing;
        Main.selectedPosition = selectedThing != null ? selectedThing.getPos() : null;
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

