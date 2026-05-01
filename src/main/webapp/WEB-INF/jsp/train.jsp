<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Java ML Studio - Train Model</title>
    <style>
        body { font-family: 'Inter', sans-serif; background-color: #f4f7f6; margin: 0; padding: 20px; color: #333; }
        .container { max-width: 800px; margin: 50px auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        h1 { font-size: 24px; margin-bottom: 20px; color: #2c3e50; }
        .info-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 30px; }
        .info-box { background: #ecf0f1; padding: 20px; border-radius: 8px; text-align: center; }
        .info-box h3 { margin: 0 0 10px 0; font-size: 16px; color: #7f8c8d; }
        .info-box p { margin: 0; font-size: 24px; font-weight: bold; color: #2c3e50; }
        .form-group { margin-bottom: 20px; }
        label { display: block; margin-bottom: 5px; font-weight: 500; }
        select, input[type="number"] { width: 100%; padding: 10px; border: 1px solid #bdc3c7; border-radius: 4px; box-sizing: border-box; }
        button { background: #27ae60; color: white; border: none; padding: 12px 20px; border-radius: 4px; cursor: pointer; font-size: 16px; width: 100%; transition: background 0.3s; }
        button:hover { background: #2ecc71; }
        .error { color: #e74c3c; background: #fadbd8; padding: 10px; border-radius: 4px; margin-bottom: 20px; }
        .hidden { display: none; }
    </style>
    <script>
        function updateParams() {
            var modelType = document.getElementById("modelType").value;
            var lrGroup = document.getElementById("lrGroup");
            var kOrEpochsLabel = document.getElementById("kOrEpochsLabel");
            var kOrEpochs = document.getElementById("kOrEpochs");
            var lrHidden = document.getElementById("learningRateHidden");

            if (modelType === "knn") {
                lrGroup.classList.add("hidden");
                kOrEpochsLabel.innerText = "K (Neighbors)";
                kOrEpochs.value = "3";
                lrHidden.value = "0.01";
            } else if (modelType === "linearregression") {
                lrGroup.classList.remove("hidden");
                kOrEpochsLabel.innerText = "Epochs";
                kOrEpochs.value = "500";
            } else if (modelType === "naivebayes") {
                lrGroup.classList.add("hidden");
                kOrEpochsLabel.innerText = "Ignored (K=1)";
                kOrEpochs.value = "1";
                lrHidden.value = "0.01";
            }
        }

        function syncLr() {
            document.getElementById("learningRateHidden").value = document.getElementById("learningRate").value;
        }
    </script>
</head>
<body onload="updateParams()">
    <div class="container">
        <h1>Train Model</h1>
        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>
        
        <div class="info-grid">
            <div class="info-box">
                <h3>Rows</h3>
                <p><c:out value="${sessionScope.datasetRows}"/></p>
            </div>
            <div class="info-box">
                <h3>Features</h3>
                <p><c:out value="${sessionScope.datasetFeatures}"/></p>
            </div>
            <div class="info-box">
                <h3>Split</h3>
                <p>80 / 20 split</p>
            </div>
        </div>

        <form action="/java-ml-studio/train" method="post">
            <div class="form-group">
                <label for="modelType">Algorithm</label>
                <select id="modelType" name="modelType" onchange="updateParams()">
                    <option value="knn">KNN</option>
                    <option value="linearregression">Linear Regression</option>
                    <option value="naivebayes">Naive Bayes</option>
                </select>
            </div>
            <div class="form-group">
                <label id="kOrEpochsLabel" for="kOrEpochs">K (Neighbors)</label>
                <input type="number" id="kOrEpochs" name="kOrEpochs" value="3" required>
            </div>
            <div class="form-group hidden" id="lrGroup">
                <label for="learningRate">Learning Rate</label>
                <input type="number" step="0.0001" id="learningRate" oninput="syncLr()" value="0.01">
            </div>
            <input type="hidden" id="learningRateHidden" name="learningRate" value="0.01">
            
            <button type="submit">Start Training</button>
        </form>
    </div>
</body>
</html>
