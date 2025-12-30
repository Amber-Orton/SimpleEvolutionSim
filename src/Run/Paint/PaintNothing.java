package Run.Paint;

import Run.World.World;
import Things.Helpers.Position;

public class PaintNothing extends PaintOption {
    public PaintNothing(World world) {
        super(world);
    }

    @Override
    public void paint(Position pos) {
        Paint.KillThingAt(pos, world);
    }

}
