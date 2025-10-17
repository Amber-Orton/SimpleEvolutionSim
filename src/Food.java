import java.awt.Color;

public class Food extends Edible{

    public Food(World world, Position pos, float energy) {
        super(Color.RED, world, pos);
        if (energy <= 0){
            throw new IllegalArgumentException("cannot make food with no energy" + energy);
        }
        this.energy = energy;
    }

}
