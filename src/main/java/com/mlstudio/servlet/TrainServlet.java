package com.mlstudio.servlet;

import com.mlstudio.dao.DatasetDAO;
import com.mlstudio.dataset.DataSet;
import com.mlstudio.model.MLModel;
import com.mlstudio.model.ModelFactory;
import com.mlstudio.util.TrainingTask;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@WebServlet("/train")
public class TrainServlet extends HttpServlet {
    private DatasetDAO dao;

    @Override
    public void init() {
        this.dao = new DatasetDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/train.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(true);
            String sessionId = session.getId();

            String modelType = request.getParameter("modelType");
            if (modelType == null || modelType.trim().isEmpty()) {
                modelType = "knn";
            }

            String kOrEpochsStr = request.getParameter("kOrEpochs");
            int kOrEpochs = 3;
            if (kOrEpochsStr != null && !kOrEpochsStr.trim().isEmpty()) {
                kOrEpochs = Integer.parseInt(kOrEpochsStr.trim());
            }

            String lrStr = request.getParameter("learningRate");
            double learningRate = 0.01;
            if (lrStr != null && !lrStr.trim().isEmpty()) {
                learningRate = Double.parseDouble(lrStr.trim());
            }

            DataSet dataSet = dao.load(sessionId);
            if (dataSet == null) {
                throw new IllegalStateException("Dataset not found. Please upload again.");
            }

            MLModel model = ModelFactory.create(modelType, kOrEpochs, learningRate);

            session.setAttribute("trainedModel", model);
            session.setAttribute("currentModel", model.getModelName());
            session.setAttribute("trainingStatus", "RUNNING");

            AtomicInteger progress = new AtomicInteger(0);
            session.setAttribute("trainingProgress", progress);

            TrainingTask task = new TrainingTask(model, dataSet, sessionId, progress);
            session.setAttribute("trainingTask", task);

            Thread taskThread = new Thread(task);
            taskThread.setDaemon(true);
            taskThread.start();

            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage() != null ? e.getMessage() : "Error starting training");
            request.getRequestDispatcher("/WEB-INF/jsp/train.jsp").forward(request, response);
        }
    }
}
