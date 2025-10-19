package NeuralNet;

public class ReLU extends ActivationFunction{

    @Override
    protected float calculate(float sum) {
        return (sum > 0) ? sum : 0;
    }

}
