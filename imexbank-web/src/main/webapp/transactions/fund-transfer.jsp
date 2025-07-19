<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Fund Transfer" scope="request"/>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>

<div id="page-content-wrapper">
    <%@ include file="../common/navigation.jsp" %>
    <div class="container-fluid">
        <h1 class="mt-4">Fund Transfer</h1>
        <p>Transfer money between accounts securely.</p>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <div class="card">
            <div class="card-body">
                <form action="<c:url value='/transaction/transfer'/>" method="post" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label for="sourceAccount" class="form-label">From Account</label>
                        <input type="text" class="form-control" id="sourceAccount" name="sourceAccount" required>
                    </div>
                    <div class="mb-3">
                        <label for="targetAccount" class="form-label">To Account</label>
                        <input type="text" class="form-control" id="targetAccount" name="targetAccount" required>
                    </div>
                    <div class="mb-3">
                        <label for="amount" class="form-label">Amount</label>
                        <input type="number" step="0.01" class="form-control" id="amount" name="amount" required>
                    </div>
                    <div class="mb-3">
                        <label for="description" class="form-label">Description</label>
                        <input type="text" class="form-control" id="description" name="description">
                    </div>
                    <button type="submit" class="btn btn-primary">Submit Transfer</button>
                </form>
            </div>
        </div>
    </div>
</div>
<script src="<c:url value='/js/form-validation.js'/>"></script>
<%@ include file="../common/footer.jsp" %>