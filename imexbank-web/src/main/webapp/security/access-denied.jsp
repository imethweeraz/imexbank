<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Access Denied" scope="request"/>
<%@ include file="../common/header.jsp" %>

<div class="container text-center mt-5">
    <h1 class="display-4 text-warning">Access Denied</h1>
    <p class="lead">You do not have the necessary permissions to view this page.</p>
    <hr>
    <p>If you believe this is an error, please contact the system administrator.</p>
    <a href="<c:url value='/dashboard.jsp'/>" class="btn btn-primary">Return to Dashboard</a>
</div>

<%@ include file="../common/footer.jsp" %>