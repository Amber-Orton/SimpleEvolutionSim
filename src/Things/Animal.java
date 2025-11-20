package Things;
import java.awt.Color;
import java.awt.image.BufferedImage;

import Run.Main;
import Run.World;
import Things.Helpers.ACTION;
import Things.Helpers.AnimalAttributes;
import Things.Helpers.DIRECTION;
import Things.Helpers.HasAppearance;
import Things.Helpers.NameCreator;
import Things.Helpers.Position;


public class Animal extends Edible implements HasAppearance {

    protected AnimalAttributes attributes;
    protected float health;
    protected String name;

    protected ACTION action;
    protected DIRECTION facing;


    /**
    * Constructor for Animal.
    * @param world The world instance the animal belongs to.
    * @param pos The initial Position of the animal.
    * @param attributes an Attributes object defining the animal's stats.
    * @param parent The parent animal, or null if this is a new animal without a parent.
    */
    public Animal(World world, Position pos, AnimalAttributes attributes, Animal parent) {
        super(world, pos);
        this.attributes = attributes;
        this.health = attributes.getMaxHealth();
        if (parent == null) {
            this.facing = DIRECTION.SOUTH; // Default facing direction for new animals
            this.energy = attributes.getMaxEnergy();
        } else {
            this.facing = parent.facing; // Inherit facing direction from parent
            this.energy = parent.attributes.getReproductionCost();
        }
        try {
            this.name = NameCreator.nextLine() + " " + (parent != null ? parent.getName().split(" ")[0]: "NoParent");
        } catch (Exception e) {
            e.printStackTrace();
            this.name = "Animal_errorname " + (parent != null ? parent.getName().split(" ")[0]: "NoParent");
        }
        System.out.println(getName() + " is born");
    }

    @Override
    public void run() {
        action = think();
        super.run();
    }


    @Override
    public void doAction(){
        if (!isAlive) {return;}
        switch (action) {
            case TURN_LEFT:
                facing = facing.turnLeft();
                world.posHasChanged(pos);
                break;
            
            case TURN_RIGHT:
                facing = facing.turnRight();
                world.posHasChanged(pos);
                break;

            case MOVE:
                move();
                break;

            case ATTACK:
                attack();
                break;

            case EAT:
                eat();
                break;

            case REPRODUCE:
                reproduce();
                break;

            case REST:
                rest();
                break;

            default:
                break;
        }
        super.doAction();
    }




    protected ACTION think() {
        System.out.println(this.getName() + " is thinking.");
        float[] thinkingInputs = new float[Main.NEURAL_NET_INPUT_SIZE];
        if (world == null) {
            System.out.println("World is not initialized for : " + this);
        }
        float[][] seen = see();
        int i = 0;
        for(float[] floats : seen) {
            for (float f : floats) {
                thinkingInputs[i++] = f;
            }
        }

        //devide by 100 to make in range 0 - maxstattotal since the cap for all is ~maxstattotal
        thinkingInputs[i++] = energy/attributes.getMaxStatTotal();
        thinkingInputs[i++] = health/attributes.getMaxStatTotal();
        thinkingInputs[i++] = attributes.getReproductionCost()/attributes.getMaxStatTotal();
        return attributes.getNeuralNet().think(thinkingInputs);
    }

    /**
    * Move the animal to a new position if it's adjacent.
    * Costs 1 energy to move, even if bumping into something.
    * @param newRow The row index of the new position.
    * @param newCol The column index of the new position.
    */
    protected void move() {
        removeEnergy(attributes.getMoveCost());

        
        Position newPos = getFacingPosition();

        if(isAdjacentTo(newPos)) {
            Thing thingAtNewPos = world.getThingAt(newPos);
            if (thingAtNewPos instanceof Nothing) {
                world.removeThing(this);
                world.putThingAt(newPos, this);
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

        Egg egg = new Egg(world, pos, attributes, this);
        world.addThing(pos, egg);
    }


    /**
     * Look at the four adjacent cells (up, down, left, right).
     * this is not synchronized, so the returned array will be only mostly accurate
     * this should not matter as see() will be called before think()
     * and the world is likely to change while thinking anyway.
     * @return An array of Thing objects in the order: [Up, Down, Left, Right].
     * If an adjacent cell is out of bounds, it will contain default wall.
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
     * Attempts to eat the thing the creature is facing.
     * 
     * @return true if an edible thing was eaten, false otherwise
     */
    protected boolean eat() {
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

    
    protected void rest() {
        removeEnergy(attributes.getRestEnergy());
        addHealth(attributes.getRestHealth());
    }


    /**
     * Attempts to attack the thing the creature is facing.
     * always costs energy even if unsucessfull
     * 
     * @return true if an Animal was attacked, false otherwise
     */
    protected boolean attack() {
        removeEnergy(attributes.getAttackCost());
        Thing thingBeingAttacked = getFacingThing();
        if (thingBeingAttacked.getClass() == Animal.class) {
            thingBeingAttacked.removeHealth(attributes.getAttackDamage());
            return true;
        } else {
            return false;
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
        return world.getThingAt(getFacingPosition());
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
    public BufferedImage getImage() {
        return attributes.getAppearance().getRotatedImage(facing);
    }

    @Override
    public Color getColor() {
        return null;
    }

    public AnimalAttributes getAnimalAttributes() {
        return attributes;
    }

    public float getHealth() {
        return health;
    }

    public String getName() {
        return name;
    }

    public ACTION getAction() {
        return action;
    }

    @Override
    public void die() {
        System.out.println(getName() + " has died.");
        super.die();
    }

    @Override
    public void removeHealth(float amount) {
        health -= amount;
        // Animal dies
        if (health <= 0) {
            if (energy > 0){
                Food food = new Food(world, pos, energy);
                world.killThing(this);
                world.putThingAt(pos, food);//place food with energy equal to itself in the world when it dies
            } else {
                world.killThing(this);
            }
        }
    }

    @Override
    public boolean needsToTick() {
        return true;
    }

    @Override
    public boolean needsToDoAction() {
        return true;
    }

    @Override
    protected int getasInt() {
        return 4;
    }
    
    
    @Override
    public String toString() {
        return "Health: " + health + '/' + attributes.getMaxHealth() + ", Energy: " + energy + '/' + attributes.getMaxEnergy() + ", Attack Damage: " + attributes.getAttackDamage() + ", " + super.toString() + ":Animal";
    }
    
}
