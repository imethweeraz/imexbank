<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Dashboard - ImexBank</title>
    <jsp:include page="/common/header.jsp"/>
</head>
<body>
<div class="d-flex" id="wrapper">
    <jsp:include page="/common/sidebar.jsp"/>
    <div id="page-content-wrapper">
        <jsp:include page="/common/navigation.jsp"/>
        <div class="container-fluid">
            <h1 class="mt-4">Dashboard</h1>
            <p>Welcome, <c:out value="${sessionScope.currentUser}" />!</p>

            <div class="row">
                <div class="col-md-4">
                    <div class="card text-white bg-primary mb-3">
                        <div class="card-body">
                            <h5 class="card-title">Total Accounts</h5>
                            <p class="card-text">5</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card text-white bg-success mb-3">
                        <div class="card-body">
                            <h5 class="card-title">Total Balance</h5>
                            <p class="card-text">\$1,234,567.89</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card text-white bg-info mb-3">
                        <div class="card-body">
                            <h5 class="card-title">Recent Transactions</h5>
                            <p class="card-text">15</p>
                        </div>
                    </div>
                </div>
            </div>

            <div class="card mt-4">
                <div class="card-header">
                    Quick Actions
                </div>
                <div class="card-body">
                    <a href="<c:url value='/transaction/transfer'/>" class="btn btn-primary">Fund Transfer</a>
                    <a href="<c:url value='/account/view/1'/>" class="btn btn-secondary">View Account</a>
                    <a href="<c:url value='/report/daily'/>" class="btn btn-info">View Reports</a>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>