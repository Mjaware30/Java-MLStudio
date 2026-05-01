package com.mlstudio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelResultDAO {

    public void save(String sessionId, String modelName, double accuracy, List<Double> lossHistory) throws SQLException {
        String sql = "INSERT INTO model_results (session_id, model_name, accuracy, loss_history) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.setString(2, modelName);
            stmt.setDouble(3, accuracy);
            stmt.setString(4, lossHistory != null ? listToString(lossHistory) : null);
            stmt.executeUpdate();
        }
    }

    public Map<String, Object> loadLatest(String sessionId) throws SQLException {
        String sql = "SELECT model_name, accuracy, loss_history, created_at FROM model_results WHERE session_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("modelName", rs.getString("model_name"));
                    result.put("accuracy", rs.getDouble("accuracy"));
                    result.put("lossHistory", rs.getString("loss_history"));
                    result.put("createdAt", rs.getTimestamp("created_at"));
                    return result;
                }
            }
        }
        return null;
    }

    public List<Map<String, Object>> loadAll(String sessionId) throws SQLException {
        String sql = "SELECT model_name, accuracy, loss_history, created_at FROM model_results WHERE session_id = ? ORDER BY id DESC";
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("modelName", rs.getString("model_name"));
                    result.put("accuracy", rs.getDouble("accuracy"));
                    result.put("lossHistory", rs.getString("loss_history"));
                    result.put("createdAt", rs.getTimestamp("created_at"));
                    results.add(result);
                }
            }
        }
        return results;
    }

    private String listToString(List<Double> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(String.format("%.6f", list.get(i)));
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
