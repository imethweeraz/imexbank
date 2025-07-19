<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Customer List - ImexBank</title>
    <jsp:include page="../common/header.jsp"/>
</head>
<body>
<div class="d-flex" id="wrapper">
    <jsp:include page="../common/sidebar.jsp"/>
    <div id="page-content-wrapper">
        <jsp:include page="../common/navigation.jsp"/>
        <div class="container-fluid">
            <h1 class="mt-4">Customers</h1>
            <div class="card">
                <div class="card-header">All Customers</div>
                <div class="card-body">
                    <table class="table table-striped">
                        <thead>
                        <tr>
                            <th>Customer Number</th>
                            <th>Full Name</th>
                            <th>Email</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${requestScope.customers}" var="customer">
                            <tr>
                                <td>${customer.customerNumber}</td>
                                <td>${customer.fullName}</td>
                                <td>${customer.email}</td>
                                <td><span class="badge ${customer.isActive ? 'bg-success' : 'bg-danger'}">${customer.isActive ? 'Active' : 'Inactive'}</span></td>
                                <td><a href="${pageContext.request.contextPath}/customer/view/${customer.id}" class="btn btn-sm btn-primary">Details</a></td>
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