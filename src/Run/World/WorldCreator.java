package Run.World;

import java.util.ArrayList;

import NeuralNet.NeuralNet;
import Run.Main;
import Things.Animal;
import Things.Nothing;
import Things.Helpers.AnimalAttributes;
import Things.Helpers.Appearance;
import Things.Helpers.Position;

public class WorldCreator {
    private static final int INPUT_SIZE = Main.NEURAL_NET_INPUT_SIZE;
    private static final int OUTPUT_SIZE = Main.NEURAL_NET_OUTPUT_SIZE;

    public static World createWorld(int worldWidth, int worldHeight, int maxLayers, int statTotal, float initialAnimalDensity, float initialNeuralNetRandomness) {
        World world = new World(worldWidth, worldHeight);
        populateWorld(world, maxLayers, statTotal, initialAnimalDensity, initialNeuralNetRandomness);
        return world;
    }

    /**
     * Populate the world with Things.
     */
    private static void populateWorld(World world, int maxLayers,int statTotal, float initialAnimalDensity, float initialNeuralNetRandomness) {
        // first fill with Nothing
        for (int row = 0; row < world.getHeight(); row++) {
            for (int col = 0; col < world.getWidth(); col++) {
                Position position = new Position(row, col);
                world.nothingGrid[row][col] = new Nothing(world, new Position(row, col));
                world.putThingAt(position, world.nothingGrid[row][col]); // Empty cell
            }
        }

        // Randomly place animals by density
        int placed = 0;
        for (int row = 0; row < world.getHeight(); row++) {
            for (int col = 0; col < world.getWidth(); col++) {
                if (Main.getRandom().nextFloat() < initialAnimalDensity) {
                    NeuralNet net = buildRandomNeuralNet(maxLayers, INPUT_SIZE, OUTPUT_SIZE, initialNeuralNetRandomness);
                    AnimalAttributes attrs = buildRandomAnimalAttributes(statTotal, net);
                    Position pos = new Position(row, col);
                    world.putThingAt(pos, new Animal(world, pos, attrs, null));
                    placed++;
                }
            }
        }

        // Ensure at least one animal exists
        if (placed == 0) {
            int row = Main.getRandom().nextInt(world.getHeight());
            int col = Main.getRandom().nextInt(world.getWidth());
            NeuralNet net = buildRandomNeuralNet(maxLayers, INPUT_SIZE, OUTPUT_SIZE, initialNeuralNetRandomness);
            AnimalAttributes attrs = buildRandomAnimalAttributes(statTotal, net);
            Position pos = new Position(row, col);
            world.putThingAt(pos, new Animal(world, pos, attrs, null));
        }

        world.updateSnapshot();
    }

