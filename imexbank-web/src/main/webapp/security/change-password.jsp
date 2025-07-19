<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Change Password - ImexBank</title>
    <jsp:include page="../common/header.jsp"/>
</head>
<body>
<div class="d-flex" id="wrapper">
    <jsp:include page="../common/sidebar.jsp"/>
    <div id="page-content-wrapper">
        <jsp:include page="../common/navigation.jsp"/>
        <div class="container-fluid">
            <h1 class="mt-4">Change Your Password</h1>
            <form action="${pageContext.request.contextPath}/security/change-password" method="post">
                <div class="mb-3">
                    <label for="oldPassword" class="form-label">Old Password</label>
                    <input type="password" class="form-control" id="oldPassword" name="oldPassword" required>
                </div>
                <div class="mb-3">
                    <label for="newPassword" class="form-label">New Password</label>
                    <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                </div>
                <button type="submit" class="btn btn-primary">Update Password</button>
            </form>
        </div>
    </div>
</div>
<jsp:include page="../common/footer.jsp"/>
</body>
</html>