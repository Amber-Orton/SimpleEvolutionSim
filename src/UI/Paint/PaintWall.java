package UI.Paint;

import Main.World.World;
import Things.Wall;
import Things.Helpers.Position;

public class PaintWall extends PaintOption {
    public PaintWall(World world) {
        super(world);
    }

    @Override
    public void paint(Position pos) {
        Paint.paintThingAt(pos, new Wall(world, pos), world);
    }

}
