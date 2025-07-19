<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Success - ImexBank</title>
    <jsp:include page="/common/header.jsp"/>
</head>
<body>
<div class="container mt-5">
    <div class="alert alert-success">
        <h2>Operation Successful</h2>
        <p><%= request.getAttribute("successMessage") != null ? request.getAttribute("successMessage") : "Your operation was completed successfully." %></p>
    </div>
    <a href="${pageContext.request.contextPath}/dashboard.jsp" class="btn btn-primary">Go to Dashboard</a>
</div>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>