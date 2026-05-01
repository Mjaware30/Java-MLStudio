<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Java ML Studio - Prediction Result</title>
    <style>
        body { font-family: 'Inter', sans-serif; background-color: #f4f7f6; margin: 0; padding: 20px; color: #333; }
        .container { max-width: 800px; margin: 50px auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        h1 { font-size: 24px; margin-bottom: 20px; color: #2c3e50; }
        .result-card { background: #34495e; color: white; padding: 30px; border-radius: 8px; text-align: center; margin-bottom: 30px; }
        .result-card h2 { margin: 0 0 10px 0; font-weight: 300; }
        .result-card .value { font-size: 48px; font-weight: bold; color: #f1c40f; margin: 0; }
        table { width: 100%; border-collapse: collapse; margin-bottom: 30px; }
        th, td { padding: 12px 15px; border-bottom: 1px solid #ecf0f1; text-align: left; }
        th { background: #f8f9f9; color: #7f8c8d; font-weight: 500; }
        .btn-group { display: flex; gap: 15px; }
        .btn { flex: 1; text-align: center; padding: 12px; border-radius: 4px; text-decoration: none; font-weight: bold; transition: background 0.3s; color: white; }
        .btn-primary { background: #3498db; }
        .btn-primary:hover { background: #2980b9; }
        .btn-secondary { background: #95a5a6; }
        .btn-secondary:hover { background: #7f8c8d; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Prediction Result for ${modelName}</h1>
        
        <div class="result-card">
            <h2>Predicted Value</h2>
            <p class="value"><c:out value="${prediction}"/></p>
        </div>

        <h3>Input Features</h3>
        <table>
            <thead>
                <tr>
                    <th>Feature Name</th>
                    <th>Input Value</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${columnNames}" var="colName" varStatus="loop">
                    <tr>
                        <td><c:out value="${colName}"/></td>
                        <td><c:out value="${inputValues[loop.index]}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <div class="btn-group">
            <a href="/java-ml-studio/dashboard" class="btn btn-primary">Predict again</a>
            <a href="/java-ml-studio/train" class="btn btn-secondary">New model</a>
            <a href="/java-ml-studio/upload" class="btn btn-secondary">New dataset</a>
        </div>
    </div>
</body>
</html>
