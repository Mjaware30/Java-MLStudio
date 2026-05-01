# Java ML Studio 🤖

A full-stack machine learning web application built entirely in **pure Java** — no Python, no Spring Boot, no AI APIs. Upload a CSV dataset, train classical ML algorithms, monitor live training progress, and make real-time predictions through a browser dashboard.

> Built as a Value Added Program project for Advanced Java Programming (Sem VI) at Sandip Institute of Technology and Research Centre, Department of AI & Data Science.

---

## 📸 Screenshots

| Upload Dataset | Select Algorithm | Live Training | Prediction Result |
|---|---|---|---|
| Upload CSV with last column as label | Choose KNN / Naive Bayes / Linear Regression | Real-time progress bar via AJAX | Instant prediction from trained model |

---

## ✨ Features

- **3 ML Algorithms from scratch** — KNN, Gaussian Naive Bayes, Linear Regression with Gradient Descent
- **Live training progress bar** — background Java thread + AJAX polling every second
- **CSV upload and parsing** — Java File I/O with BufferedReader, auto-detects columns
- **MySQL persistence** — dataset rows, model results, and predictions stored via JDBC
- **MVC architecture** — Jakarta Servlets as Controllers, JSP as Views, zero business logic in JSP
- **Real-time prediction** — enter feature values and get instant model output
- **Session-based isolation** — each user's upload and training is fully isolated

---

## 🧠 Algorithms Implemented

### K-Nearest Neighbours (KNN)
- Lazy learner — stores all training points, no computation at train time
- At prediction: computes Euclidean distance to every stored point, majority-votes among K nearest
- Uses explicit index-based distance array (not `List.indexOf` which fails on arrays)

### Gaussian Naive Bayes
- Computes class priors and per-feature Gaussian statistics (mean + variance) during training
- At prediction: computes log-probability under Gaussian distribution for each class
- Uses log-probabilities to avoid numerical underflow on multi-feature datasets

### Linear Regression (Gradient Descent)
- Initialises weights to zero, runs gradient descent for N epochs
- Computes MSE loss and gradients per epoch, updates weights by learning rate × gradient
- Stores loss history in `List<Double>` — visible as convergence curve on dashboard

---

## 🏗️ Architecture

```
Browser (JSP View)
      ↓ HTTP
Servlet (Controller)
      ↓ Java call
Service / MLModel (Business Logic)
      ↓ JDBC
MySQL Database
```

**Background training thread flow:**
```
TrainServlet → new Thread(TrainingTask) → train() → evaluate() → ModelResultDAO.save()
                                                ↑
StatusServlet ← AJAX poll every 1s ← AtomicInteger progress (0→100)
```

---

## 📁 Project Structure

```
JavaMLStudio/
├── src/main/java/com/mlstudio/
│   ├── model/
│   │   ├── MLModel.java              # Abstract base class
│   │   ├── KNNClassifier.java        # K-Nearest Neighbours
│   │   ├── NaiveBayes.java           # Gaussian Naive Bayes
│   │   ├── LinearRegression.java     # Gradient descent regression
│   │   └── ModelFactory.java         # Factory pattern
│   ├── dataset/
│   │   └── DataSet.java              # Feature matrix + label array
│   ├── util/
│   │   ├── CSVParser.java            # BufferedReader CSV ingestion
│   │   └── TrainingTask.java         # Runnable background training
│   ├── dao/
│   │   ├── DBConnection.java         # JDBC connection manager
│   │   ├── DatasetDAO.java           # Dataset persistence
│   │   └── ModelResultDAO.java       # Training results persistence
│   ├── exception/
│   │   ├── InvalidDatasetException.java
│   │   └── TrainingFailedException.java
│   └── servlet/
│       ├── UploadServlet.java        # POST /upload
│       ├── TrainServlet.java         # GET+POST /train
│       ├── StatusServlet.java        # GET /status (AJAX)
│       ├── PredictServlet.java       # POST /predict
│       └── DashboardServlet.java     # GET /dashboard
├── src/main/webapp/WEB-INF/
│   ├── jsp/
│   │   ├── index.jsp                 # Upload page
│   │   ├── train.jsp                 # Algorithm selection
│   │   ├── dashboard.jsp             # Live training + predict form
│   │   └── result.jsp                # Prediction result
│   └── web.xml                       # Deployment descriptor
├── src/test/java/com/mlstudio/
│   ├── Phase1Test.java               # Test all models standalone
│   └── Phase2Test.java               # Test CSV parsing standalone
├── db/
│   └── schema.sql                    # MySQL schema
└── pom.xml
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Web Framework | Jakarta Servlets 6.0 + JSP + JSTL 3.0 |
| Server | Apache Tomcat 10.1.x |
| Database | MySQL 8.x via JDBC |
| Build Tool | Maven 3.8+ |
| Language | Java 17+ |
| Frontend | Plain HTML + CSS + Vanilla JS (no frameworks) |

---

## ⚙️ Prerequisites

- Java JDK 17 or higher
- Apache Maven 3.8+
- Apache Tomcat 10.1.x — [download here](https://tomcat.apache.org/download-10.cgi)
- MySQL 8.x running locally
- VS Code with [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)

---

## 🚀 Setup & Run

### 1. Clone the repository

```bash
git clone https://github.com/your-username/java-ml-studio.git
cd java-ml-studio
```

### 2. Set up the database

```bash
mysql -u root -p < db/schema.sql
```

Or paste `db/schema.sql` into MySQL Workbench and execute.

### 3. Configure database credentials

Open `src/main/java/com/mlstudio/dao/DBConnection.java` and update:

```java
private static final String DB_URL   = "jdbc:mysql://localhost:3306/mlstudio?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String USER     = "root";
private static final String PASSWORD = "your_password_here";
```

### 4. Run Phase 1 test (no DB or Tomcat needed)

Open `src/test/java/com/mlstudio/Phase1Test.java` in VS Code and click **Run** above `main()`. You should see all three algorithms train and print predictions. Fix any issues before proceeding.

### 5. Build the WAR

```bash
mvn clean package
```

### 6. Deploy to Tomcat

**Windows (PowerShell):**
```powershell
Copy-Item -Force "target\java-ml-studio.war" "C:\apache-tomcat-10.1.54\webapps\"
& "C:\apache-tomcat-10.1.54\bin\startup.bat"
```

**Mac / Linux:**
```bash
cp target/java-ml-studio.war /opt/tomcat/webapps/
/opt/tomcat/bin/startup.sh
```

### 7. Open the app

```
http://localhost:8080/java-ml-studio/upload
```

---

## 📊 Sample Dataset Format

The app expects CSV files where:
- First row = column headers
- All values = numeric
- Last column = label (class index for classification, numeric value for regression)

Example (Iris dataset format):
```csv
sepal_length,sepal_width,petal_length,petal_width,class
5.1,3.5,1.4,0.2,0
4.9,3.0,1.4,0.2,0
7.0,3.2,4.7,1.4,1
```

Download sample datasets:
- [Iris Dataset](https://archive.ics.uci.edu/ml/datasets/iris) — 150 rows, 4 features, 3 classes (KNN / Naive Bayes)
- [Pima Indians Diabetes](https://www.kaggle.com/datasets/uciml/pima-indians-diabetes-database) — 768 rows, 8 features, 2 classes

---

## 🗄️ Database Schema

```sql
-- Stores uploaded CSV rows per user session
dataset_rows (id, session_id, features TEXT, label DOUBLE, created_at)

