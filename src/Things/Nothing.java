package Things;
import java.awt.Color;
import java.util.Random;

import Run.World;

/**
 * Represents an empty space in the world.
 * is a singleton and can have instaces as the same time.
 */
public class Nothing extends Thing {
        
    private static final Random random = new Random();
    private static float foodGrowRate;
    private static float newFoodEnergy;

    public Nothing(World world, Position pos) {
        super(Color.WHITE, world, pos);
    }

    public static void setWorldAttributes(float foodGrowRate, float newFoodEnergy) {
        Nothing.foodGrowRate = foodGrowRate;
        Nothing.newFoodEnergy = newFoodEnergy;
    }

    @Override
    public boolean needsToTick() {
        return false;
    }

    @Override
    public boolean needsToDoAction() {
        return true;
    }

    @Override
    public String getName() {
        return "Nothing";
    }

    
    @Override
    public void doAction() {
        if (random.nextFloat() <= foodGrowRate) {
            world.replaceThing(this, new Food(world, pos, newFoodEnergy));
        }
    }

    @Override
    public String toString() {
        return super.toString() + ":Nothing";
    }

    @Override
    protected int getasInt() {
        return 1;
    }
}
