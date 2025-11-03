package Things;
import java.awt.Color;

import Run.World;

public abstract class Thing implements Runnable {

    protected Position pos;
    public Thread thread;
    protected World world;
    protected boolean isAlive;


    protected Color color;

    protected Thing() {
    }
    
    public Thing(Color color,World world, Position pos) {
        this.color = color;
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
        thread = null;
    }

    public void doAction() {
        return;//defult to do nothing
    }
    
    @Override
    public String toString() {
        return pos.toString() + " is: Thing";
    }

    public Color getColor() {
        return color;
    }

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

    public void interrupt() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    public void removeHealth (float amount){
        // default do nothing
    }

    
    public void setThread(Thread thread) {
        this.thread = thread;
    }
    
    public Thread getThread() {
        return thread;
    }


    //for seeing input to neural net
    protected abstract int getasInt();

    public void die(){
        isAlive= false;
    }
}
