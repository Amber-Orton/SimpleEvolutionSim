package NeuralNet;

import java.util.ArrayList;

public class HiddenOrOutputNode extends Node{
    protected ArrayList<Node> parentNodes;
    protected float[] weights;
    protected float bias;
    private ActivationFunction activationFunction;

    protected HiddenOrOutputNode(ArrayList<Node> parentNodes, float[] weights, float bias, ActivationFunction activationFunction) {
        if (parentNodes.size() != weights.length) {
            throw new IllegalArgumentException("Mismatched parent nodes and weights" + parentNodes.size() + "!=" + weights.length);
        }
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



    

}
