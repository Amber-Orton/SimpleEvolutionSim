package Run.Paint;

import Run.World.World;
import Things.Helpers.Position;

public abstract class PaintOption {
    protected World world;

    public PaintOption(World world) {
        this.world = world;
    }
    public abstract void paint(Position pos);
}
