<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Daily Report - ImexBank</title>
    <jsp:include page="../common/header.jsp"/>
</head>
<body>
<div class="d-flex" id="wrapper">
    <jsp:include page="../common/sidebar.jsp"/>
    <div id="page-content-wrapper">
        <jsp:include page="../common/navigation.jsp"/>
        <div class="container-fluid">
            <h1 class="mt-4">Daily Transaction Report for <fmt:formatDate value="${reportDate}" type="date" dateStyle="long"/></h1>

            <div class="card mb-4">
                <div class="card-header">Summary</div>
                <div class="card-body">
                    <p><strong>Total Transactions:</strong> <c:out value="${reportData.transactionCount}"/></p>
                    <p><strong>Total Amount Transacted:</strong> <fmt:formatNumber value="${reportData.totalAmount}" type="currency" currencySymbol="$"/></p>
                </div>
            </div>

            <div class="card">
                <div class="card-header">Transactions</div>
                <div class="card-body">
                    <table class="table table-bordered">
                        <thead>
                        <tr>
                            <th>Reference</th>
                            <th>Type</th>
                            <th>Amount</th>
                            <th>Status</th>
                            <th>Date</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${reportData.transactions}" var="tx">
                            <tr>
                                <td>${tx.referenceNumber}</td>
                                <td>${tx.transactionType.displayName}</td>
                                <td><fmt:formatNumber value="${tx.amount}" type="currency" currencySymbol="$"/></td>
                                <td><span class="badge bg-info">${tx.status.displayName}</span></td>
                                <td><fmt:formatDate value="${tx.transactionDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="../common/footer.jsp"/>
</body>
</html>