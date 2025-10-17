import java.awt.Color;

public abstract class Edible extends Thing{
    protected float energy;

    public Edible(Color color,World world, Position pos) {
        super(color, world, pos);
    }

    public float getEnergy() {
        return energy;
    }

}
