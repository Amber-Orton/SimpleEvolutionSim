package NeuralNet;

public class None extends ActivationFunction{

    @Override
    protected float calculate(float sum) {
        return sum;
    }

}
