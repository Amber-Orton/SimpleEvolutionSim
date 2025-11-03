package NeuralNet;

import java.util.ArrayList;
import java.util.Arrays;

public class OutputNode extends HiddenOrOutputNode{

    protected OutputNode(ArrayList<Node> parentNodes, float[] weights, float bias) {
        super(parentNodes, weights, bias, new None());
    }

        @Override
    public String toString() {
        return "OutputNode; bias: " + bias + ", weights: " + Arrays.toString(weights) + super.toString();
    }

}
