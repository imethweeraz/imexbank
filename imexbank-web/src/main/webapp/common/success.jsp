<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Success - ImexBank</title>
    <jsp:include page="header.jsp"/>
</head>
<body>
<div class="container">
    <div class="alert alert-success mt-4" role="alert">
        <h4 class="alert-heading">Operation Successful!</h4>
        <p>
            <c:if test="${not empty requestScope.success}">
                <c:out value="${requestScope.success}"/>
            </c:if>
            <c:if test="${empty requestScope.success}">
                The operation was completed successfully.
            </c:if>
        </p>
        <hr>
        <p class="mb-0"><a href="${pageContext.request.contextPath}/dashboard.jsp">Go to Dashboard</a></p>
    </div>
</div>
<jsp:include page="footer.jsp"/>
</body>
</html>