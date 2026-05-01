package com.mlstudio.servlet;

import com.mlstudio.dao.DatasetDAO;
import com.mlstudio.dataset.DataSet;
import com.mlstudio.util.CSVParser;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;

@WebServlet("/upload")
@MultipartConfig(maxFileSize = 10 * 1024 * 1024)
public class UploadServlet extends HttpServlet {
    private DatasetDAO dao;

    @Override
    public void init() {
        this.dao = new DatasetDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Part filePart = request.getPart("csvFile");
            if (filePart == null || filePart.getSubmittedFileName() == null || !filePart.getSubmittedFileName().toLowerCase().endsWith(".csv")) {
                throw new IllegalArgumentException("Please upload a valid .csv file.");
            }

            DataSet dataSet = CSVParser.parse(filePart.getInputStream());
            
            HttpSession session = request.getSession(true);
            String sessionId = session.getId();
            
            dao.deleteBySession(sessionId);
            dao.save(sessionId, dataSet);

            String[] colNames = dataSet.getColumnNames();
            if (colNames == null) {
                colNames = new String[0];
            }
            session.setAttribute("columnNames", colNames);
            session.setAttribute("datasetFeatures", dataSet.getFeatureCount());
            session.setAttribute("datasetRows", dataSet.getRowCount());
            session.setAttribute("trainingStatus", "IDLE");

            response.sendRedirect(request.getContextPath() + "/train");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage() != null ? e.getMessage() : "Error uploading file");
            request.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(request, response);
        }
    }
}