-- Stores accuracy and loss history after each training run
model_results (id, session_id, model_name, accuracy DOUBLE, loss_history TEXT, created_at)

-- Stores each prediction made by the user
predictions (id, session_id, model_name, input_data TEXT, prediction DOUBLE, created_at)
```

---

## 📐 Course Module Mapping

| Module | Topic | Used In |
|---|---|---|
| Module 1 | OOP & Core Java | `MLModel` abstract class, algorithm subclasses, `ModelFactory` |
| Module 2 | Exception Handling & Multithreading | `TrainingTask` Runnable, `AtomicInteger`, custom exceptions |
| Module 3 | Collections & Generics | Streams in KNN, `HashMap` in Naive Bayes, `List<Double>` loss history |
| Module 4 | JDBC & File I/O | `DatasetDAO`, `ModelResultDAO`, `CSVParser` with `BufferedReader` |
| Module 5 | Servlets, JSP & MVC | 5 Servlets as Controllers, 4 JSP pages as Views, strict MVC |

---

## ⚠️ Common Issues & Fixes

**App deploys but 404 on all pages**
→ Check `web.xml` has `metadata-complete="false"` (lowercase). If `true`, Tomcat ignores all `@WebServlet` annotations.

**"MySQL JDBC Driver not found" on upload**
→ Make sure the MySQL dependency in `pom.xml` has NO `<scope>provided</scope>` tag — it must be compiled into the WAR.

**"Error deploying web application archive"**
→ Check `catalina.log` for duplicate servlet mapping. Each servlet must be declared via `@WebServlet` annotation only — no servlet entries in `web.xml`.

**KNN shows 0% accuracy**
→ Normalization must happen AFTER the train/test split, independently on each split. Normalizing before splitting causes data leakage.

**"Not supported yet" or "Unimplemented method"**
→ An auto-generated stub exists in one of your DAO files. Delete and recreate the file — VS Code sometimes inserts `throw new UnsupportedOperationException()` stubs.

**Training starts but dashboard shows "Failed: No dataset found"**
→ The TrainingTask is trying to reload the dataset from DB by session ID but the session changed. Pass the `DataSet` object directly to `TrainingTask` constructor instead of reloading inside the thread.

---

## 👥 Team

| Role | Responsibility |
|---|---|
| Member 1 | Phase 1 — MLModel hierarchy, all algorithm implementations |
| Member 2 | Phase 3 — Training thread, exceptions, progress tracking |
| Member 3 | Phase 2 — CSV parser, DataSet normalization and splitting |
| Member 4 | Phase 2 — JDBC DAOs, MySQL schema, DB connection |
| Member 5 | Phase 5 — Servlets, JSP pages, MVC wiring, AJAX polling |

---

## 📄 License

This project is developed for academic purposes at Sandip Institute of Technology and Research Centre under the guidance of Prof. Akhilesh Sharma (Coordinator) and Dr. Atmeshkumar Patel (HoD, AIDS).

---

## 🙏 Acknowledgements

- Department of Artificial Intelligence & Data Science, SITRC
- Advanced Java Programming Value Added Program, 2025-26
- Apache Tomcat, MySQL, and the Jakarta EE community
