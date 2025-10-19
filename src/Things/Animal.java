package Things;
import java.awt.Color;

import Run.World;


public class Animal extends Edible{

    protected AnimalAttributes attributes;
    protected float health;


    protected DIRECTION facing;


    /**
    * Constructor for Animal.
    * @param world The world instance the animal belongs to.
    * @param pos The initial Position of the animal.
    * @param attributes an Attributes object defining the animal's stats.
    * @param parent The parent animal, or null if this is a new animal without a parent.
    */
    public Animal(World world, Position pos, AnimalAttributes attributes, Animal parent) {
        super(Color.BLACK, world, pos);
        this.attributes = attributes;
        this.health = attributes.getMaxHealth();
        if (parent == null) {
            this.facing = DIRECTION.SOUTH; // Default facing direction for new animals
            this.energy = attributes.getMaxEnergy();
        } else {
            this.facing = parent.facing; // Inherit facing direction from parent
            this.energy = parent.attributes.getReproductionCost();
        }
    }

    @Override
    public void run() {
        eat();
        super.run();
    }


    protected ACTION think() {
        float[] thinkingInputs = new float[18];
        float[][] seen = see();
        int i = 0;
        for(float[] floats : seen) {
            for (float f : floats) {
                thinkingInputs[i++] = f;
            }
        }
        thinkingInputs[i++] = energy;
        thinkingInputs[i++] = health;
        thinkingInputs[i++] = attributes.getReproductionCost();
        return attributes.getNeuralNet().think(thinkingInputs);
    }

    /**
    * Move the animal to a new position if it's adjacent.
    * Costs 1 energy to move, even if bumping into something.
    * @param newRow The row index of the new position.
    * @param newCol The column index of the new position.
    */
    protected void move() {
        removeEnergy(1);
        if (Thread.interrupted()) {return;}

        
        Position newPos = getFacingPosition();

        if(isAdjacentTo(newPos)) {
            if (Thread.interrupted() || !isAlive) {return;}
            synchronized (world) {
                if (Thread.interrupted() || !isAlive) {return;}
                Thing thingAtNewPos = world.getThingAt(newPos);
                if (thingAtNewPos instanceof Nothing) {
                    world.removeThing(this);
                    world.putThingAt(newPos, this);
                }
            }
        }else {
            throw new IllegalArgumentException("Can only move to adjacent cells. Tried to move from " + pos + " to " + newPos);
        }
    }

    
    

    /**
     * Reproduce if enough energy is available.
     * Costs some energy and creates an child in an adjacent cell if possible.
     * If no adjacent cell is free, reproduction fails but energy is still lost.
     * If the animal does not have enough energy, reproduction does not occur but energy and health are still lost.
     */
    protected void reproduce() {
        removeEnergy(attributes.getReproductionCost());

        if (energy <= 0) {
            return;
        }

        if (Thread.interrupted() || !isAlive) {return;}
        synchronized (world) {
            if (Thread.interrupted() || !isAlive) {return;}
            Egg egg = new Egg(world, pos, attributes, this);
            world.addThing(pos, egg);
        }
    }


    /**
     * Look at the four adjacent cells (up, down, left, right).
     * this is not synchronized, so the returned array will be only mostly accurate
     * this should not matter as see() will be called before think()
     * and the world is likely to change while thinking anyway.
     * @return An array of Thing objects in the order: [Up, Down, Left, Right].
     * If an adjacent cell is out of bounds, it will contain null.
     */
    protected float[][] see() {
        Position facingPosition = getFacingPosition();
        Thing[] seenThings;
        switch (facing) {
            case DIRECTION.NORTH:
                seenThings = new Thing[]{
                    world.getThingAt(facingPosition.add(new Position(0, -1))),
                    getFacingThing(),
                    world.getThingAt(facingPosition.add(new Position(0, +1)))
                };
                break;

            case DIRECTION.EAST:
                seenThings = new Thing[]{
                    world.getThingAt(facingPosition.add(new Position(-1, 0))),
                    getFacingThing(),
                    world.getThingAt(facingPosition.add(new Position(+1, 0)))
                };
                break;
            
            case DIRECTION.SOUTH:
                seenThings = new Thing[]{
                    world.getThingAt(facingPosition.add(new Position(0, +1))),
                    getFacingThing(),
                    world.getThingAt(facingPosition.add(new Position(0, -1)))
                };
                break;

            case DIRECTION.WEST:
                seenThings = new Thing[]{
                    world.getThingAt(facingPosition.add(new Position(+1, 0))),
                    getFacingThing(),
                    world.getThingAt(facingPosition.add(new Position(-1, 0)))
                };
                break;
        
            default:
                return null;
        }
        float[][] out = new float[3][5];
        int i = 0;
        for (Thing thing : seenThings) {
            out[i][thing.getasInt()] = 1;
        }
        return out;
    }
    
