package Things;
import java.awt.Color;

import Run.World;
import Things.Helpers.AnimalAttributes;
import Things.Helpers.Position;

public class Egg extends Edible{
    private AnimalAttributes attributes;
    private Animal parent;
    private int cyclesToHatch;
    private boolean isHatched = false;
    private Animal child;

    public Egg(World world, Position pos, AnimalAttributes attributes, Animal parent) {
        super(world, pos);
        this.attributes = attributes;
        this.parent = parent;
        this.cyclesToHatch = attributes.getHatchCycles();
        this.energy = attributes.getReproductionCost();
    }

    @Override
    public void run(){
        cyclesToHatch--;
        super.run();
    }

    @Override
    //egg is special case as it needs to do calculations when it does action
    public void doAction() {
        if (!isAlive) { return; }
        if (world.posIsNothing(pos)) {
            world.putThingAt(pos, this);//attempt to place itself in the world
            tick();
        }
        if (world.getThingAt(pos) == this) {
            tick();
        }
        super.doAction();
    }
    
    private void tick() {
        if (cyclesToHatch <= 0){
            hatch();
        }
    }


    public void hatch() {
        child = new Animal(world, pos, attributes.generateMutatedAttributes(), parent);
        isHatched = true;

        world.replaceThing(this, child);
        world.killThing(this);
    }

    public Animal getChild() {
        return child;
    }

    public boolean isHatched() {
        return isHatched;
    }

    public int getCyclesToHatch() {
        return cyclesToHatch;
    }

    public AnimalAttributes getAnimalAttributes() {
        return attributes;
    }

    public Animal getParent() {
        return parent;
    }

    @Override
    public boolean needsToDoAction() {
        return true;
    }

    @Override
    public boolean needsToTick() {
        return true;
    }

    @Override
    public String getName() {
        return "Egg";
    }

    @Override
    public Color getColor() {
        return Color.PINK;
    }

    @Override
    protected int getasInt() {
        return 3;
    }

    
    @Override
    public String toString() {
        return super.toString() + ":Egg";
    }

    

}
