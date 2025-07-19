<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Audit Report - ImexBank</title>
    <jsp:include page="../common/header.jsp"/>
</head>
<body>
<div class="d-flex" id="wrapper">
    <jsp:include page="../common/sidebar.jsp"/>
    <div id="page-content-wrapper">
        <jsp:include page="../common/navigation.jsp"/>
        <div class="container-fluid">
            <h1 class="mt-4">Audit Log Report</h1>
            <p>From: <fmt:formatDate value="${startDate}" type="date"/> To: <fmt:formatDate value="${endDate}" type="date"/></p>

            <div class="card">
                <div class="card-header">Audit Events</div>
                <div class="card-body">
                    <table class="table table-sm">
                        <thead>
                        <tr>
                            <th>Timestamp</th>
                            <th>User</th>
                            <th>Action</th>
                            <th>Entity</th>
                            <th>Details</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${reportData.auditLogs}" var="log">
                            <tr>
                                <td><fmt:formatDate value="${log.timestamp}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                <td>${log.username}</td>
                                <td>${log.action}</td>
                                <td>${log.entityName} #${log.entityId}</td>
                                <td><small>Old: ${log.oldValue}<br>New: ${log.newValue}</small></td>
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