package com.mlstudio.servlet;

import com.mlstudio.model.MLModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/predict")
public class PredictServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/upload");
            return;
        }

        MLModel model = (MLModel) session.getAttribute("trainedModel");
        if (model == null || !model.isTrained()) {
            response.sendRedirect(request.getContextPath() + "/train");
            return;
        }

        int featureCount = 0;
        try {
            Object dfObj = session.getAttribute("datasetFeatures");
            if (dfObj != null) {
                featureCount = Integer.parseInt(dfObj.toString());
            }
        } catch (Exception e) {
            featureCount = 0;
        }

        String[] columnNames = new String[featureCount];
        Object colObj = session.getAttribute("columnNames");
        if (colObj instanceof String[]) {
            String[] sArr = (String[]) colObj;
            for (int i = 0; i < featureCount && i < sArr.length; i++) {
                columnNames[i] = sArr[i];
            }
            for (int i = sArr.length; i < featureCount; i++) {
                columnNames[i] = "Feature " + i;
            }
        } else if (colObj instanceof String) {
            String[] sArr = colObj.toString().split(",");
            for (int i = 0; i < featureCount && i < sArr.length; i++) {
                columnNames[i] = sArr[i];
            }
            for (int i = sArr.length; i < featureCount; i++) {
                columnNames[i] = "Feature " + i;
            }
        } else {
            for (int i = 0; i < featureCount; i++) {
                columnNames[i] = "Feature " + i;
            }
        }

        double[] input = new double[featureCount];
        double[] inputValues = new double[featureCount];
        try {
            for (int i = 0; i < featureCount; i++) {
                String valStr = request.getParameter("feature_" + i);
                if (valStr == null || valStr.trim().isEmpty()) {
                    throw new IllegalArgumentException("Missing parameter feature_" + i);
                }
                input[i] = Double.parseDouble(valStr.trim());
                inputValues[i] = input[i];
            }
        } catch (Exception e) {
            request.setAttribute("error", "Invalid input values: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(request, response);
            return;
        }

        double prediction = model.predict(input);

        request.setAttribute("prediction", prediction);
        request.setAttribute("inputValues", inputValues);
        request.setAttribute("columnNames", columnNames);
        request.setAttribute("modelName", model.getModelName());

        request.getRequestDispatcher("/WEB-INF/jsp/result.jsp").forward(request, response);
    }
}
