<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- This page displays a report of all interest credited over a period. --%>
<!DOCTYPE html>
<html>
<head><title>Interest Report - ImexBank</title><jsp:include page="../common/header.jsp"/></head>
<body>
<div class="d-flex" id="wrapper">
    <jsp:include page="../common/sidebar.jsp"/>
    <div id="page-content-wrapper">
        <jsp:include page="../common/navigation.jsp"/>
        <div class="container-fluid">
            <h1 class="mt-4">Interest Report</h1>
            <p><strong>Total Interest Paid:</strong> ${reportData.totalInterestPaid}</p>
            <%-- Table listing all interest transactions would go here. --%>
        </div>
    </div>
</div>
<jsp:include page="../common/footer.jsp"/>
</body>
</html>