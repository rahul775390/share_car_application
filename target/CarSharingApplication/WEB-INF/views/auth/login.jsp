<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - CoShare</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        body {
            background: linear-gradient(135deg, #e6e9fa 0%, #f4f5fc 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .login-card {
            border: 1px solid var(--border-color);
            border-radius: 20px;
            box-shadow: var(--shadow-lg);
            background: var(--card-bg);
            padding: 40px;
            max-width: 450px;
            width: 100%;
        }
    </style>
</head>
<body>

    <div class="container d-flex justify-content-center">
        <div class="login-card">
            <div class="text-center mb-4">
                <a href="${pageContext.request.contextPath}/index.jsp" class="navbar-brand text-center d-block mb-2" style="font-size: 2rem;">
                    <i class="bi bi-car-front-fill me-2"></i>CoShare
                </a>
                <h4 class="fw-bold">Welcome Back</h4>
                <p class="text-muted">Enter credentials to access your account</p>
            </div>

            <!-- Success Notification Message -->
            <c:if test="${not empty message}">
                <div class="alert alert-success d-flex align-items-center" role="alert">
                    <i class="bi bi-check-circle-fill me-2"></i>
                    <div>${message}</div>
                </div>
            </c:if>

            <!-- Error Banner -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger d-flex align-items-center" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>
                    <div>${error}</div>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/auth/login" method="POST" class="needs-validation" novalidate>
                <div class="mb-3">
                    <label for="email" class="form-label">Email Address</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light border-end-0"><i class="bi bi-envelope text-muted"></i></span>
                        <input type="email" class="form-control bg-light border-start-0" id="email" name="email" placeholder="john@example.com" required>
                        <div class="invalid-feedback">Please enter your email.</div>
                    </div>
                </div>

                <div class="mb-4">
                    <label for="password" class="form-label">Password</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light border-end-0"><i class="bi bi-lock text-muted"></i></span>
                        <input type="password" class="form-control bg-light border-start-0" id="password" name="password" placeholder="••••••••" required>
                        <div class="invalid-feedback">Please enter your password.</div>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary-custom w-100 mb-3">Sign In</button>

                <div class="text-center mt-3">
                    <p class="text-muted mb-0">New to CoShare? <a href="${pageContext.request.contextPath}/auth/register" class="text-primary text-decoration-none fw-semibold">Register here</a></p>
                </div>
            </form>
        </div>
    </div>

    <!-- Bootstrap 5 Bundle with Popper JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Custom JS -->
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
