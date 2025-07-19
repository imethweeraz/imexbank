<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- This form allows editing an existing customer's details. --%>
<!DOCTYPE html>
<html>
<head><title>Update Customer - ImexBank</title><jsp:include page="../common/header.jsp"/></head>
<body>
<div class="d-flex" id="wrapper">
    <jsp:include page="../common/sidebar.jsp"/>
    <div id="page-content-wrapper">
        <jsp:include page="../common/navigation.jsp"/>
        <div class="container-fluid">
            <h1 class="mt-4">Update Customer</h1>
            <%-- A form pre-filled with customer data, posting to CustomerServlet's /update path. --%>
            <p>Form to update customer details for ${customer.fullName} would be here.</p>
        </div>
    </div>
</div>
<jsp:include page="../common/footer.jsp"/>
</body>
</html>