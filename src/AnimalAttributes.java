import java.util.Random;

public class AnimalAttributes {

    private static final Random random = new Random();

    private static int statTotal;
    private static int attackCost;
    private static float mutationRate;
    


    private float maxEnergy;
    private float maxHealth;
    private float attackDamage;
    private float reproductionCost;

    public AnimalAttributes(float maxEnergy, float maxHealth, float attackDamage, float reproductionCost) {
        this.maxEnergy = maxEnergy;
        this.maxHealth = maxHealth;
        this.reproductionCost = reproductionCost;
        this.attackDamage = attackDamage;
    }

    public static void setWorldAttributes(int statTotal, int attackCost, float mutationRate) {
        AnimalAttributes.statTotal = statTotal;
        AnimalAttributes.attackCost = attackCost;
        AnimalAttributes.mutationRate = mutationRate;
    }

    public AnimalAttributes generateMutatedAttributes() {
        float[] newAttributes = new float[]{maxEnergy, maxHealth, attackDamage};
        float newAttributesSum = 0f;
        
        for (int i = 0; i < newAttributes.length; i++) {
            newAttributes[i] *= generateMutationFactor();
            if (newAttributes[i] < 1){
                newAttributes[i] = 1; //cap minimum attributes at 1
            }
            newAttributesSum += newAttributes[i];
        }

        float scalar = statTotal / newAttributesSum;

        for (int i = 0; i < newAttributes.length; i++) {
            newAttributes[i] *= scalar;
        }

        float newReproductionCost = reproductionCost * generateMutationFactor();
        if (newReproductionCost > newAttributes[0]) { // if reproduction cost is bigger than energy cap it to energy
            newReproductionCost = newAttributes[0];
        }

        return new AnimalAttributes(newAttributes[0], newAttributes[1], newAttributes[2], newReproductionCost);
    }

    private float generateMutationFactor() {
        return 1 + (((float)random.nextGaussian()*2 - 1) * mutationRate);
    }
    


    public float getAttackDamage() {
        return attackDamage;
    }

    public float getMaxEnergy() {
        return maxEnergy;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public float getReproductionCost() {
        return reproductionCost;
    }

    public int getAttackCost() {
        return attackCost;
    }


    @Override
    public String toString() {
        return "AnimalAttributes{" +
                "maxEnergy=" + maxEnergy +
                ", maxHealth=" + maxHealth +
                ", reproductionCost=" + reproductionCost +
                ", attackDamage=" + attackDamage +
                '}';
    }
}
