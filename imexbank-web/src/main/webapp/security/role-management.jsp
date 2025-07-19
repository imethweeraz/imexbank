<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- This page allows an admin to assign or revoke roles for a specific user. --%>
<!DOCTYPE html>
<html>
<head><title>Role Management - ImexBank</title><jsp:include page="../common/header.jsp"/></head>
<body>
<div class="d-flex" id="wrapper">
    <jsp:include page="../common/sidebar.jsp"/>
    <div id="page-content-wrapper">
        <jsp:include page="../common/navigation.jsp"/>
        <div class="container-fluid">
            <h1 class="mt-4">User Role Management</h1>
            <p>A form to select a user and assign/revoke roles like TELLER, MANAGER, etc., would be here.</p>
        </div>
    </div>
</div>
<jsp:include page="../common/footer.jsp"/>
</body>
</html>