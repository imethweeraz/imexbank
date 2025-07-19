<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create Account - ImexBank</title>
    <jsp:include page="../common/header.jsp"/>
</head>
<body>
<div class="d-flex" id="wrapper">
    <jsp:include page="../common/sidebar.jsp"/>
    <div id="page-content-wrapper">
        <jsp:include page="../common/navigation.jsp"/>
        <div class="container-fluid">
            <h1 class="mt-4">Create New Account</h1>
            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger">${requestScope.error}</div>
            </c:if>
            <form action="${pageContext.request.contextPath}/account/create" method="post" class="needs-validation" novalidate>
                <div class="card">
                    <div class="card-body">
                        <div class="mb-3">
                            <label for="customerId" class="form-label">Customer ID</label>
                            <input type="number" class="form-control" id="customerId" name="customerId" required>
                        </div>
                        <div class="mb-3">
                            <label for="accountType" class="form-label">Account Type</label>
                            <select class="form-select" id="accountType" name="accountType" required>
                                <c:forEach items="${requestScope.accountTypes}" var="type">
                                    <option value="${type}">${type.displayName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="initialDeposit" class="form-label">Initial Deposit</label>
                            <input type="number" step="0.01" class="form-control" id="initialDeposit" name="initialDeposit">
                        </div>
                        <button type="submit" class="btn btn-success">Create Account</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<jsp:include page="../common/footer.jsp"/>
<script src="${pageContext.request.contextPath}/js/form-validation.js"></script>
</body>
</html>