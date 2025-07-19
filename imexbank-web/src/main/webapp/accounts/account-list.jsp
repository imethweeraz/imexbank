<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.imeth.imexbank.web.utils.JSPHelper" %>
<c:set var="pageTitle" value="My Accounts" scope="request"/>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>

<div id="page-content-wrapper">
    <%@ include file="../common/navigation.jsp" %>
    <div class="container-fluid">
        <h1 class="mt-4">Customer Accounts</h1>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <div class="card">
            <div class="card-header">Your Accounts</div>
            <div class="card-body">
                <table class="table table-hover">
                    <thead>
                    <tr>
                        <th>Account Number</th>
                        <th>Account Type</th>
                        <th>Balance</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="account" items="${accounts}">
                        <tr>
                            <td>${account.accountNumber}</td>
                            <td>${account.accountType.displayName}</td>
                            <td><%= JSPHelper.formatCurrency( (java.math.BigDecimal) pageContext.getAttribute("account.balance") ) %></td>
                            <td><span class="badge ${account.isActive ? 'bg-success' : 'bg-danger'}">${account.isActive ? 'Active' : 'Inactive'}</span></td>
                            <td>
                                <a href="<c:url value='/account/view/${account.id}'/>" class="btn btn-sm btn-info">View Details</a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>