package Things.Helpers;
import java.util.ArrayList;

import Main.Main;
import NeuralNet.NeuralNet;

public class AnimalAttributes {

    private static int statTotal;
    private static int attackCost;
    private static int moveCost;
    private static float mutationRate;
    private static float mutationFactor;
    private static float nodeInsertOrDeleteRate;
    private static int maxLayers;
    private static int existanceCost;
    private static int restCost;
    private static int eatCost;
    private static int turnCost;
    private static int restHealth;
    private static int hatchCycles;

    private float maxEnergy;
    private float maxHealth;
    private float attackDamage;
    private float reproductionCost;
    private NeuralNet neuralNet;
    private Appearance appearance;

    public AnimalAttributes(float maxEnergy, float maxHealth, float attackDamage, float reproductionCost, NeuralNet neuralNet, Appearance appearance) {
        this.maxEnergy = maxEnergy;
        this.maxHealth = maxHealth;
        this.reproductionCost = reproductionCost;
        this.attackDamage = attackDamage;
        this.neuralNet = neuralNet;
        this.appearance = appearance;
    }

    public static void setWorldAttributes(int statTotal, int attackCost, float mutationRate, float mutationFactor, float nodeInsertOrDeleteRate, int maxLayers, int existanceCost, int moveCost, int eatCost, int turnCost, int restEnergy, int restHealth, int hatchCycles) {
        AnimalAttributes.statTotal = statTotal;
        AnimalAttributes.attackCost = attackCost;
        AnimalAttributes.mutationRate = mutationRate;
        AnimalAttributes.mutationFactor = mutationFactor;
        AnimalAttributes.nodeInsertOrDeleteRate = nodeInsertOrDeleteRate;
        AnimalAttributes.maxLayers = maxLayers;
        AnimalAttributes.existanceCost = existanceCost;
        AnimalAttributes.moveCost = moveCost;
        AnimalAttributes.eatCost = eatCost;
        AnimalAttributes.turnCost = turnCost;
        AnimalAttributes.restCost = restEnergy;
        AnimalAttributes.restHealth = restHealth;
        AnimalAttributes.hatchCycles = hatchCycles;
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

        return new AnimalAttributes(newAttributes[0], newAttributes[1], newAttributes[2], newReproductionCost, generateMutatedNeuralNet(), getMutatedApperance());
    }

