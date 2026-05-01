<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Java ML Studio - Upload Dataset</title>
    <style>
        body { font-family: 'Inter', sans-serif; background-color: #f4f7f6; margin: 0; padding: 20px; color: #333; }
        .container { max-width: 600px; margin: 100px auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        h1 { font-size: 24px; margin-bottom: 20px; color: #2c3e50; }
        .error { color: #e74c3c; background: #fadbd8; padding: 10px; border-radius: 4px; margin-bottom: 20px; }
        .form-group { margin-bottom: 20px; }
        input[type="file"] { display: block; width: 100%; margin-top: 10px; }
        .hint { font-size: 14px; color: #7f8c8d; margin-top: 5px; }
        button { background: #3498db; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; font-size: 16px; width: 100%; transition: background 0.3s; }
        button:hover { background: #2980b9; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Upload Dataset</h1>
        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>
        <form action="/java-ml-studio/upload" method="post" enctype="multipart/form-data">
            <div class="form-group">
                <label for="csvFile">Select CSV File:</label>
                <input type="file" id="csvFile" name="csvFile" accept=".csv" required>
                <div class="hint">Last column must be numeric label (0, 1, 2... for classification or any number for regression).</div>
            </div>
            <button type="submit">Upload and Continue</button>
        </form>
    </div>
</body>
</html>
