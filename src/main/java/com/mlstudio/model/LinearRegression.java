package com.mlstudio.model;

import com.mlstudio.dataset.DataSet;
import java.util.ArrayList;
import java.util.List;

public class LinearRegression extends MLModel {
    private static final long serialVersionUID = 1L;

    private double learningRate;
    private int epochs;
    private double[] weights;
    private double bias;
    private List<Double> lossHistory;

    public LinearRegression(double learningRate, int epochs) {
        this.learningRate = learningRate;
        this.epochs = epochs;
        this.modelName = "Linear Regression (epochs=" + epochs + ", lr=" + learningRate + ")";
        this.lossHistory = new ArrayList<>();
    }

    @Override
    public void train(DataSet data) {
        int numFeatures = data.getFeatureCount();
        int rowCount = data.getRowCount();
        
        weights = new double[numFeatures];
        bias = 0.0;
        lossHistory.clear();

        for (int epoch = 0; epoch < epochs; epoch++) {
            double[] dWeights = new double[numFeatures];
            double dBias = 0.0;
            double loss = 0.0;

            for (int i = 0; i < rowCount; i++) {
                double[] features = data.getFeatures()[i];
                double actual = data.getLabels()[i];
                double predicted = predict(features);
                
                double error = predicted - actual;
                loss += error * error;

                for (int j = 0; j < numFeatures; j++) {
                    dWeights[j] += error * features[j];
                }
                dBias += error;
            }

            loss /= rowCount; // MSE
            lossHistory.add(loss);

            for (int j = 0; j < numFeatures; j++) {
                weights[j] -= learningRate * (dWeights[j] * 2 / rowCount);
            }
            bias -= learningRate * (dBias * 2 / rowCount);

            if (epoch % 100 == 0) {
                System.out.println("Epoch " + epoch + " Loss: " + loss);
            }
        }

        this.isTrained = true;
    }

    @Override
    public double predict(double[] input) {
        double result = bias;
        if (weights != null) {
            for (int i = 0; i < input.length; i++) {
                result += weights[i] * input[i];
            }
        }
        return result;
    }

    public List<Double> getLossHistory() {
        return lossHistory;
    }
}
