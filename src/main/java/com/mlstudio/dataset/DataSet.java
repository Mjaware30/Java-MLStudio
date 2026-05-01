package com.mlstudio.dataset;

import java.io.Serializable;
import java.util.Arrays;

public class DataSet implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double[][] features;
    private final double[] labels;
    private final String[] columnNames;
    private final int rowCount;
    private final int featureCount;

    public DataSet(double[][] features, double[] labels, String[] columnNames) {
        if (features == null || labels == null) {
            throw new IllegalArgumentException("Features and labels cannot be null");
        }
        if (features.length != labels.length) {
            throw new IllegalArgumentException("Features and labels length mismatch");
        }
        this.features = features;
        this.labels = labels;
        this.columnNames = columnNames != null ? columnNames : new String[0];
        this.rowCount = features.length;
        this.featureCount = rowCount > 0 ? features[0].length : 0;
    }

    public void normalize() {
        if (rowCount == 0 || featureCount == 0) return;

        double[] min = new double[featureCount];
        double[] max = new double[featureCount];
        Arrays.fill(min, Double.MAX_VALUE);
        Arrays.fill(max, -Double.MAX_VALUE);

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < featureCount; j++) {
                if (features[i][j] < min[j]) min[j] = features[i][j];
                if (features[i][j] > max[j]) max[j] = features[i][j];
            }
        }

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < featureCount; j++) {
                if (max[j] - min[j] != 0) {
                    features[i][j] = (features[i][j] - min[j]) / (max[j] - min[j]);
                } else {
                    features[i][j] = 0.0;
                }
            }
        }
    }

    public DataSet[] trainTestSplit(double ratio) {
        int trainSize = (int) (rowCount * ratio);
        
        double[][] trainFeatures = Arrays.copyOfRange(features, 0, trainSize);
        double[] trainLabels = Arrays.copyOfRange(labels, 0, trainSize);
        
        double[][] testFeatures = Arrays.copyOfRange(features, trainSize, rowCount);
        double[] testLabels = Arrays.copyOfRange(labels, trainSize, rowCount);

        DataSet trainData = new DataSet(trainFeatures, trainLabels, columnNames);
        DataSet testData = new DataSet(testFeatures, testLabels, columnNames);

        return new DataSet[]{trainData, testData};
    }

    public DataSet preview(int n) {
        int size = Math.min(n, rowCount);
        double[][] previewFeatures = Arrays.copyOfRange(features, 0, size);
        double[] previewLabels = Arrays.copyOfRange(labels, 0, size);
        return new DataSet(previewFeatures, previewLabels, columnNames);
    }

    public double[][] getFeatures() {
        return features;
    }

    public double[] getLabels() {
        return labels;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getFeatureCount() {
        return featureCount;
    }
}
