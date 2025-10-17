import java.awt.Color;
import java.util.Set;

public class World {
    private int width;
    private int height;
    private Set<Thing> things;
    Thing[][] grid;
    private final Wall DEFAULTWALL = new Wall();



    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Thing[height][width];
        things = new java.util.HashSet<>();
        populateWorld();
    }

    /**
     * Populate the world with Things.
     */
    private void populateWorld() {
        // currently just for testing purposes
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Position position = new Position(row, col);
                //if (Math.random() < 0.2) { // 20% chance to place a Wall on each cell
                //    putThingAt(position, new Wall(this, position));
                //}else if (Math.random() < 0.1) { // 10% chance to place an Animal on each cell
                //    putThingAt(position, new Animal(this, position , new AnimalAttributes(20, 20, 5), null));
                //} else {
                    putThingAt(position, Nothing.getInstance()); // Empty cell
                //}
            }
        }
        putThingAt(new Position(1, 1), new Animal(this, new Position(1,1) , new AnimalAttributes(33, 33, 34, 303), null));
        putThingAt(new Position(2, 1), new Food(this, new Position(2, 1), 2));
    }

    /**
     * Advance the world by one tick, running each Thing in its own thread.
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
        System.out.println("ticked!");
    }

    
    /** 
     * Set a Thing at a specific location in the grid.
     * Useful for testing and initializing.
     * @param pos The position to place the Thing
     * @param thing The Thing to place at the specified location
     */
    public void putThingAt(Position pos, Thing thing) {
        if (getThingAt(pos) != null && !posIsNothingClass(pos)) {//cannot place one Thing on another unless placing on Nothing
            //if (!(thing.getClass() == Egg.class && (getThingAt(pos).getClass() == Animal.class || getThingAt(pos).getClass() == Egg.class))){//can place Egg on Animal or Egg
                throw new IllegalArgumentException("Cant place a Thing at an occupied cell " + pos + ". Current thing: " + getThingAt(pos) + ", New thing: " + thing);
            //}
        }

        addThing(pos, thing);

        if (thing.getClass() != Egg.class || posIsNothingClass(pos)){//dont attempt to put Egg in grid if something else is aready there
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
        if (!thingIsNothingInstance(thing)){// Don't attempt to change coordinates of the singleton instance of Nothing
            thing.setPos(pos);
            thing.setWorld(this);
        }
        if (!thingIsNothingClass(thing)) { // Don't add an instance of Nothing to things
            things.add(thing);
        }
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
        replaceThing(thing, Nothing.getInstance());
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
    public boolean thingIsNothingClass(Thing thing){
        return thing.getClass() == Nothing.class;
    }

    /**
     * returns weather thing is the singlton instace of Nothing
     * @param thing thing to check
     * @return true if thing is singlton instace of Nothing false otherwise
     */
    public boolean thingIsNothingInstance(Thing thing){
        return thing == Nothing.getInstance();
    }

    /**
     * returns weather there is any instace of Nothing at the position
     * @param pos position to check
     * @return true if any instace of Nothing is at pos in grid false otherwise
     */
    public boolean posIsNothingClass(Position pos){
        return thingIsNothingClass(getThingAt(pos));
    }

    /**
     * returns weather there is the singlton instace of Nothing at the position
     * @param pos position to check
     * @return true if singlton instace of Nothing is at pos in grid false otherwise
     */
    public boolean posIsNothingInstance(Position pos){
        return thingIsNothingInstance(getThingAt(pos));
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
