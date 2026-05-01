package com.mlstudio.model;

public class ModelFactory {
    public static MLModel create(String type, int kOrEpochs, double learningRate) {
        if (type == null) {
            throw new IllegalArgumentException("Model type cannot be null");
        }
        
        switch (type.toLowerCase()) {
            case "knn":
                return new KNNClassifier(kOrEpochs);
            case "linearregression":
                return new LinearRegression(learningRate, kOrEpochs);
            case "naivebayes":
                return new NaiveBayes();
            default:
                throw new IllegalArgumentException("Unknown model type: " + type);
        }
    }
}
