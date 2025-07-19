<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- This page would display a detailed, printable statement for a specific account. --%>
<!DOCTYPE html>
<html>
<head><title>Account Statement - ImexBank</title><jsp:include page="../common/header.jsp"/></head>
<body>
<div class="container">
    <h1 class="mt-4">Account Statement</h1>
    <p>Account: ${account.accountNumber}</p>
    <p>Period: _Start_Date_ to _End_Date_</p>
    <hr>
    <%-- A detailed table of transactions for the period would be rendered here. --%>
</div>
<jsp:include page="../common/footer.jsp"/>
</body>
</html>