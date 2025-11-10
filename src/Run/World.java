package Run;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.SwingUtilities;

import Things.Egg;
import Things.Nothing;
import Things.Thing;
import Things.Wall;
import Things.Helpers.Position;

public class World implements Runnable {
    private int width;
    private int height;
    private Set<Thing> things = new HashSet<>();
    private Set<Thing> thingsToUpdate = new HashSet<>();
    private Set<Thing> eggsToUpdate = new HashSet<>();
    private Set<Thing> nothingToUpdate = new HashSet<>();
    protected Thing[][] grid;
    protected Color[][] colorGrid;
    protected boolean[][] changedGrid;
    protected Nothing[][] nothingGrid;
    private final Wall DEFAULTWALL = new Wall();
    private int tickCount = 0;
    protected boolean readyToTick = true;


    protected ArrayList<Long> tickDebugTimes = new ArrayList<>();




    protected World(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Thing[height][width];
        this.nothingGrid = new Nothing[height][width];
        colorGrid = new Color[height][width];
        changedGrid = new boolean[height][width];
        for (boolean[] row : changedGrid) {
            Arrays.fill(row, true);//initialise to true since the world has changed from completly empty to populated on boot
        }
    }


    /**
     * Used to tick automatically on time
     * The caller must first check and set readyToTick to false before calling
     */
    public void run() {
        long startTime = System.nanoTime();
        doTickAndUpdateGUI(startTime);  // Do the tick

        synchronized (this) {
            readyToTick = true;
            notifyAll();
        }
        Main.lastTickTime = System.nanoTime() - startTime;
        System.out.println("Ticked! in: " + (Main.lastTickTime / 1_000_000_000.0) + " seconds");
    }

    protected void doTickAndUpdateGUI(long startTime) {
        tick();
        Main.lastTickTime = System.nanoTime() - startTime;
        SwingUtilities.invokeLater(() -> GUI.getInstance().updateAfterTick());
    }

    /**
     * Advance the world by one tick, running each Thing in its own thread to think 
     * then one thread for updating the world with the actions of the Things.
     */
    public synchronized void tick() {
        if (Main.IN_DEPTH_DEBUG_MODE) {
            tickDebugTimes.clear();
            tickDebugTimes.add(System.nanoTime());
        }
        tickCount++;

        // Remove dead things before ticking
        //things.removeIf(thing -> !thing.isAlive());
        for (Thing thing : new HashSet<>(things)) {
            if (!thing.isAlive()) {
                removeThing(thing);
            }
        }

        if (Main.IN_DEPTH_DEBUG_MODE) {tickDebugTimes.add(System.nanoTime());}

        List<Future<?>> futures = new ArrayList<>(things.size());

        //dispatch the Things
        for (Thing thing : things) {
            if (thing.needsToTick()) {
                futures.add(Main.executorService.submit(thing));
            }
        }


        // Wait for all threads to finish
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        if (Main.IN_DEPTH_DEBUG_MODE) {tickDebugTimes.add(System.nanoTime());}

        thingsToUpdate = new HashSet<>();
        eggsToUpdate = new HashSet<>();
        for (Thing thing : things) {
            if (thing.needsToDoAction()) {
                if (thing instanceof Egg) {
                    eggsToUpdate.add(thing);
                } else if (thing instanceof Nothing) {
                    nothingToUpdate.add(thing);
                } else {
                    thingsToUpdate.add(thing);
                }
            }
        }

        if (Main.IN_DEPTH_DEBUG_MODE) {tickDebugTimes.add(System.nanoTime());}

        thingsDoAction(thingsToUpdate);
        if (Main.IN_DEPTH_DEBUG_MODE) {tickDebugTimes.add(System.nanoTime());}
        thingsDoAction(eggsToUpdate);
        if (Main.IN_DEPTH_DEBUG_MODE) {tickDebugTimes.add(System.nanoTime());}
        thingsDoAction(nothingToUpdate);
        if (Main.IN_DEPTH_DEBUG_MODE) {tickDebugTimes.add(System.nanoTime());}
    }

    private void thingsDoAction(Set<Thing> things) {
        while (things.iterator().hasNext()) {
            Thing thing = things.iterator().next();
            if (thing.isAlive()) {
                thing.doAction();
            }
            things.remove(thing);
        }
    }



