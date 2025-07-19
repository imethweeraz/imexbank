<%@ page isErrorPage="true" contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Error - ImexBank</title>
    <jsp:include page="/common/header.jsp"/>
</head>
<body>
<div class="container mt-5">
    <div class="alert alert-danger">
        <h2>An Error Occurred</h2>
        <p>We're sorry, but an unexpected error has occurred. Please try again later.</p>
        <hr>
        <% if (request.getAttribute("error") != null) { %>
        <p><strong>Details:</strong> <%= request.getAttribute("error") %></p>
        <% } %>
        <% if (exception != null) { %>
        <p><strong>Exception:</strong> <%= exception.getMessage() %></p>
        <% } %>
    </div>
    <a href="${pageContext.request.contextPath}/dashboard.jsp" class="btn btn-primary">Go to Dashboard</a>
</div>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>