package com.mlstudio.util;

import com.mlstudio.dataset.DataSet;
import com.mlstudio.exception.InvalidDatasetException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {

    public static DataSet parse(InputStream is) throws InvalidDatasetException, IOException {
        File tempFile = File.createTempFile("dataset_", ".csv");
        try {
            Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return parse(tempFile);
        } finally {
            tempFile.delete();
        }
    }

    public static DataSet parse(File file) throws InvalidDatasetException, IOException {
        List<double[]> featuresList = new ArrayList<>();
        List<Double> labelsList = new ArrayList<>();
        String[] columnNames = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 2) {
                    throw new InvalidDatasetException("Dataset must have at least one feature column and one label column");
                }

                if (isFirstLine) {
                    columnNames = new String[parts.length - 1];
                    for (int i = 0; i < parts.length - 1; i++) {
                        columnNames[i] = parts[i].trim();
                    }
                    isFirstLine = false;
                } else {
                    double[] features = new double[parts.length - 1];
                    try {
                        for (int i = 0; i < parts.length - 1; i++) {
                            features[i] = Double.parseDouble(parts[i].trim());
                        }
                        featuresList.add(features);
                        labelsList.add(Double.parseDouble(parts[parts.length - 1].trim()));
                    } catch (NumberFormatException e) {
                        throw new InvalidDatasetException("All features and labels must be numeric.", e);
                    }
                }
            }
        }

        if (featuresList.isEmpty()) {
            throw new InvalidDatasetException("File is empty or contains no valid data rows");
        }

        double[][] features = featuresList.toArray(new double[0][]);
        double[] labels = new double[labelsList.size()];
        for (int i = 0; i < labelsList.size(); i++) {
            labels[i] = labelsList.get(i);
        }

        return new DataSet(features, labels, columnNames);
    }
}
