package Things;
import java.awt.Color;

import Run.World;
import Things.Helpers.Position;

public abstract class Edible extends Thing{
    protected float energy;

    public Edible(Color color,World world, Position pos) {
        super(color, world, pos);
    }

    public float getEnergy() {
        return energy;
    }

}
