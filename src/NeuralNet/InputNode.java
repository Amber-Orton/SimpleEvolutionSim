package NeuralNet;

public class InputNode extends Node{

    private NeuralNet neuralNet;

    protected InputNode(NeuralNet neuralNet) {
        this.neuralNet = neuralNet;
    }

    @Override
    protected void update() {
        output = neuralNet.getNextInput();
    }

    @Override
    public String toString() {
        return "InputNode, " + super.toString();
    }

}
