<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Create Customer" scope="request"/>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>

<div id="page-content-wrapper">
    <%@ include file="../common/navigation.jsp" %>
    <div class="container-fluid">
        <h1 class="mt-4">Create New Customer</h1>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <div class="card">
            <div class="card-body">
                <form action="<c:url value='/customer/create'/>" method="post" class="needs-validation" novalidate>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="firstName" class="form-label">First Name</label>
                            <input type="text" class="form-control" id="firstName" name="firstName" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="lastName" class="form-label">Last Name</label>
                            <input type="text" class="form-control" id="lastName" name="lastName" required>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="nicNumber" class="form-label">NIC Number</label>
                            <input type="text" class="form-control" id="nicNumber" name="nicNumber" required>
                            <div class="invalid-feedback">A valid NIC is required.</div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="dateOfBirth" class="form-label">Date of Birth</label>
                            <input type="date" class="form-control" id="dateOfBirth" name="dateOfBirth" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" class="form-control" id="email" name="email" required>
                        <div class="invalid-feedback">A valid email is required.</div>
                    </div>
                    <div class="mb-3">
                        <label for="phoneNumber" class="form-label">Phone Number</label>
                        <input type="text" class="form-control" id="phoneNumber" name="phoneNumber" required>
                    </div>
                    <button type="submit" class="btn btn-primary">Create Customer</button>
                </form>
            </div>
        </div>
    </div>
</div>
<script src="<c:url value='/js/ajax-handlers.js'/>"></script>
<script src="<c:url value='/js/form-validation.js'/>"></script>
<%@ include file="../common/footer.jsp" %>