    private Appearance getMutatedApperance() {
        NibbleGrid8x8 mutatedData = appearance.getData().clone();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (Main.getRandom().nextDouble() < mutationRate) {
                    mutatedData.set(i, j, Main.getRandom().nextInt(16));
                }
            }
        }
        return new Appearance(mutatedData);
    }

    private NeuralNet generateMutatedNeuralNet() {
        ArrayList<ArrayList<float[][]>> neuralNetNodesWeightsAndBiases = neuralNet.getNeuralNetNodesWeightsAndBiases();
        for (ArrayList<float[][]> layer : neuralNetNodesWeightsAndBiases) {
            for (float[][] nodeWeightsAndBias : layer) {
                //mutate weights
                for (int i = 0; i < nodeWeightsAndBias[0].length; i++) {
                    if (Main.getRandom().nextFloat() < mutationRate) {
                        nodeWeightsAndBias[0][i] *= generateMutationFactor();
                    }
                }
                //mutate the bias
                if (Main.getRandom().nextFloat() < mutationRate) {
                    nodeWeightsAndBias[1][0] *= generateMutationFactor();
                }
            }
        }

        if (Main.getRandom().nextFloat() < nodeInsertOrDeleteRate) {
            int action = Main.getRandom().nextInt(3);
            if (action == 0) {
                // Insert a new node
                int layer = Main.getRandom().nextInt(neuralNetNodesWeightsAndBiases.size()-1); // -1 since output layer are fixed in size
                float[][] newNode = new float[2][];// create the node
                if (layer > 0) {
                    newNode[0] = new float[neuralNetNodesWeightsAndBiases.get(layer-1).size()];
                } else {
                    newNode[0] = new float[Main.NEURAL_NET_INPUT_SIZE];
                }
                newNode[1] = new float[1];
                // initialise the bias and weights to be very small for small mutations
                for (int i = 0; i < newNode[0].length; i++) {
                    newNode[0][i] = Main.getRandom().nextFloat()*mutationFactor;
                }
                newNode[1][0] = Main.getRandom().nextFloat()*mutationFactor;


                //TODO: change to use arrayList instead of float[][]
                //update the weights of the next layer
                for (int i = 0; i < neuralNetNodesWeightsAndBiases.get(layer + 1).size(); i++) {
                    float[][] oldWeightsAndBias = neuralNetNodesWeightsAndBiases.get(layer + 1).get(i);
                    float[][] newWeightsAndBias = new float[2][];
                    newWeightsAndBias[0] = new float[neuralNetNodesWeightsAndBiases.get(layer).size()];
                    newWeightsAndBias[1] = new float[1];
                    //copy the old weights
                    for (int j = 0; j < newWeightsAndBias[0].length-1; j++) {
                        newWeightsAndBias[0][j] = oldWeightsAndBias[0][j];
                    }
                    newWeightsAndBias[0][newWeightsAndBias[0].length-1] = Main.getRandom().nextFloat()*mutationFactor;//add the new weight
                    newWeightsAndBias[1][0] = oldWeightsAndBias[1][0];//copy the old bias
                    neuralNetNodesWeightsAndBiases.get(layer + 1).set(i, newWeightsAndBias);//put the new weights and bias back in the list
                }
            } else if (action == 1) {
                // add or remove a layer
                if (Main.getRandom().nextBoolean()) {
                    // Add a new layer
                    if (neuralNetNodesWeightsAndBiases.size() < maxLayers) {
                        int layer = Main.getRandom().nextInt(neuralNetNodesWeightsAndBiases.size());
                        ArrayList<float[][]> newLayer = new ArrayList<>();
                        int previousLayerSize;
                        if (layer > 0) {
                            previousLayerSize = neuralNetNodesWeightsAndBiases.get(layer-1).size();
                        } else {
                            previousLayerSize = Main.NEURAL_NET_INPUT_SIZE; // Input layer size
                        }

                        // Create nodes for the new layer same number to reduce the impact of adding a new layer
                        for (int i = 0; i < previousLayerSize; i++) {
                            float[][] newNode = new float[2][];
                            newNode[0] = new float[previousLayerSize];
                            newNode[1] = new float[1];
                            
                            // Initialize weights and bias with small random values
                            for (int j = 0; j < newNode[0].length; j++) {
                                newNode[0][j] = Main.getRandom().nextFloat() * mutationFactor;
                            }
                            newNode[0][i] = 1.0f;//set weight of the directly previous node to be 1 to reduce the impact of adding a new layer
                            newNode[1][0] = Main.getRandom().nextFloat() * mutationFactor;
                            newLayer.add(newNode);
                        }
                        
                        // Insert the new layer
                        neuralNetNodesWeightsAndBiases.add(layer, newLayer);
                    }
                } else {
                    // Remove a layer
                    if (neuralNetNodesWeightsAndBiases.size() > 2) { // Keep at least one hidden layer
                        int layer = Main.getRandom().nextInt(neuralNetNodesWeightsAndBiases.size() - 1);
                        
                        // Update weights for the layer after the removed layer
                        int previousLayerSize;
                        if (layer > 0){
                            previousLayerSize = neuralNetNodesWeightsAndBiases.get(layer - 1).size();
                        } else {
                            previousLayerSize = Main.NEURAL_NET_INPUT_SIZE; // Input layer size
                        }

                        for (float[][] node : neuralNetNodesWeightsAndBiases.get(layer + 1)) {
                            float[] newWeights = new float[previousLayerSize];
                            
                            // Initialize new weights with small random values
                            for (int i = 0; i < previousLayerSize; i++) {
                                newWeights[i] = Main.getRandom().nextFloat() * mutationFactor;
                            }
                            node[0] = newWeights;
                        }
                        
                        // Remove the layer
                        neuralNetNodesWeightsAndBiases.remove(layer);
                    }
                }
            } else if (action == 2) {
                // remove a node
                int layer = Main.getRandom().nextInt(neuralNetNodesWeightsAndBiases.size()-1);
                if (neuralNetNodesWeightsAndBiases.get(layer).size() > 1) {
                    int node = Main.getRandom().nextInt(neuralNetNodesWeightsAndBiases.get(layer).size());
                    neuralNetNodesWeightsAndBiases.get(layer).remove(node);

                    //update next layer
                    for (float[][] nextNode : neuralNetNodesWeightsAndBiases.get(layer + 1)) {
                        float[] oldWeights = nextNode[0];
                        float[] newWeights = new float[neuralNetNodesWeightsAndBiases.get(layer).size()];
                        int j = 0;
                        for (int i = 0; i < newWeights.length; i++) {
                            newWeights[i] = oldWeights[j++];
                            if (i == node) {
                                j++;
                            }
                        }
                        nextNode[0] = newWeights;
                    }
                }
            }
        }

        return new NeuralNet(neuralNetNodesWeightsAndBiases);
    }

    private float generateMutationFactor() {
        return 1.0f + (float)(Main.getRandom().nextGaussian() * mutationFactor);
    }

    public AnimalAttributes clone() {
        return new AnimalAttributes(maxEnergy, maxHealth, attackDamage, reproductionCost, neuralNet.clone(), appearance.clone());
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

    public NeuralNet getNeuralNet() {
        return neuralNet;
    }

    public int getMaxStatTotal() {
        return statTotal;
    }

    public int getMoveCost() {
        return moveCost;
    }

    public int getExistanceCost() {
        return existanceCost;
    }

    public int getTurnCost() {
        return turnCost;
    }

    public int getEatCost() {
        return eatCost;
    }

    public int getRestCost() {
        return restCost;
    }

    public int getRestHealth() {
        return restHealth;
    }

    public int getHatchCycles() {
        return hatchCycles;
    }

    public Appearance getAppearance() {
        return appearance;
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
