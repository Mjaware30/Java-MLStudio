package com.mlstudio.model;

import com.mlstudio.dataset.DataSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NaiveBayes extends MLModel {
    private static final long serialVersionUID = 1L;

    private Set<Double> classes;
    private Map<Double, Double> classPriors;
    private Map<Double, double[]> means;
    private Map<Double, double[]> variances;
    private int numFeatures;

    public NaiveBayes() {
        this.modelName = "Naive Bayes";
        this.classes = new HashSet<>();
        this.classPriors = new HashMap<>();
        this.means = new HashMap<>();
        this.variances = new HashMap<>();
    }

    @Override
    public void train(DataSet data) {
        classes.clear();
        classPriors.clear();
        means.clear();
        variances.clear();

        numFeatures = data.getFeatureCount();
        int rowCount = data.getRowCount();
        
        Map<Double, Integer> classCounts = new HashMap<>();

        for (int i = 0; i < rowCount; i++) {
            double label = data.getLabels()[i];
            classes.add(label);
            classCounts.put(label, classCounts.getOrDefault(label, 0) + 1);
        }

        for (Double cls : classes) {
            means.put(cls, new double[numFeatures]);
            variances.put(cls, new double[numFeatures]);
            classPriors.put(cls, (double) classCounts.get(cls) / rowCount);
        }

        for (int i = 0; i < rowCount; i++) {
            double label = data.getLabels()[i];
            double[] featureRow = data.getFeatures()[i];
            double[] clsMeans = means.get(label);
            for (int j = 0; j < numFeatures; j++) {
                clsMeans[j] += featureRow[j];
            }
        }

        for (Double cls : classes) {
            double[] clsMeans = means.get(cls);
            int count = classCounts.get(cls);
            for (int j = 0; j < numFeatures; j++) {
                clsMeans[j] /= count;
            }
        }

        for (int i = 0; i < rowCount; i++) {
            double label = data.getLabels()[i];
            double[] featureRow = data.getFeatures()[i];
            double[] clsMeans = means.get(label);
            double[] clsVars = variances.get(label);
            for (int j = 0; j < numFeatures; j++) {
                clsVars[j] += Math.pow(featureRow[j] - clsMeans[j], 2);
            }
        }

        for (Double cls : classes) {
            double[] clsVars = variances.get(cls);
            int count = classCounts.get(cls);
            for (int j = 0; j < numFeatures; j++) {
                clsVars[j] = (clsVars[j] / count) + 1e-9;
            }
        }

        this.isTrained = true;
    }

    @Override
    public double predict(double[] input) {
        double maxScore = -Double.MAX_VALUE;
        double bestClass = -1;

        for (Double cls : classes) {
            double score = Math.log(classPriors.get(cls));
            double[] clsMeans = means.get(cls);
            double[] clsVars = variances.get(cls);

            for (int j = 0; j < numFeatures; j++) {
                double mean = clsMeans[j];
                double var = clsVars[j];
                double x = input[j];
                
                double logLikelihood = -0.5 * Math.log(2 * Math.PI * var) - Math.pow(x - mean, 2) / (2 * var);
                score += logLikelihood;
            }

            if (score > maxScore) {
                maxScore = score;
                bestClass = cls;
            }
        }

        return bestClass;
    }
}
