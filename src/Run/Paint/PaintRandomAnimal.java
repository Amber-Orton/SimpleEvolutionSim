package Run.Paint;

import Run.World.World;
import Run.World.WorldCreator;
import Things.Helpers.Position;

public class PaintRandomAnimal extends PaintOption {
    public PaintRandomAnimal(World world) {
        super(world);
    }

    @Override
    public void paint(Position pos) {
        Paint.paintThingAt(pos, WorldCreator.createRandomAnimal(world, pos), world);
    }

}
