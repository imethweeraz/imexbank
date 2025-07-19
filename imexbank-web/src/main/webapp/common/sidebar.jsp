<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="bg-light border-end" id="sidebar-wrapper">
    <div class="sidebar-heading border-bottom bg-light">
        <img src="${pageContext.request.contextPath}/images/logo.png" height="30" class="me-2"/>ImexBank
    </div>
    <div class="list-group list-group-flush">
        <a class="list-group-item list-group-item-action list-group-item-light p-3" href="<c:url value='/dashboard.jsp'/>">Dashboard</a>
        <a class="list-group-item list-group-item-action list-group-item-light p-3" href="<c:url value='/account/'/>">Accounts</a>
        <a class="list-group-item list-group-item-action list-group-item-light p-3" href="<c:url value='/transaction/'/>">Transactions</a>
        <a class="list-group-item list-group-item-action list-group-item-light p-3" href="<c:url value='/customer/'/>">Customers</a>
        <a class="list-group-item list-group-item-action list-group-item-light p-3" href="<c:url value='/report/'/>">Reports</a>
        <a class="list-group-item list-group-item-action list-group-item-light p-3" href="<c:url value='/admin/'/>">Admin</a>
    </div>
</div>