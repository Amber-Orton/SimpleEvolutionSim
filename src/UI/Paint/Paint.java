package UI.Paint;

import Main.Main;
import Main.World.World;
import Things.Thing;
import Things.Helpers.Livable;
import Things.Helpers.Position;

public class Paint {

    protected static void paintThingAt(Position pos, Thing thing, World world) {
        KillThingAt(pos, world);
        world.replaceThingAt(pos, thing);
        world.updateSnapshot();
        boolean wasDoUpdateWorldView = Main.isDoUpdateWorldView();
        Main.setDoUpdateWorldView(true);
        Main.getGui().update(System.currentTimeMillis());
        Main.setDoUpdateWorldView(wasDoUpdateWorldView);
    }

    protected static void KillThingAt(Position pos, World world) {
        Thing origionalThing = world.getThingAt(pos);
        if (origionalThing instanceof Livable) {
            world.killThing((Livable)origionalThing);
        }
    }
}