    /**
     * Build a random feed-forward network with:
     * - Input size fixed (not stored in the list) e.g., INPUT_SIZE
     * - Output layer fixed at 'outputSize' nodes (OUTPUT_SIZE)
     * - 1..(maxLayers-1) hidden layers (since the list includes hidden + output)
     * - Each hidden layer node has 'prevLayerSize' weights, first hidden layer uses 'inputSize'
     */
    private static NeuralNet buildRandomNeuralNet(int maxLayers, int inputSize, int outputSize, float initialNeuralNetRandomness) {
        // Ensure at least one hidden layer; 'maxLayers' counts hidden+output in the list
        int maxHiddenLayers = Math.max(1, maxLayers - 1);
        int hiddenLayers = 1 + Main.getRandom().nextInt(maxHiddenLayers); // [1, maxHiddenLayers]

        // Hidden layer node count bounds
        int minHiddenNodes = 4;
        int maxHiddenNodes = Math.max(minHiddenNodes, inputSize);

        ArrayList<ArrayList<float[][]>> layers = new ArrayList<>();
        int prevSize = inputSize;

        // Hidden layers
        for (int h = 0; h < hiddenLayers; h++) {
            int nodes = minHiddenNodes + Main.getRandom().nextInt(maxHiddenNodes - minHiddenNodes + 1);
            ArrayList<float[][]> layer = new ArrayList<>();

            for (int n = 0; n < nodes; n++) {
                float[][] node = new float[2][];
                node[0] = new float[prevSize]; // weights
                node[1] = new float[1];        // bias

                // Small random weights/bias around 0
                for (int i = 0; i < prevSize; i++) {
                    node[0][i] = (Main.getRandom().nextFloat() - 0.5f) * initialNeuralNetRandomness *2; // +-initialNeuralNetRandomness
                }
                node[1][0] = (Main.getRandom().nextFloat() - 0.5f) * initialNeuralNetRandomness *2;

                layer.add(node);
            }

            layers.add(layer);
            prevSize = nodes;
        }

        // Output layer (fixed size)
        ArrayList<float[][]> outputLayer = new ArrayList<>();
        for (int n = 0; n < outputSize; n++) {
            float[][] node = new float[2][];
            node[0] = new float[prevSize]; // weights from last hidden layer
            node[1] = new float[1];        // bias

            for (int i = 0; i < prevSize; i++) {
                node[0][i] = (Main.getRandom().nextFloat() - 0.5f) * initialNeuralNetRandomness *2;
            }
            node[1][0] = (Main.getRandom().nextFloat() - 0.5f) * initialNeuralNetRandomness *2;

            outputLayer.add(node);
        }
        layers.add(outputLayer);

        return new NeuralNet(layers);
    }

    /**
     * Randomize the 3 non-net stats so they sum to 'statTotal'.
     * Reproduction cost is randomized separately (not part of the 100) and must be < maxEnergy (stat3).
     * Constructor: new AnimalAttributes(stat1, stat2, stat3, reproductionCost, net)
     */
    private static AnimalAttributes buildRandomAnimalAttributes(int statTotal, NeuralNet net) {
        // Minimums to avoid useless animals; ensure stat3 (max energy) is not tiny.
        int min1 = 10;
        int min2 = 10;
        int min3 = Math.max(20, statTotal / 5); // at least 20% of total or 20

        int[] parts = randomTripleSum(statTotal, min1, min2, min3);
        int s1 = parts[0];
        int s2 = parts[1];
        int s3 = parts[2]; // treated as max energy

        // Reproduction cost: random but always less than max energy (s3) and not in the 100 total.
        // Keep it reasonable: between 2 and min(6, s3-1) as lower bound, up to s3-1 as upper.
        int maxRepCost = Math.max(2, s3 - 10);
        int minRepCost = Math.min(6, maxRepCost); // prefer small-ish base cost
        int reproductionCost = minRepCost + (maxRepCost > minRepCost ? Main.getRandom().nextInt(maxRepCost - minRepCost + 1) : 0);

        return new AnimalAttributes(s1, s2, s3, reproductionCost, net, new Appearance());
    }

    /**
     * Split 'total' into three non-negative integers with given minimums.
     * The result sums to 'total'.
     */
    private static int[] randomTripleSum(int total, int minA, int minB, int minC) {
        int base = minA + minB + minC;
        if (base > total) {
            // Fallback: clamp mins proportionally if someone changes totals
            float scale = (float) total / Math.max(1, base);
            minA = Math.max(0, Math.round(minA * scale));
            minB = Math.max(0, Math.round(minB * scale));
            minC = Math.max(0, total - minA - minB);
            base = minA + minB + minC;
        }
        int remaining = total - base;

        int extraA = remaining == 0 ? 0 : Main.getRandom().nextInt(remaining + 1);
        int extraB = remaining - extraA == 0 ? 0 : Main.getRandom().nextInt(remaining - extraA + 1);
        int extraC = remaining - extraA - extraB;

        return new int[] { minA + extraA, minB + extraB, minC + extraC };
    }
}
