CREATE DATABASE IF NOT EXISTS mlstudio;
USE mlstudio;

CREATE TABLE IF NOT EXISTS dataset_rows (
  id INT AUTO_INCREMENT PRIMARY KEY,
  session_id VARCHAR(64) NOT NULL,
  features TEXT NOT NULL,
  label DOUBLE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_session (session_id)
);

CREATE TABLE IF NOT EXISTS model_results (
  id INT AUTO_INCREMENT PRIMARY KEY,
  session_id VARCHAR(64) NOT NULL,
  model_name VARCHAR(128) NOT NULL,
  accuracy DOUBLE NOT NULL,
  loss_history TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_session (session_id)
);

CREATE TABLE IF NOT EXISTS predictions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  session_id VARCHAR(64) NOT NULL,
  model_name VARCHAR(128) NOT NULL,
  input_data TEXT NOT NULL,
  prediction DOUBLE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_session (session_id)
);
