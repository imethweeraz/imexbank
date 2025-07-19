<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="com.imeth.imexbank.web.utils.JSPHelper" %>
<c:set var="pageTitle" value="Transaction History" scope="request"/>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>

<div id="page-content-wrapper">
    <%@ include file="../common/navigation.jsp" %>
    <div class="container-fluid">
        <h1 class="mt-4">Transaction History</h1>

        <div class="card">
            <div class="card-body">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th>Date</th>
                        <th>Reference</th>
                        <th>Description</th>
                        <th>Type</th>
                        <th>Amount</th>
                        <th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="tx" items="${transactions}">
                        <tr>
                            <td><fmt:formatDate value="${tx.transactionDate}" pattern="yyyy-MM-dd HH:mm"/></td>
                            <td>${tx.referenceNumber}</td>
                            <td>${tx.description}</td>
                            <td>${tx.transactionType.displayName}</td>
                            <td><%= JSPHelper.formatCurrency((java.math.BigDecimal) pageContext.getAttribute("tx.amount")) %></td>
                            <td><span class="badge bg-info text-dark">${tx.status.displayName}</span></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>