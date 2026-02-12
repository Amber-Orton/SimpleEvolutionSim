package Things;
import java.awt.Color;

import Main.World.World;
import Things.Helpers.Position;


public class Wall extends Thing {

    public Wall(World world, Position pos) {
        super(world, pos);
    }

    /**
     * Should only ever be used for the DEFAULTWALL instance in world.
     */
    public Wall() {
        world = null;
        pos = new Position(-1, -1);
    }

    @Override
    public Thing clone() {
        if (world == null) {
            return this; //default wall instance
        }
        return new Wall(world, pos);
    }

    @Override
    public boolean needsToTick() {
        return false;
    }

    @Override
    public boolean needsToDoAction() {
        return false;
    }

    @Override
    public String getName() {
        return "Wall";
    }

    @Override
    public Color getColor() {
        return Color.DARK_GRAY;
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
