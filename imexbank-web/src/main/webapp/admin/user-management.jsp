<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>User Management - ImexBank</title>
    <jsp:include page="../common/header.jsp"/>
</head>
<body>
<div class="d-flex" id="wrapper">
    <jsp:include page="../common/sidebar.jsp"/>
    <div id="page-content-wrapper">
        <jsp:include page="../common/navigation.jsp"/>
        <div class="container-fluid">
            <h1 class="mt-4">User Management</h1>
            <!-- User listing and management forms (lock/unlock/change role) would go here -->
            <p>This page would contain a list of system users with options to lock, unlock, or assign roles. Forms would post to the AdminServlet.</p>
        </div>
    </div>
</div>
<jsp:include page="../common/footer.jsp"/>
</body>
</html>