<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Deposit - ImexBank</title>
    <jsp:include page="../common/header.jsp"/>
</head>
<body>
<div class="d-flex" id="wrapper">
    <jsp:include page="../common/sidebar.jsp"/>
    <div id="page-content-wrapper">
        <jsp:include page="../common/navigation.jsp"/>
        <div class="container-fluid">
            <h1 class="mt-4">Deposit Funds</h1>
            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger">${requestScope.error}</div>
            </c:if>
            <form action="${pageContext.request.contextPath}/account/deposit" method="post" class="needs-validation" novalidate>
                <div class="card">
                    <div class="card-body">
                        <div class="mb-3">
                            <label for="accountNumber" class="form-label">Account Number</label>
                            <input type="text" class="form-control" id="accountNumber" name="accountNumber" required>
                        </div>
                        <div class="mb-3">
                            <label for="amount" class="form-label">Amount</label>
                            <input type="number" step="0.01" class="form-control" id="amount" name="amount" required>
                        </div>
                        <button type="submit" class="btn btn-success">Deposit</button>
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