    /** 
     * Set a Thing at a specific location in the grid.
     * Useful for testing and initializing.
     * @param pos The position to place the Thing
     * @param thing The Thing to place at the specified location
     */
    public void putThingAt(Position pos, Thing thing) {
        if (getThingAt(pos) != null && !posIsNothing(pos)) {//cannot place one Thing on another unless placing on Nothing
                throw new IllegalArgumentException("Cant place a Thing at an occupied cell " + pos + ". Current thing: " + getThingAt(pos) + ", New thing: " + thing);
        }

        addThing(pos, thing);

        if (thing.getClass() != Egg.class || posIsNothing(pos)){//dont attempt to put Egg in grid if something else is aready there
            changeGridAt(pos, thing);
        }
    }


    /**
     * adds a Thing without attempting to place it in the world
     * used to make sure that the Thing gets ticked even if it is 'below' something else
     * @param pos the position to set the things position to (does not place thing in grid)
     * @param thing the thing to be added
     */
    public void addThing(Position pos, Thing thing){
        if (!posIsInBounds(pos)) {
            throw new IndexOutOfBoundsException("Invalid grid coordinates when placing thing");
        }
        if (!thingIsNothing(thing)){// Don't attempt to change coordinates of Nothing
            thing.setPos(pos);
            thing.setWorld(this);
        }
        things.add(thing);
    }

    public void updateChangedGrid() {
        for (boolean[] row : changedGrid) {
            Arrays.fill(row, false);
        }
    }

    public Thing getThingAt(Position pos) {
        if (posIsInBounds(pos)) {
            return grid[pos.getRow()][pos.getCol()];
        } else {
            return DEFAULTWALL; // Out of bounds
        }
    }

    public Thing getThingAt(int row, int col) {
        return getThingAt(new Position(row, col));
    }

    /**
     * changes Thing at the location of origionalThing to newThing as long as origionalThing is in grid at origionalThing.pos
     * fails and returns false if origionalThing is not where it is supposed to be
     * @param origionalThing Thing to replace
     * @param newThing Thing to replace with
     * @return true if the Thing was replaced false otherwise
     */
    public boolean replaceThing(Thing origionalThing, Thing newThing){
        things.remove(origionalThing);
        if (getThingAt(origionalThing.getPos()) == origionalThing){
            changeGridAt(origionalThing.getPos(), null);
            putThingAt(origionalThing.getPos(), newThing);
            return true;
        } else {
            return false;
        }
    }


    /**
     * removes all references to thing in this instance of world.
     * does not edit thing
     * @param thing the thing to remove
     */
    public void removeThing(Thing thing){
        replaceThing(thing, nothingGrid[thing.getPos().getRow()][thing.getPos().getCol()]);
    }

    /**
     * removes all references to thing in this instance of world using removeThing(Thing)
     * interrupts things thread to kill it
     * @param thing the thing to kill
     */
    public void killThing(Thing thing) {
        removeThing(thing);
        thing.die();
    }

    /**
     * Changes the Thing at the specified position in the grid.
     * updates the colour grid accordingly
     * caller responsible for all other actions only checks if the position is valid.
     * careful when calling can end up with duplicate entries in grid[][]
     *      does not update thing to reflect this change
     * @param pos the position to update
     * @param thing the thing to place at the position
     */
    private void changeGridAt(Position pos, Thing thing) {
        if (posIsInBounds(pos)) {
            grid[pos.getRow()][pos.getCol()] = thing;
            if (thing != null) {
                colorGrid[pos.getRow()][pos.getCol()] = thing.getColor();
            }
            changedGrid[pos.getRow()][pos.getCol()] = true;
        }
    }

    public boolean posIsInBounds(Position pos) {
        return pos.getRow() >= 0 && pos.getRow() < height && pos.getCol() >= 0 && pos.getCol() < width;
    }

    /**
     * returns weather thing is any instace of Nothing
     * @param thing thing to check
     * @return true if thing is any instace of Nothing false otherwise
     */
    public boolean thingIsNothing(Thing thing){
        return thing instanceof Nothing;
    }


    /**
     * returns weather there is any instace of Nothing at the position
     * @param pos position to check
     * @return true if any instace of Nothing is at pos in grid false otherwise
     */
    public boolean posIsNothing(Position pos){
        return thingIsNothing(getThingAt(pos));
    }


    public Set<Thing> getThings() {
        return things;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }

    public int getTickCount() {
        return tickCount;
    }
}
