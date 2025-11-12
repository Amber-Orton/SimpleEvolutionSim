package Things;

import java.awt.Color;

import Run.World;
import Things.Helpers.Position;

public abstract class Thing implements Runnable {

    protected Position pos;
    protected World world;
    protected boolean isAlive;


    protected Thing() {
    }
    
    public Thing(World world, Position pos) {
        this.world = world;
        setPos(pos);
        this.isAlive = true;
    }
    
    @Override
    public void run() {
        // Default behavior: do nothing
        if (!isAlive){
            world = null;
        }
    }

    public void doAction() {
        return;//defult to do nothing
    }
    
    @Override
    public String toString() {
        return "at: " + pos.toString() + " is: Thing";
    }

    public abstract Color getColor();

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        if (!world.posIsInBounds(pos)) {
            throw new IndexOutOfBoundsException("Position: " + pos + " out of bounds for: " + this);
        }
        this.pos = pos;
    }

    public World getWorld() {
        return world;
    }
    
    public void setWorld(World world) {
        this.world = world;
    }


    public void removeHealth (float amount){
        // default do nothing
    }
    

    public boolean isAlive() {
        return isAlive;
    }

    public abstract String getName();

    //for seeing input to neural net
    protected abstract int getasInt();

    public void die(){
        isAlive= false;
    }

    public abstract boolean needsToTick();

    public abstract boolean needsToDoAction();
}
