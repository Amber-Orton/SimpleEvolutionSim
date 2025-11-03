package Things;
import java.awt.Color;

import Run.World;

public class Egg extends Edible{
    private AnimalAttributes attributes;
    private Animal parent;
    private int cyclesToHatch;

    public Egg(World world, Position pos, AnimalAttributes attributes, Animal parent) {
        super(Color.PINK, world, pos);
        this.attributes = attributes;
        this.parent = parent;
        this.cyclesToHatch = 2;
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
        tick();
    }
    
    private void tick() {
        if (cyclesToHatch <= 0){
            hatch();
        }
    }

    public void perantMoved() {
        world.putThingAt(pos, this);//attempt to place itself in the world
        tick();
    }

    public void hatch() {
        Animal child = new Animal(world, pos, attributes.generateMutatedAttributes(), parent);

        world.replaceThing(this, child);
        world.killThing(this);
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
