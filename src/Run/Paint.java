package Run;

import Things.Food;
import Things.Thing;
import Things.Wall;
import Things.Helpers.Livable;
import Things.Helpers.Position;

public class Paint {
    private World world;


    public Paint(World world) {
        this.world = world;
    }

    private void paintThingAt(Position pos, Thing thing) {
        KillThingAt(pos);
        world.replaceThingAt(pos, thing);
    }

    private void KillThingAt(Position pos) {
        Thing origionalThing = world.getThingAt(pos);
        if (origionalThing instanceof Livable) {
            world.killThing((Livable)origionalThing);
        }
    }
    
    public void paintWallAt(Position pos) {
        paintThingAt(pos, new Wall(world, pos));
    }

    public void paintCloneOfAt(Position pos, Thing thingToClone) {
        paintThingAt(pos, thingToClone.clone());
    }
    
    public void paintNothingAt(Position pos) {
        KillThingAt(pos);
        world.removeThingAt(pos);
    }

    public void paintFoodAt(Position pos) {
        paintThingAt(pos, new Food(world, pos, Main.NEW_FOOD_ENERGY));
    }

    public void paintFoodAt(Position pos, float energy) {
        paintThingAt(pos, new Food(world, pos, energy));
    }
}