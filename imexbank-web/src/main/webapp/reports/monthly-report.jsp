<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Monthly Report - ImexBank</title>
    <jsp:include page="../common/header.jsp"/>
</head>
<body>
<div class="d-flex" id="wrapper">
    <jsp:include page="../common/sidebar.jsp"/>
    <div id="page-content-wrapper">
        <jsp:include page="../common/navigation.jsp"/>
        <div class="container-fluid">
            <h1 class="mt-4">Monthly Account Summary for ${month}/${year}</h1>
            <div class="card">
                <div class="card-body">
                    <p><strong>Total Active Accounts:</strong> ${reportData.totalAccounts}</p>
                    <p><strong>Total Savings Balance:</strong> <fmt:formatNumber value="${reportData.totalSavingsBalance}" type="currency" currencySymbol="$"/></p>
                    <p><strong>Total Current Balance:</strong> <fmt:formatNumber value="${reportData.totalCurrentBalance}" type="currency" currencySymbol="$"/></p>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="../common/footer.jsp"/>
</body>
</html>