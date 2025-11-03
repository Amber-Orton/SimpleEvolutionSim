package NeuralNet;

import java.util.ArrayList;
import java.util.Arrays;

public class HiddenNode extends HiddenOrOutputNode{

    protected HiddenNode(ArrayList<Node> parentNodes, float[] weights, float bias) {
        super(parentNodes, weights, bias, new ReLU());
    }

    @Override
    public String toString() {
        return "HiddenNode; bias: " + bias + ", weights: " + Arrays.toString(weights) + super.toString();
    }

}
