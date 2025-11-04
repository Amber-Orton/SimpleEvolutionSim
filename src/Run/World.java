package Run;
import java.awt.Color;

import java.util.HashSet;
import java.util.Set;

import Things.Egg;
import Things.Nothing;
import Things.Position;
import Things.Thing;
import Things.Wall;

public class World {
    private int width;
    private int height;
    private Set<Thing> things = new HashSet<>();
    private Set<Thing> thingsToUpdate = new HashSet<>();
    private Set<Thing> eggsToUpdate = new HashSet<>();
    protected Thing[][] grid;
    protected Nothing[][] nothingGrid;
    private final Wall DEFAULTWALL = new Wall();




    protected World(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Thing[height][width];
        this.nothingGrid = new Nothing[height][width];
    }

    

    /**
     * Advance the world by one tick, running each Thing in its own thread to think 
     * then one thread for updating the world with the actions of the Things.
     */
    public void tick() {
        Thread[] threads = new Thread[things.size()];
        int i = 0;

        for (Thing thing : things) {
            Thread t = new Thread(thing);
            thing.setThread(t);
            threads[i++] = t;
        }
        
        // Start all threads
        for (Thread t : threads){
            t.start();
        }

        // Wait for all threads to finish
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        thingsToUpdate = new HashSet<>();
        eggsToUpdate = new HashSet<>();
        for (Thing thing : things) {
            if (thing instanceof Egg) {
                eggsToUpdate.add(thing);
            } else {
                thingsToUpdate.add(thing);
            }
        }

        while (thingsToUpdate.iterator().hasNext()) {
            Thing thing = thingsToUpdate.iterator().next();
            thing.doAction();
            thingsToUpdate.remove(thing);
        }

        //update eggs after
        while (eggsToUpdate.iterator().hasNext()) {
            Thing egg = eggsToUpdate.iterator().next();
            egg.doAction();
            eggsToUpdate.remove(egg);
        }

        System.out.println("ticked!");
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
            grid[pos.getPos()[0]][pos.getPos()[1]] = thing;
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

    /** 
     * Get a 2D array representing the colors of the grid.
     * Empty cells are represented by DEFAULTCOLOR.
     * @return A 2D array of Colors representing the grid
     */
    public Color[][] getGridOfColors() {
        Color[][] colorGrid = new Color[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                colorGrid[row][col] = grid[row][col].getColor();
            }
        }
        return colorGrid;
    }

    public Thing getThingAt(Position pos) {
        if (posIsInBounds(pos)) {
            return grid[pos.getRow()][pos.getCol()];
        } else {
            return DEFAULTWALL; // Out of bounds
        }
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
            grid[origionalThing.getPos().getRow()][origionalThing.getPos().getCol()] = null;
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
     * Edits Thing to set the world to null (so we thow an error if it attempts to change world)
     * interrupts things thread to kill it
     * @param thing the thing to kill
     */
    public void killThing(Thing thing) {
        removeThing(thing);
        thing.die();
        if (thing.thread != null){
            thing.thread.interrupt();
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
}
