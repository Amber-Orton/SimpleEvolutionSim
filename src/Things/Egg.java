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

    public void run() {
        tick();
        super.run();
    }
    
    private void tick() {
        if (parent.getThread() != null){//wait untill parent has finished incase it is going to move off of the egg this tick
            try {
                parent.getThread().join();
            } catch (InterruptedException e) {
                return;//is killed
            }
        }

        if (Thread.interrupted() || !isAlive) {return;}
        synchronized(world){
            if (Thread.interrupted() || !isAlive) {return;}
            if (world.getThingAt(pos) != parent && world.posIsNothing(pos)){
                world.putThingAt(pos, this);//attempt to place itself in the world
            }
        }
        if (cyclesToHatch-- <= 0){
            hatch();
        }
    }

    public void hatch() {
        Animal child = new Animal(world, pos, attributes.generateMutatedAttributes(), parent);

        if (Thread.interrupted() || !isAlive) {return;}
        synchronized (world) {
            if (Thread.interrupted() || !isAlive) {return;}
            world.replaceThing(this, child);
            world.killThing(this);
        }
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
