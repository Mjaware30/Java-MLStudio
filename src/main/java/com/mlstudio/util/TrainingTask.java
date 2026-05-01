package com.mlstudio.util;

import com.mlstudio.dao.ModelResultDAO;
import com.mlstudio.dataset.DataSet;
import com.mlstudio.model.LinearRegression;
import com.mlstudio.model.MLModel;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TrainingTask implements Runnable {
    private final MLModel model;
    private final DataSet dataSet;
    private final String sessionId;
    private final AtomicInteger progress;
    private final ModelResultDAO dao;

    private volatile String statusMessage = "RUNNING";
    private volatile String errorMessage = null;
    private volatile double finalAccuracy = 0.0;

    public TrainingTask(MLModel model, DataSet dataSet, String sessionId, AtomicInteger progress) {
        this.model = model;
        this.dataSet = dataSet;
        this.sessionId = sessionId;
        this.progress = progress;
        this.dao = new ModelResultDAO();
    }

    @Override
    public void run() {
        try {
            progress.set(10);
            
            // split FIRST then normalize each split independently to avoid data leakage
            DataSet[] splits = dataSet.trainTestSplit(0.8);
            DataSet trainData = splits[0];
            DataSet testData = splits[1];
            
            trainData.normalize();
            testData.normalize();
            
            progress.set(25);
            
            model.train(trainData);
            
            progress.set(75);
            
            finalAccuracy = model.evaluate(testData);
            
            progress.set(90);
            
            List<Double> lossHistory = null;
            if (model instanceof LinearRegression) {
                lossHistory = ((LinearRegression) model).getLossHistory();
            }
            dao.save(sessionId, model.getModelName(), finalAccuracy, lossHistory);
            
            progress.set(100);
            statusMessage = "DONE";
            
        } catch (Exception e) {
            statusMessage = "FAILED";
            errorMessage = e.getMessage() != null ? e.getMessage() : e.toString();
            e.printStackTrace();
        }
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public double getFinalAccuracy() {
        return finalAccuracy;
    }
}
