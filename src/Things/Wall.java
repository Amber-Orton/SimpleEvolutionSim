package Things;
import java.awt.Color;

import Run.World;


public class Wall extends Thing {

    public Wall(World world, Position pos) {
        super(Color.DARK_GRAY, world, pos);
    }

    /**
     * Should only ever be used for the DEFAULTWALL instance in world.
     */
    public Wall() {
        color = Color.DARK_GRAY;
        world = null;
        pos = new Position(-1, -1);
    }

    @Override
    public String toString() {
        return super.toString() + ":Wall";
    }

    @Override
    protected int getasInt() {
        return 0;
    }
}
