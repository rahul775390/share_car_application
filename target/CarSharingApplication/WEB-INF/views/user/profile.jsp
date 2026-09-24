<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile - CarSharing</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        body {
            padding-top: 100px;
        }
        .profile-container {
            max-width: 1000px;
            margin: 0 auto;
        }
    </style>
</head>
<body>

    <!-- Header Navigation -->
    <nav class="navbar navbar-expand-lg navbar-light fixed-top custom-navbar">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">
                <i class="bi bi-car-front-fill me-2"></i>CarSharing
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/rides/search"><i class="bi bi-search me-1"></i>Find Rides</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/rentals"><i class="bi bi-key me-1"></i>Rent a Car</a>
                    </li>
                </ul>
                <div class="d-flex align-items-center">
                    <div class="dropdown">
                        <button class="btn btn-outline-primary dropdown-toggle" type="button" id="profileDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="bi bi-person-circle me-1"></i>Hello, ${sessionScope.user.name}
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0 mt-2" aria-labelledby="profileDropdown">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person me-2"></i>My Profile</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/vehicles"><i class="bi bi-truck me-2"></i>My Vehicles</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/rides"><i class="bi bi-compass me-2"></i>My Offered Rides</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/bookings"><i class="bi bi-journal-text me-2"></i>My Bookings</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/rentals/my"><i class="bi bi-tags me-2"></i>My Rental Listings</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/rentals/bookings"><i class="bi bi-calendar-check me-2"></i>My Car Rentals</a></li>
                            <c:if test="${sessionScope.user.role == 'ADMIN'}">
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/admin/dashboard"><i class="bi bi-speedometer2 me-2"></i>Admin Dashboard</a></li>
                            </c:if>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-secondary" href="${pageContext.request.contextPath}/auth/logout"><i class="bi bi-box-arrow-right me-2"></i>Logout</a></li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Container -->
    <main class="container profile-container mb-5">
        <h2 class="fw-bold mb-1">Your Profile Dashboard</h2>
        <p class="text-muted mb-4">View and update your personal information.</p>

        <div class="row g-4">
            
            <!-- Profile Settings Panel -->
            <div class="col-lg-6">
                <div class="bg-white p-4 border rounded-4 shadow-sm h-100">
                    <h4 class="fw-bold mb-3"><i class="bi bi-gear-fill me-2 text-primary"></i>Profile Details</h4>
                    <p class="text-muted">Update your public profile information.</p>

                    <!-- Alert messages -->
                    <c:if test="${not empty message}">
                        <div class="alert alert-success d-flex align-items-center" role="alert">
                            <i class="bi bi-check-circle-fill me-2"></i>
                            <div>${message}</div>
                        </div>
                    </c:if>
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger d-flex align-items-center" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>
                            <div>${error}</div>
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/profile" method="POST" class="needs-validation" novalidate>
                        <div class="mb-3">
                            <label for="name" class="form-label">Full Name</label>
                            <input type="text" class="form-control" id="name" name="name" value="${sessionScope.user.name}" required>
                            <div class="invalid-feedback">Name cannot be empty.</div>
                        </div>
                        <div class="mb-3">
                            <label for="email" class="form-label">Email Address</label>
                            <input type="email" class="form-control bg-light" id="email" value="${sessionScope.user.email}" disabled>
                            <span class="form-text text-muted">Email cannot be modified.</span>
                        </div>
                        <div class="mb-3">
                            <label for="phone" class="form-label">Phone Number</label>
                            <input type="tel" class="form-control" id="phone" name="phone" value="${sessionScope.user.phone}" pattern="[0-9]{10,15}" required>
                            <div class="invalid-feedback">Please enter a valid phone number.</div>
                        </div>
                        <div class="mb-4">
                            <label for="city" class="form-label">City</label>
                            <input type="text" class="form-control" id="city" name="city" value="${sessionScope.user.city}" required>
                            <div class="invalid-feedback">City is required.</div>
                        </div>
                        <button type="submit" class="btn btn-primary-custom w-100">Update Profile</button>
                    </form>
                </div>
            </div>

            <!-- Profile Info Metadata -->
            <div class="col-lg-6">
                <div class="bg-white p-4 border rounded-4 shadow-sm h-100 d-flex flex-column justify-content-between">
                    <div>
                        <h4 class="fw-bold mb-3"><i class="bi bi-shield-check me-2 text-primary"></i>Account Status</h4>
                        <div class="table-responsive">
                            <table class="table table-borderless align-middle">
                                <tbody>
                                    <tr>
                                        <td class="text-muted fw-semibold py-2">Role Type:</td>
                                        <td>
                                            <span class="badge bg-primary px-3 py-2 rounded-3">${sessionScope.user.role}</span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="text-muted fw-semibold py-2">Account Status:</td>
                                        <td>
                                            <span class="badge bg-success px-3 py-2 rounded-3">${sessionScope.user.status}</span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="text-muted fw-semibold py-2">Current City:</td>
                                        <td class="text-dark fw-bold">${sessionScope.user.city}</td>
                                    </tr>
                                    <tr>
                                        <td class="text-muted fw-semibold py-2">Registration Date:</td>
                                        <td class="text-dark fw-bold">${sessionScope.user.createdAt}</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="p-3 bg-light rounded-3 mt-3">
                        <small class="text-muted d-block mb-2"><i class="bi bi-info-circle me-1"></i>Unified Account Permission:</small>
                        <p class="mb-0 small text-secondary">
                            Your account is authorized to secure private user profile listings and manage access filter keys. Dashboard functions can be extended in future development phases.
                        </p>
                    </div>
                </div>
            </div>

        </div>
    </main>

    <!-- Bootstrap 5 Bundle with Popper JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Custom JS -->
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
