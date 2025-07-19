<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- This page lists all configured scheduled tasks from the database. --%>
<!DOCTYPE html>
<html>
<head><title>Scheduled Tasks - ImexBank</title><jsp:include page="../common/header.jsp"/></head>
<body>
<div class="d-flex" id="wrapper">
    <jsp:include page="../common/sidebar.jsp"/>
    <div id="page-content-wrapper">
        <jsp:include page="../common/navigation.jsp"/>
        <div class="container-fluid">
            <h1 class="mt-4">Configured Scheduled Tasks</h1>
            <%-- A table listing all tasks from the ScheduledTask entity would be here. --%>
        </div>
    </div>
</div>
<jsp:include page="../common/footer.jsp"/>
</body>
</html>