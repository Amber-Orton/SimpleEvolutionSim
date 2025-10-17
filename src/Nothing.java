import java.awt.Color;

/**
 * Represents an empty space in the world.
 * is a singleton and can have instaces as the same time.
 */
public class Nothing extends Thing {

    // Singleton instance
    private static Nothing instance = new Nothing();

    /**
     * Only ever used for the singleton instance.
     */
    private Nothing() {
        color = Color.WHITE;
        world = null;
        pos = new Position(-1, -1);
    }
        

    // Private constructor to prevent direct instantiation
    private Nothing(World world, Position pos) {
        super(Color.WHITE, world, pos);
    }

    /**
     * Returns the single instance of Nothing.
     * use witout parameters to get the singleton instance that has no world and invalid cordinates.
     * @param world The world instance (can be ignored if not needed for Nothing)
     * @param row The row index (can be ignored if not needed for Nothing)
     * @param col The column index (can be ignored if not needed for Nothing)
     * @return new nothing instance
     */
    public static Nothing CreateNewInstance(World world, Position pos) {
        return new Nothing(world, pos);
    }

    /**
     * Get the singleton instance of Nothing.
     * has no world and invalid cordinates.
     * if you need a Nothing with a world and valid cordinates use CreateNewInstance(World, Position) instead.
     * @return The singleton instance of Nothing
     */
    public static Nothing getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return super.toString() + ":Nothing";
    }
}
