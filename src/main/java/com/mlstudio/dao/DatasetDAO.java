package com.mlstudio.dao;

import com.mlstudio.dataset.DataSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatasetDAO {

    public void save(String sessionId, DataSet dataSet) throws SQLException {
        String sql = "INSERT INTO dataset_rows (session_id, features, label) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < dataSet.getRowCount(); i++) {
                    stmt.setString(1, sessionId);
                    stmt.setString(2, arrayToString(dataSet.getFeatures()[i]));
                    stmt.setDouble(3, dataSet.getLabels()[i]);
                    stmt.addBatch();
                }
                stmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public DataSet load(String sessionId) throws SQLException {
        String sql = "SELECT features, label FROM dataset_rows WHERE session_id = ? ORDER BY id";
        List<double[]> featuresList = new ArrayList<>();
        List<Double> labelsList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    featuresList.add(stringToArray(rs.getString("features")));
                    labelsList.add(rs.getDouble("label"));
                }
            }
        }

        if (featuresList.isEmpty()) {
            return null;
        }

        double[][] features = featuresList.toArray(new double[0][]);
        double[] labels = new double[labelsList.size()];
        for (int i = 0; i < labelsList.size(); i++) {
            labels[i] = labelsList.get(i);
        }

        return new DataSet(features, labels, new String[0]);
    }

    public void deleteBySession(String sessionId) throws SQLException {
        String sql = "DELETE FROM dataset_rows WHERE session_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.executeUpdate();
        }
    }

    private String arrayToString(double[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private double[] stringToArray(String str) {
        String[] parts = str.split(",");
        double[] arr = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            arr[i] = Double.parseDouble(parts[i]);
        }
        return arr;
    }
}
