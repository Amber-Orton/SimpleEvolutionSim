package Run.World;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import Logger.Logger;
import Run.Main;
import Run.OldGUI;
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
    private Thing[][] grid;
    private volatile boolean[][] changedGrid;
    private volatile Snapshot latestSnapshot;
    protected Nothing[][] nothingGrid;
    
    private final Wall DEFAULTWALL = new Wall();
    private int tickCount = 0;
    
    

    
    


    protected World(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Thing[height][width];
        this.nothingGrid = new Nothing[height][width];
        changedGrid = new boolean[height][width];
        for (boolean[] row : changedGrid) {
            Arrays.fill(row, true);//initialise to true since the world has changed from completly empty to populated on boot
        }
    }
    

    /**
     * Used to tick automatically on time
     * The caller must first check and set readyToTick to false before calling
     */
    public synchronized void run() {
        
        tick();
        
    }
    
    /**
     * Advance the world by one tick, running each Thing in its own thread to think 
     * then one thread for updating the world with the actions of the Things.
     */
    private synchronized void tick() {
        Logger.logEvent("Tick: " + tickCount, "Tick started");
        tickCount++;

        // Remove dead things before ticking
        for (Thing thing : new HashSet<>(things)) {
            if (!thing.isAlive()) {
                removeThing(thing);
            }
        }

        Logger.logEvent("Tick: " + tickCount, "Removed dead things");
        
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
                Main.play = false;
                OldGUI.getInstance().playPauseButton.setText(Main.play ? "Pause" : "Play");
                System.err.println("Paused!: Error occurred while updating world: " + e.getMessage());
                Logger.logError("World", "Error occurred while updating world: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        Logger.logEvent("Tick: " + tickCount, "Threads run");
        
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
        
        Logger.logEvent("Tick: " + tickCount, "Created update sets");

        thingsDoAction(thingsToUpdate);
        Logger.logEvent("Tick: " + tickCount, "Things did action");
        thingsDoAction(eggsToUpdate);
        Logger.logEvent("Tick: " + tickCount, "Eggs did action");
        thingsDoAction(nothingToUpdate);
        Logger.logEvent("Tick: " + tickCount, "Nothing did action");

        updateSnapshot();
        Logger.logEvent("Tick: " + tickCount, "Updated cached grids");
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
     * caller responsible for all other actions only checks if the position is valid.
     * careful when calling can end up with duplicate entries in grid[][]
     *      does not update thing to reflect this change
     * @param pos the position to update
     * @param thing the thing to place at the position
     */
    private void changeGridAt(Position pos, Thing thing) {
        if (posIsInBounds(pos)) {
            grid[pos.getRow()][pos.getCol()] = thing;
            changedGrid[pos.getRow()][pos.getCol()] = true;
        }
    }

    /**
     * Creates a snapshot of the current world state for GUI rendering.
     * This must be called when it is known the state of the world is stable for the whole execution time.
     */
    public void updateSnapshot() {
        Thing[][] gridCopy = new Thing[height][width];
        boolean[][] changedCopy = latestSnapshot == null ? new boolean[height][width] : latestSnapshot.changedGrid;

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                gridCopy[r][c] = grid[r][c];
                if (!changedCopy[r][c] && changedGrid[r][c]) {
                    changedCopy[r][c] = true;
                    changedGrid[r][c] = false;
                }
            }
        }

        latestSnapshot = new Snapshot(gridCopy, changedCopy);
    }

    /**
     * Snapshot container returned by {@link #getLatestSnapshot()}.
     * Arrays returned here are deep copies representing a single point in time.
     */
    public static class Snapshot {
        public final Thing[][] grid;
        public final boolean[][] changedGrid;

        public Snapshot(Thing[][] grid, boolean[][] changedGrid) {
            this.grid = grid;
            this.changedGrid = changedGrid;
        }

        public void resetChangedGrid(boolean value) {
            for (boolean[] row : changedGrid) {
                Arrays.fill(row, value);
            }
        }

        public void setChangedAt(int row, int col, boolean changed) {
            changedGrid[row][col] = changed;
        }
        
        public void setChangedAt(Position pos, boolean changed) {
            setChangedAt(pos.getRow(), pos.getCol(), changed);
        }
    }


    /**
     * Returns the most recently published snapshot, a representation of the world the last time it was known stable.
     * used to get a recent state of the world when all grids were garanteed to be at the same time. 
     * @return the latest stable snapshot; may return null until the world has produced its first snapshot.
     */
    public Snapshot getLatestSnapshot() {
        return latestSnapshot;
    }

    public void posHasChanged(Position pos) {
        if (posIsInBounds(pos)) {
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

    public Thing[][] getThingGrid() {
        return grid;
    }

    public boolean[][] getChangedGrid() {
        return changedGrid;
    }
}
