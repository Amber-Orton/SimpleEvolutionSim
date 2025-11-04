package Run;

import java.util.ArrayList;

import NeuralNet.NeuralNet;
import Things.Animal;
import Things.AnimalAttributes;
import Things.Nothing;
import Things.Position;

public class WorldCreator {

    public static World createWorld(int worldWidth, int worldHeight) {
        World world = new World(worldWidth, worldHeight);
        populateWorld(world);
        return world;
    }

    /**
     * Populate the world with Things.
     */
    private static void populateWorld(World world) {
        // first fill with Nothing
        for (int row = 0; row < world.getHeight(); row++) {
            for (int col = 0; col < world.getWidth(); col++) {
                Position position = new Position(row, col);
                world.nothingGrid[row][col] = new Nothing(world, new Position(row, col));
                world.putThingAt(position, world.nothingGrid[row][col]); // Empty cell
            }
        }

        ArrayList<ArrayList<float[][]>> testNet = new ArrayList<>();

        // ----- Hidden Layer (3 nodes, each with 18 weights + 1 bias) -----
        ArrayList<float[][]> hiddenLayer = new ArrayList<>();

        hiddenLayer.add(new float[][] {
            { 0.2f, -0.5f, 0.1f, 0.4f, 0.2f, -0.5f, 0.1f, 0.4f, 0.2f, -0.5f, 0.1f, 0.4f, 0.2f, -0.5f, 0.1f, 0.4f, 0.2f, 0.8f},  // weights
            { 0.05f }                     // bias
        });
        hiddenLayer.add(new float[][] {
            { -0.3f, 0.8f, -0.2f, 0.6f, 0.2f, -0.5f, 0.1f, 0.4f, 0.2f, -0.5f, 0.1f, 0.4f, 0.2f, -0.5f, 0.1f, 0.4f, 0.2f, 0.8f },
            { -0.1f }
        });
        hiddenLayer.add(new float[][] {
            { 0.7f, -0.4f, 0.3f, -0.9f, 0.2f, -0.5f, 0.1f, 0.4f, 0.2f, -0.5f, 0.1f, 0.4f, 0.2f, -0.5f, 0.1f, 0.4f, 0.2f, 0.8f },
            { 0.2f }
        });

        testNet.add(hiddenLayer);

        // ----- Output Layer (7 nodes, each with 3 weights + 1 bias) -----
        ArrayList<float[][]> outputLayer = new ArrayList<>();

        outputLayer.add(new float[][] {
            { 0.5f, -0.1f, 0.3f },
            { 0.1f }
        });
        outputLayer.add(new float[][] {
            { -0.2f, 0.7f, 0.1f },
            { 0.05f }
        });
        outputLayer.add(new float[][] {
            { 0.6f, -0.5f, 0.4f },
            { -0.2f }
        });
        outputLayer.add(new float[][] {
            { -0.3f, 0.9f, -0.7f },
            { 0.1f }
        });
        outputLayer.add(new float[][] {
            { 0.2f, -0.8f, 0.5f },
            { 0.0f }
        });
        outputLayer.add(new float[][] {
            { -0.4f, 0.2f, 0.6f },
            { -0.15f }
        });
        outputLayer.add(new float[][] {
            { 0.1f, 0.3f, -0.2f },
            { 0.07f }
        });

        testNet.add(outputLayer);


        world.putThingAt(new Position(1, 1), new Animal(world, new Position(1,1),
            new AnimalAttributes(33, 33, 34, 3, new NeuralNet(testNet)),
            null));
    }
    
}
