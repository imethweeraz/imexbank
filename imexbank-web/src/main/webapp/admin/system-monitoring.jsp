<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- This page would display system health metrics (e.g., CPU, memory, performance logs). --%>
<!DOCTYPE html>
<html>
<head><title>System Monitoring - ImexBank</title><jsp:include page="../common/header.jsp"/></head>
<body>
<div class="d-flex" id="wrapper">
    <jsp:include page="../common/sidebar.jsp"/>
    <div id="page-content-wrapper">
        <jsp:include page="../common/navigation.jsp"/>
        <div class="container-fluid">
            <h1 class="mt-4">System Performance Monitoring</h1>
            <p>This page would contain real-time graphs and statistics on system health, fed by the PerformanceInterceptor and other monitoring tools.</p>
        </div>
    </div>
</div>
<jsp:include page="../common/footer.jsp"/>
</body>
</html>