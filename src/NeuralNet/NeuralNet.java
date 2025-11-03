package NeuralNet;

import java.util.ArrayList;

import Things.ACTION;

public class NeuralNet {

    ArrayList<ArrayList<Node>> neuralNetNodes = new ArrayList<>();
    int inputPos;
    float[] inputs;

    public NeuralNet(ArrayList<ArrayList<float[][]>> neuralNetNodesWeightsAndBiases){
        //create input layer
        ArrayList<Node> inputLayer = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            inputLayer.add(new InputNode(this));
        }
        this.neuralNetNodes.add(inputLayer);
        
        //add hidden layers
        int layerNum = 1;
        for (ArrayList<float[][]> layer : neuralNetNodesWeightsAndBiases) {
            neuralNetNodes.add(new ArrayList<>());
            if (layerNum < neuralNetNodesWeightsAndBiases.size() - 1){
                for (float[][] nodeWeightsAndBiases : layer) {
                    neuralNetNodes.get(layerNum).add(new HiddenNode(neuralNetNodes.get(layerNum -1),nodeWeightsAndBiases[0], nodeWeightsAndBiases[1][0]));
                }
            } else {//last layer is the output nodes (should be 7)
                for (float[][] nodeWeightsAndBiases : layer) {
                    neuralNetNodes.get(layerNum).add(new OutputNode(neuralNetNodes.get(layerNum -1),nodeWeightsAndBiases[0], nodeWeightsAndBiases[1][0]));
                }
            }
            layerNum++;
        }
        
        System.out.println("Created a NeuralNet!");
        System.out.println(this);
    }
    
    public ACTION think(float[] inputs) {
        inputPos = 0;
        this.inputs = inputs;
        for (ArrayList<Node> nodes : neuralNetNodes) {
            for (Node node : nodes) {
                node.update();
            }
        }

        // soft max algorithm for last layer of nodes
        ArrayList<Node> lastLayer = neuralNetNodes.get(neuralNetNodes.size()-1);
        float[] outputProbabilities = new float[lastLayer.size()];
        float max = 0f;
        for (int i = 0; i < lastLayer.size(); i++) {
            outputProbabilities[i] = lastLayer.get(i).getOutput();
            if (outputProbabilities[i] > max) max = outputProbabilities[i];
        }
        float sum = 0;
        for (int i = 0; i < outputProbabilities.length; i++) {
            outputProbabilities[i] -= max;
            outputProbabilities[i] = (float)Math.exp(outputProbabilities[i]);
            sum += outputProbabilities[i];
        }
        
        for (int i = 0; i < outputProbabilities.length; i++) {
            outputProbabilities[i] = outputProbabilities[i]/sum;
        }

        // work out action based on probabilities
        double num = Math.random();
        double numTot = 0;
        int i = 0;
        while (numTot <= num){
            numTot += outputProbabilities[i++];
        }


        return ACTION.values()[i-1];
    }

    protected float getNextInput(){
        return inputs[inputPos++];
    }

    @Override
    public String toString() {
        return "NeuralNet{" +
                neuralNetNodes +
                '}';
    }
}
