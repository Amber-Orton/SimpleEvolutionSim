package Things;

import Run.World.World;
import Things.Helpers.Position;

public abstract class Edible extends Thing{
    protected float energy;

    public Edible(World world, Position pos) {
        super(world, pos);
    }

    public float getEnergy() {
        return energy;
    }

}
