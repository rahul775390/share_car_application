<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Rent a Car - CarSharing</title>
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
        .search-container {
            max-width: 1050px;
            margin: 0 auto;
        }
        .rental-card {
            transition: var(--transition);
        }
        .rental-card:hover {
            transform: translateY(-4px);
            box-shadow: var(--shadow-md);
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
                        <a class="nav-link active fw-bold text-primary" href="${pageContext.request.contextPath}/rentals"><i class="bi bi-key me-1"></i>Rent a Car</a>
                    </li>
                </ul>
                <div class="d-flex align-items-center">
                    <c:choose>
                        <c:when test="${not empty sessionScope.user}">
                            <div class="dropdown">
                                <button class="btn btn-outline-primary dropdown-toggle" type="button" id="profileDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                                    <i class="bi bi-person-circle me-1"></i>Hello, ${sessionScope.user.name}
                                </button>
                                <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0 mt-2" aria-labelledby="profileDropdown">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person me-2"></i>My Profile</a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/vehicles"><i class="bi bi-truck me-2"></i>My Vehicles</a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/rides"><i class="bi bi-compass me-2"></i>My Rides</a></li>
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
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/auth/login" class="btn btn-link text-decoration-none text-dark me-2">Login</a>
                            <a href="${pageContext.request.contextPath}/auth/register" class="btn btn-primary-custom">Register</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Container -->
    <main class="container search-container mb-5">
        <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
            <div>
                <h2 class="fw-bold mb-1">Find & Rent Vehicles</h2>
                <p class="text-muted mb-0">Rent a verified car by the day for your personal road trips or weekend travels.</p>
            </div>
            <c:if test="${not empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/rentals/create" class="btn btn-primary-custom">
                    <i class="bi bi-plus-circle me-1"></i>List Your Car for Rent
                </a>
            </c:if>
        </div>

        <!-- Search Form Card -->
        <div class="bg-white p-4 border rounded-4 shadow-sm mb-4">
            <form action="${pageContext.request.contextPath}/rentals" method="GET">
                <div class="row g-3 align-items-end">
                    <div class="col-md-4">
                        <label for="location" class="form-label fw-semibold">Pickup Location / City</label>
                        <input type="text" class="form-control" id="location" name="location" value="${param.location}" placeholder="e.g. Mumbai, Bangalore...">
                    </div>
                    <div class="col-md-3">
                        <label for="from" class="form-label fw-semibold">Available From</label>
                        <input type="date" class="form-control" id="from" name="from" value="${param.from}">
                    </div>
                    <div class="col-md-3">
                        <label for="maxPrice" class="form-label fw-semibold">Max Daily Rate (₹)</label>
                        <input type="number" class="form-control" id="maxPrice" name="maxPrice" value="${param.maxPrice}" placeholder="e.g. 2500">
                    </div>
                    <div class="col-md-2">
                        <button type="submit" class="btn btn-primary-custom w-100 py-2"><i class="bi bi-search me-1"></i>Search</button>
                    </div>
                </div>
            </form>
        </div>

        <!-- Listings Section -->
        <c:choose>
            <c:when test="${empty listings}">
                <div class="bg-white p-5 border rounded-4 shadow-sm text-center my-4">
                    <i class="bi bi-car-front text-muted display-4 mb-3 d-block"></i>
                    <h5 class="fw-bold">No rental cars found</h5>
                    <p class="text-muted mb-3">Try widening your search location or clearing filters.</p>
                    <c:if test="${not empty sessionScope.user}">
                        <a href="${pageContext.request.contextPath}/rentals/create" class="btn btn-outline-primary">Be the First to List a Car</a>
                    </c:if>
                </div>
            </c:when>
            <c:otherwise>
                <div class="row g-4">
                    <c:forEach var="l" items="${listings}">
                        <div class="col-md-6 col-lg-4">
                            <div class="card border rounded-4 h-100 bg-white rental-card">
                                <div class="card-body p-4 d-flex flex-column justify-content-between">
                                    <div>
                                        <div class="d-flex justify-content-between align-items-start mb-2">
                                            <h5 class="fw-bold mb-0 text-dark">${l.vehicleMake} ${l.vehicleModel}</h5>
                                            <span class="badge bg-primary-subtle text-primary px-2 py-1 rounded-2">${l.vehicleType}</span>
                                        </div>
                                        <p class="text-muted small mb-3"><i class="bi bi-geo-alt-fill text-danger me-1"></i>${l.location}</p>

                                        <div class="p-3 bg-light rounded-3 mb-3 small">
                                            <div class="d-flex justify-content-between mb-1">
                                                <span class="text-muted">Transmission:</span>
                                                <span class="fw-bold text-dark">${l.transmission}</span>
                                            </div>
                                            <div class="d-flex justify-content-between mb-1">
                                                <span class="text-muted">Fuel Type:</span>
                                                <span class="fw-bold text-dark">${l.fuelType}</span>
                                            </div>
                                            <div class="d-flex justify-content-between mb-1">
                                                <span class="text-muted">Seating Capacity:</span>
                                                <span class="fw-bold text-dark">${l.seatingCapacity} seats</span>
                                            </div>
                                            <div class="d-flex justify-content-between">
                                                <span class="text-muted">Available Period:</span>
                                                <span class="fw-bold text-dark">${l.availableFrom} to ${l.availableUntil}</span>
                                            </div>
                                        </div>
                                    </div>

                                    <div>
                                        <div class="d-flex justify-content-between align-items-baseline border-top pt-3 mb-3">
                                            <div>
                                                <span class="h4 fw-bold text-success mb-0">₹${l.pricePerDay}</span>
                                                <span class="text-muted small"> / day</span>
                                            </div>
                                            <small class="text-muted">Deposit: ₹${l.securityDeposit}</small>
                                        </div>
                                        <a href="${pageContext.request.contextPath}/rentals/details?id=${l.id}" class="btn btn-primary-custom w-100">
                                            <i class="bi bi-eye me-1"></i>View & Rent
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </main>

    <!-- Bootstrap 5 Bundle with Popper JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
