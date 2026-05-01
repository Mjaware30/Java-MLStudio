package com.mlstudio.model;

import com.mlstudio.dataset.DataSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KNNClassifier extends MLModel {
    private static final long serialVersionUID = 1L;

    private int k;
    private List<double[]> trainingFeatures;
    private List<Double> trainingLabels;

    public KNNClassifier(int k) {
        this.k = k;
        this.modelName = "KNN Classifier (k=" + k + ")";
        this.trainingFeatures = new ArrayList<>();
        this.trainingLabels = new ArrayList<>();
    }

    @Override
    public void train(DataSet data) {
        this.trainingFeatures.clear();
        this.trainingLabels.clear();

        for (int i = 0; i < data.getRowCount(); i++) {
            this.trainingFeatures.add(data.getFeatures()[i]);
            this.trainingLabels.add(data.getLabels()[i]);
        }
        this.isTrained = true;
    }

    @Override
    public double predict(double[] input) {
        int n = trainingFeatures.size();
        double[][] distances = new double[n][2]; // [distance, label]

        for (int i = 0; i < n; i++) {
            double dist = 0.0;
            double[] trainFeature = trainingFeatures.get(i);
            for (int j = 0; j < input.length; j++) {
                dist += Math.pow(input[j] - trainFeature[j], 2);
            }
            distances[i][0] = Math.sqrt(dist);
            distances[i][1] = trainingLabels.get(i);
        }

        Arrays.sort(distances, Comparator.comparingDouble(a -> a[0]));

        Map<Double, Integer> votes = new HashMap<>();
        for (int i = 0; i < Math.min(k, n); i++) {
            double label = distances[i][1];
            votes.put(label, votes.getOrDefault(label, 0) + 1);
        }

        double majorityClass = -1;
        int maxVotes = -1;
        for (Map.Entry<Double, Integer> entry : votes.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                majorityClass = entry.getKey();
            }
        }

        return majorityClass;
    }
}
