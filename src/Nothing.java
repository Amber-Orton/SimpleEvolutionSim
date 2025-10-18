import java.awt.Color;

/**
 * Represents an empty space in the world.
 * is a singleton and can have instaces as the same time.
 */
public class Nothing extends Thing {
        

    public Nothing(World world, Position pos) {
        super(Color.WHITE, world, pos);
    }

    @Override
    public void run() {
        super.run();
    }

    @Override
    public String toString() {
        return super.toString() + ":Nothing";
    }
}
