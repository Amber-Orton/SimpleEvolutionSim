package Run.Paint;

import Run.World.World;
import Things.Thing;
import Things.Helpers.Livable;
import Things.Helpers.Position;

public class Paint {

    protected static void paintThingAt(Position pos, Thing thing, World world) {
        KillThingAt(pos, world);
        world.addThing(pos, thing);
    }

    protected static void KillThingAt(Position pos, World world) {
        Thing origionalThing = world.getThingAt(pos);
        if (origionalThing instanceof Livable) {
            world.killThing((Livable)origionalThing);
        }
    }
}