<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Arrays" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Java ML Studio - Dashboard</title>
    <style>
        body { font-family: 'Inter', sans-serif; background-color: #f4f7f6; margin: 0; padding: 20px; color: #333; }
        .container { max-width: 800px; margin: 50px auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        h1 { font-size: 24px; margin-bottom: 20px; color: #2c3e50; }
        .status-container { margin-bottom: 30px; }
        .progress-bar-bg { width: 100%; background: #ecf0f1; border-radius: 8px; height: 30px; overflow: hidden; margin-top: 10px; }
        .progress-bar-fill { height: 100%; background: #3498db; width: 0%; transition: width 0.5s ease; text-align: center; color: white; line-height: 30px; }
        .hidden { display: none; }
        .metrics-card { background: #e8f8f5; padding: 20px; border-radius: 8px; border: 1px solid #1abc9c; margin-bottom: 30px; }
        .error-card { background: #fadbd8; padding: 20px; border-radius: 8px; border: 1px solid #e74c3c; margin-bottom: 30px; color: #c0392b; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: 500; }
        input[type="number"] { width: 100%; padding: 10px; border: 1px solid #bdc3c7; border-radius: 4px; box-sizing: border-box; }
        button { background: #9b59b6; color: white; border: none; padding: 12px 20px; border-radius: 4px; cursor: pointer; font-size: 16px; width: 100%; transition: background 0.3s; margin-top: 10px; }
        button:hover { background: #8e44ad; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Dashboard - <%= session.getAttribute("currentModel") %></h1>
        
        <div id="statusContainer" class="status-container">
            <h3 id="statusText">Training in progress...</h3>
            <div class="progress-bar-bg">
                <div id="progressBar" class="progress-bar-fill">0%</div>
            </div>
        </div>

        <div id="errorCard" class="error-card hidden">
            <h3>Training Failed</h3>
            <p id="errorMessage"></p>
        </div>

        <div id="metricsCard" class="metrics-card hidden">
            <h3>Training Complete</h3>
            <p><strong>Accuracy:</strong> <span id="accuracySpan">0.0</span>%</p>
            <p><strong>Test Rows:</strong> <%= (int)(Math.ceil(Double.parseDouble(session.getAttribute("datasetRows").toString()) * 0.2)) %></p>
        </div>

        <div id="predictFormContainer" class="hidden">
            <h3>Make a Prediction</h3>
            <form action="/java-ml-studio/predict" method="post">
                <%
                    int featureCount = 0;
                    try {
                        Object dfObj = session.getAttribute("datasetFeatures");
                        if (dfObj != null) {
                            featureCount = Integer.parseInt(dfObj.toString());
                        }
                    } catch (Exception e) {}

                    String[] columnNames = new String[featureCount];
                    Object colObj = session.getAttribute("columnNames");
                    if (colObj instanceof String[]) {
                        String[] sArr = (String[]) colObj;
                        for (int i = 0; i < featureCount && i < sArr.length; i++) columnNames[i] = sArr[i];
                        for (int i = sArr.length; i < featureCount; i++) columnNames[i] = "Feature " + i;
                    } else if (colObj instanceof String) {
                        String[] sArr = colObj.toString().split(",");
                        for (int i = 0; i < featureCount && i < sArr.length; i++) columnNames[i] = sArr[i];
                        for (int i = sArr.length; i < featureCount; i++) columnNames[i] = "Feature " + i;
                    } else {
                        for (int i = 0; i < featureCount; i++) columnNames[i] = "Feature " + i;
                    }

                    for (int i = 0; i < featureCount; i++) {
                %>
                <div class="form-group">
                    <label for="feature_<%= i %>"><%= columnNames[i] %></label>
                    <input type="number" step="any" id="feature_<%= i %>" name="feature_<%= i %>" required>
                </div>
                <%
                    }
                %>
                <button type="submit">Predict</button>
            </form>
        </div>
    </div>

    <script>
        let pollInterval;
        
        function pollStatus() {
            fetch('/java-ml-studio/status')
                .then(response => response.json())
                .then(data => {
                    let progress = data.progress;
                    document.getElementById('progressBar').style.width = progress + '%';
                    document.getElementById('progressBar').innerText = progress + '%';
                    document.getElementById('statusText').innerText = 'Status: ' + data.status;

                    if (data.status === 'DONE') {
                        clearInterval(pollInterval);
                        document.getElementById('statusContainer').classList.add('hidden');
                        document.getElementById('metricsCard').classList.remove('hidden');
                        document.getElementById('predictFormContainer').classList.remove('hidden');
                        document.getElementById('accuracySpan').innerText = data.accuracy.toFixed(2);
                    } else if (data.status === 'FAILED') {
                        clearInterval(pollInterval);
                        document.getElementById('statusContainer').classList.add('hidden');
                        document.getElementById('errorCard').classList.remove('hidden');
                        document.getElementById('errorMessage').innerText = data.error;
                    }
                })
                .catch(error => console.error('Error fetching status:', error));
        }

        pollInterval = setInterval(pollStatus, 1000);
    </script>
</body>
</html>
