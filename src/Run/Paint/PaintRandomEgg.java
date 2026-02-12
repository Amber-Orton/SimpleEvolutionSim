package Run.Paint;

import Run.World.World;
import Run.World.WorldCreator;
import Things.Helpers.Position;

public class PaintRandomEgg extends PaintOption {
    public PaintRandomEgg(World world) {
        super(world);
    }

    @Override
    public void paint(Position pos) {
        Paint.paintThingAt(pos, WorldCreator.createRandomEgg(world, pos), world);
    }
}
