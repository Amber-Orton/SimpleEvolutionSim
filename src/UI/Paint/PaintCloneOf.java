package UI.Paint;

import Main.World.World;
import Things.Thing;
import Things.Helpers.Position;

public class PaintCloneOf extends PaintOption {
    private Thing thingToClone;

    public PaintCloneOf(World world, Thing thingToClone) {
        super(world);
        this.thingToClone = thingToClone;
    }

    @Override
    public void paint(Position pos) {
        Paint.paintThingAt(pos, thingToClone.clone(), world);
    }

}
