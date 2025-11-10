package Things;
import java.awt.Color;
import java.util.Random;

import Run.World;
import Things.Helpers.Position;

/**
 * Represents an empty space in the world.
 * is a singleton and can have instaces as the same time.
 */
public class Nothing extends Thing {
        
    private static final Random random = new Random();
    private static float foodGrowRate;
    private static float newFoodEnergy;
    private boolean willGrow;

    public Nothing(World world, Position pos) {
        super(Color.WHITE, world, pos);
    }

    public static void setWorldAttributes(float foodGrowRate, float newFoodEnergy) {
        Nothing.foodGrowRate = foodGrowRate;
        Nothing.newFoodEnergy = newFoodEnergy;
    }

    @Override
    public void run() {
        if (random.nextFloat() <= foodGrowRate) {
            willGrow = true;
        } else {
            willGrow = false;
        }
        super.run();
    }

    @Override
    public boolean needsToTick() {
        return true;
    }

    @Override
    public boolean needsToDoAction() {
        return willGrow;
    }

    @Override
    public String getName() {
        return "Nothing";
    }

    
    @Override
    /**
     * this is only called if the Nothing is set to grow.
     */
    public void doAction() {
        world.replaceThing(this, new Food(world, pos, newFoodEnergy));
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
