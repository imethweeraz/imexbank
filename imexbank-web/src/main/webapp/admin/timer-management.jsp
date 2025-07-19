<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Timer Management - ImexBank</title>
    <jsp:include page="../common/header.jsp"/>
</head>
<body>
<div class="d-flex" id="wrapper">
    <jsp:include page="../common/sidebar.jsp"/>
    <div id="page-content-wrapper">
        <jsp:include page="../common/navigation.jsp"/>
        <div class="container-fluid">
            <h1 class="mt-4">Timer Service Management</h1>
            <p>This page provides an overview of the scheduled tasks running in the system.</p>

            <div class="card">
                <div class="card-header">System Timers</div>
                <div class="card-body">
                    <table class="table">
                        <thead>
                        <tr>
                            <th>Timer Name</th>
                            <th>Status</th>
                            <th>Next Execution</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <%-- Data would be populated from a servlet call to TimerService --%>
                        <tr>
                            <td>ScheduledFundTransferTimer</td>
                            <td><span class="badge bg-success">Active</span></td>
                            <td>_Placeholder_Next_Time_</td>
                            <td>
                                <form action="${pageContext.request.contextPath}/admin/timers/pause" method="post" class="d-inline">
                                    <input type="hidden" name="timerName" value="ScheduledFundTransferTimer">
                                    <button type="submit" class="btn btn-sm btn-warning">Pause</button>
                                </form>
                            </td>
                        </tr>
                        <tr>
                            <td>InterestCalculationTimer</td>
                            <td><span class="badge bg-success">Active</span></td>
                            <td>_Placeholder_Next_Time_</td>
                            <td>
                                <form action="${pageContext.request.contextPath}/admin/timers/execute" method="post" class="d-inline">
                                    <input type="hidden" name="timerName" value="InterestCalculationTimer">
                                    <button type="submit" class="btn btn-sm btn-info">Execute Now</button>
                                </form>
                            </td>
                        </tr>
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