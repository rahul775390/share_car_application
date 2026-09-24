<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - CarSharing</title>
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
            padding: 40px 0;
        }
        .register-card {
            border: 1px solid var(--border-color);
            border-radius: 20px;
            box-shadow: var(--shadow-lg);
            background: var(--card-bg);
            padding: 40px;
            max-width: 500px;
            width: 100%;
        }
    </style>
</head>
<body>

    <div class="container d-flex justify-content-center">
        <div class="register-card">
            <div class="text-center mb-4">
                <a href="${pageContext.request.contextPath}/index.jsp" class="navbar-brand text-center d-block mb-2" style="font-size: 2rem;">
                    <i class="bi bi-car-front-fill me-2"></i>CarSharing
                </a>
                <h4 class="fw-bold">Create your account</h4>
                <p class="text-muted">Register to join the Car Sharing Application</p>
            </div>

            <!-- Error Banner -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger d-flex align-items-center" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>
                    <div>${error}</div>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/auth/register" method="POST" class="needs-validation" novalidate>
                <div class="mb-3">
                    <label for="name" class="form-label">Full Name</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light border-end-0"><i class="bi bi-person text-muted"></i></span>
                        <input type="text" class="form-control bg-light border-start-0" id="name" name="name" placeholder="John Doe" required>
                        <div class="invalid-feedback">Please enter your full name.</div>
                    </div>
                </div>

                <div class="mb-3">
                    <label for="email" class="form-label">Email Address</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light border-end-0"><i class="bi bi-envelope text-muted"></i></span>
                        <input type="email" class="form-control bg-light border-start-0" id="email" name="email" placeholder="john@example.com" required>
                        <div class="invalid-feedback">Please enter a valid email address.</div>
                    </div>
                </div>

                <div class="mb-3">
                    <label for="phone" class="form-label">Phone Number</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light border-end-0"><i class="bi bi-telephone text-muted"></i></span>
                        <input type="tel" class="form-control bg-light border-start-0" id="phone" name="phone" placeholder="9876543210" pattern="[0-9]{10,15}" required>
                        <div class="invalid-feedback">Please enter a valid phone number (10-15 digits).</div>
                    </div>
                </div>

                <div class="mb-3">
                    <label for="city" class="form-label">City</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light border-end-0"><i class="bi bi-geo-alt text-muted"></i></span>
                        <input type="text" class="form-control bg-light border-start-0" id="city" name="city" placeholder="e.g. Pune" required>
                        <div class="invalid-feedback">Please enter your city.</div>
                    </div>
                </div>

                <div class="mb-3">
                    <label for="password" class="form-label">Password</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light border-end-0"><i class="bi bi-lock text-muted"></i></span>
                        <input type="password" class="form-control bg-light border-start-0" id="password" name="password" placeholder="••••••••" minlength="6" required>
                        <div class="invalid-feedback">Password must be at least 6 characters.</div>
                    </div>
                </div>

                <div class="mb-4">
                    <label for="confirmPassword" class="form-label">Confirm Password</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light border-end-0"><i class="bi bi-lock-fill text-muted"></i></span>
                        <input type="password" class="form-control bg-light border-start-0" id="confirmPassword" name="confirmPassword" placeholder="••••••••" required>
                        <div class="invalid-feedback">Please confirm your password.</div>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary-custom w-100 mb-3">Sign Up</button>

                <div class="text-center mt-3">
                    <p class="text-muted mb-0">Already have an account? <a href="${pageContext.request.contextPath}/auth/login" class="text-primary text-decoration-none fw-semibold">Login here</a></p>
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
