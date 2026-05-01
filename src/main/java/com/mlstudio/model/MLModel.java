package com.mlstudio.model;

import com.mlstudio.dataset.DataSet;
import java.io.Serializable;

public abstract class MLModel implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String modelName;
    protected boolean isTrained;
    protected double accuracy;

    public abstract void train(DataSet data);

    public abstract double predict(double[] input);

    public double evaluate(DataSet testData) {
        int correct = 0;
        int total = testData.getRowCount();

        for (int i = 0; i < total; i++) {
            double predicted = predict(testData.getFeatures()[i]);
            double actual = testData.getLabels()[i];

            if (Math.round(predicted) == Math.round(actual)) {
                correct++;
            }
        }

        this.accuracy = ((double) correct / total) * 100.0;
        System.out.println("Debug Log - Evaluation complete. Model: " + modelName + " Accuracy: " + this.accuracy + "%");
        return this.accuracy;
    }

    public String getModelName() {
        return modelName;
    }

    public boolean isTrained() {
        return isTrained;
    }

    public double getAccuracy() {
        return accuracy;
    }
}
