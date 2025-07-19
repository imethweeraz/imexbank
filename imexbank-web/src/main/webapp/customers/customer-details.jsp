<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Customer Details - ImexBank</title>
    <jsp:include page="../common/header.jsp"/>
</head>
<body>
<div class="d-flex" id="wrapper">
    <jsp:include page="../common/sidebar.jsp"/>
    <div id="page-content-wrapper">
        <jsp:include page="../common/navigation.jsp"/>
        <div class="container-fluid">
            <h1 class="mt-4">Customer Details: ${customer.fullName}</h1>
            <div class="card">
                <div class="card-body">
                    <p><strong>Customer Number:</strong> ${customer.customerNumber}</p>
                    <p><strong>NIC Number:</strong> ${customer.nicNumber}</p>
                    <p><strong>Email:</strong> ${customer.email}</p>
                    <p><strong>Phone:</strong> ${customer.phoneNumber}</p>
                    <p><strong>Address:</strong> ${customer.address}, ${customer.city}</p>
                    <p><strong>Date of Birth:</strong> <fmt:formatDate value="${customer.dateOfBirth}" type="date" dateStyle="medium"/></p>
                    <hr>
                    <a href="${pageContext.request.contextPath}/customer/edit/${customer.id}" class="btn btn-warning">Edit Customer</a>
                    <a href="${pageContext.request.contextPath}/account?customerId=${customer.id}" class="btn btn-info">View Accounts</a>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="../common/footer.jsp"/>
</body>
</html>