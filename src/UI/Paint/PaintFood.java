package UI.Paint;

import Main.Main;
import Main.World.World;
import Things.Food;
import Things.Helpers.Position;

public class PaintFood extends PaintOption {
    public PaintFood(World world) {
        super(world);
    }

    @Override
    public void paint(Position pos) {
        Paint.paintThingAt(pos, new Food(world, pos, Main.getNEW_FOOD_ENERGY()), world);
    }

}
