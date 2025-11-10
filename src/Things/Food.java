package Things;

import java.awt.Color;

import Run.World;
import Things.Helpers.Position;

public class Food extends Edible{

    public Food(World world, Position pos, float energy) {
        super(Color.RED, world, pos);
        if (energy <= 0){
            throw new IllegalArgumentException("cannot make food with no energy" + energy);
        }
        this.energy = energy;
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
        return "Food";
    }

    @Override
    protected int getasInt() {
        return 2;
    }

}
