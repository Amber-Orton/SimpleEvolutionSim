package NeuralNet;

abstract class Node {
    protected float output;
    
    protected ActivationFunction activationFunction;

    protected abstract void update();

    protected float getOutput() {
        return output;
    }

    @Override
    public String toString(){
        return "current output: " + output;
    }
}
