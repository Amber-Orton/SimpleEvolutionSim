package NeuralNet;

import java.util.ArrayList;
import java.util.Arrays;

public class HiddenNode extends Node{
    protected ArrayList<Node> parentNodes;
    protected float[] weights;
    protected float bias;
    private ActivationFunction activationFunction;

    protected HiddenNode(ArrayList<Node> parentNodes, float[] weights, float bias, ActivationFunction activationFunction) {
        this.parentNodes = parentNodes;
        this.weights = weights;
        this.bias = bias;
        this.activationFunction = activationFunction;
    }

    @Override
    protected void update() {
        float sum = 0;
        int i = 0;
        for (Node parentNode : parentNodes) {
            sum += parentNode.getOutput() * weights[i++];
        }
        output = activationFunction.calculate(sum + bias);
    }

    @Override
    public String toString() {
        return "HiddenNode, bias: " + bias + ", weights: " + Arrays.asList(weights) + super.toString();
    }

    

}
