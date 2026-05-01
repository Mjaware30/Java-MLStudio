package com.mlstudio.servlet;

import com.mlstudio.util.TrainingTask;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

@WebServlet("/status")
public class StatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null) {
            out.write("{\"progress\":0,\"status\":\"IDLE\",\"accuracy\":0.0,\"error\":\"no session\"}");
            return;
        }

        AtomicInteger progressStr = (AtomicInteger) session.getAttribute("trainingProgress");
        TrainingTask task = (TrainingTask) session.getAttribute("trainingTask");

        if (progressStr == null || task == null) {
            out.write("{\"progress\":0,\"status\":\"IDLE\",\"accuracy\":0.0,\"error\":\"\"}");
            return;
        }

        int progress = progressStr.get();
        String status = task.getStatusMessage();
        double accuracy = task.getFinalAccuracy();
        String error = task.getErrorMessage();

        if (error == null) {
            error = "";
        } else {
            error = error.replace("\"", "'").replace("\n", " ").replace("\r", "");
        }
        
        if ("DONE".equals(status)) {
            session.setAttribute("trainingStatus", "DONE");
        }

        String json = String.format("{\"progress\":%d,\"status\":\"%s\",\"accuracy\":%.4f,\"error\":\"%s\"}", 
            progress, status, accuracy, error);
        
        out.write(json);
    }
}