    /**
     * turns the animal left if direction <= -0.5 and right if direction >= 0.5 and does not turn otherwise
     * @param direction float of which direction to turn
     */
    protected void turn(float direction) {
        if (direction <= -0.5) {// turn left
            facing = facing.turnLeft();
        } else if (direction >= 0.5) {
            facing = facing.turnRight();
        }
    }


    /**
     * Attempts to eat the thing the creature is facing.
     * 
     * @return true if an edible thing was eaten, false otherwise
     */
    protected boolean eat() {
        if (Thread.interrupted() || !isAlive) {return false;}
        synchronized (world){
            if (Thread.interrupted() || !isAlive) {return false;}
            Thing thingToEat = getFacingThing();
            if (thingToEat instanceof Edible) {
                Edible edibleToEat = (Edible)thingToEat;
                world.killThing(thingToEat);
                addEnergy(edibleToEat.getEnergy());
                return true;
            } else {
                return false;
            }
        }
    }

    
    protected void rest() {
        removeEnergy(3);
        addHealth(2);
    }


    /**
     * Attempts to attack the thing the creature is facing.
     * always costs energy even if unsucessfull
     * 
     * @return true if an Animal was attacked, false otherwise
     */
    protected boolean attack() {
        removeEnergy(attributes.getAttackCost());
        if (Thread.interrupted() || !isAlive) {return false;}
        synchronized(world){
            if (Thread.interrupted() || !isAlive) {return false;}
            Thing thingBeingAttacked = getFacingThing();
            if (thingBeingAttacked.getClass() == Animal.class) {
                thingBeingAttacked.removeHealth(attributes.getAttackDamage());
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Check if the new position is adjacent (up, down, left, right) of the current position.
     * @param newPos The new position to check.
     * @return true if the new position is adjacent, false otherwise.
     */
    protected boolean isAdjacentTo(Position newPos) {
        if (Math.abs(newPos.getRow() - pos.getRow()) + Math.abs(newPos.getCol() - pos.getCol()) == 1) {
            return true;
        }
        return false;
    }

    protected Position getFacingOffset() {
        return getFacingOffset(facing);
    }
    
    protected Position getFacingOffset(DIRECTION facing) {
        switch (facing) {
            case NORTH:
                return new Position(-1, 0);
            case EAST:
                return new Position(0, 1);
            case SOUTH:
                return new Position(1, 0);
            case WEST:
                return new Position(0, -1);
            default:
                throw new IllegalStateException("Unexpected value: " + facing);
        }
    }

    protected Position getFacingPosition() {
        Position offset = getFacingOffset();
        return pos.add(offset);
    }

    protected Thing getFacingThing() {
        if (Thread.interrupted() || !isAlive) {return null;}
        synchronized (world) {
            if (Thread.interrupted() || !isAlive) {return null;}
            return world.getThingAt(getFacingPosition());
        }
    }

    
    protected void addEnergy(float amount) {
        energy = Math.min(attributes.getMaxEnergy(), energy + amount);
    }

    protected void removeEnergy(float amount) {
        energy -= amount;
        if (energy < 0) {
            removeHealth(-energy); // Lose health if out of energy
            energy = 0;
        }
    }

    protected void addHealth(float amount) {
        health = Math.min(attributes.getMaxHealth(), health + amount);
    }

    @Override
    public void removeHealth(float amount) {
        health -= amount;
        if (health <= 0) {
            // Animal dies
            if (Thread.interrupted() || !isAlive) {return;}
            synchronized (world) {
                if (Thread.interrupted() || !isAlive) {return;}
                if (energy > 0){
                    Food food = new Food(world, pos, energy);
                    world.replaceThing(this, food);//place food with energy equal to itself in the world when it dies
                }
                world.killThing(this);
            }
        }
    }

        @Override
    protected int getasInt() {
        return 4;
    }
    
    
    @Override
    public String toString() {
        return "Health: " + health + '/' + attributes.getMaxHealth() + ", Energy: " + energy + '/' + attributes.getMaxEnergy() + ", " + super.toString() + ":Animal";
    }
    
}
