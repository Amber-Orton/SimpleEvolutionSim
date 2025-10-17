import java.awt.Color;

public class Thing implements Runnable {

    protected Position pos;
    protected Thread thread;
    protected World world;


    protected Color color;

    protected Thing() {
    }
    
    public Thing(Color color,World world, Position pos) {
        this.color = color;
        this.world = world;
        setPos(pos);
    }
    
    @Override
    public void run() {
        // Default behavior: do nothing
        thread = null;
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
}
