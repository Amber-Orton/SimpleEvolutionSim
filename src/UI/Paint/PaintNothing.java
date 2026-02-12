package UI.Paint;

import Main.World.World;
import Things.Nothing;
import Things.Helpers.Position;

public class PaintNothing extends PaintOption {
    public PaintNothing(World world) {
        super(world);
    }

    @Override
    public void paint(Position pos) {
        Paint.paintThingAt(pos, new Nothing(world, pos), world);
    }

}
