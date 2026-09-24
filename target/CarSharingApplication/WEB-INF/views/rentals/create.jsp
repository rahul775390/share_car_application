<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>List Your Car for Rent - CarSharing</title>
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
        .form-container {
            max-width: 750px;
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
                        <a class="nav-link active" href="${pageContext.request.contextPath}/rentals"><i class="bi bi-key me-1"></i>Rent a Car</a>
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
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Container -->
    <main class="container form-container mb-5">
        <h2 class="fw-bold mb-1">List Vehicle for Daily Rental</h2>
        <p class="text-muted mb-4">Earn by renting out your idle vehicle to verified community drivers.</p>

        <!-- Alert messages -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger d-flex align-items-center mb-4" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                <div>${error}</div>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty vehicles}">
                <div class="bg-white p-5 border rounded-4 shadow-sm text-center">
                    <i class="bi bi-car-front display-4 text-muted mb-3 d-block"></i>
                    <h4 class="fw-bold">No registered vehicles found</h4>
                    <p class="text-muted mb-3">You need to register a vehicle in your profile fleet before you can list it for rental.</p>
                    <a href="${pageContext.request.contextPath}/vehicles/add" class="btn btn-primary-custom">
                        <i class="bi bi-plus-circle me-1"></i>Add a Vehicle First
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="bg-white p-4 border rounded-4 shadow-sm">
                    <form action="${pageContext.request.contextPath}/rentals/create" method="POST" class="needs-validation" novalidate>
                        
                        <div class="mb-3">
                            <label for="vehicleId" class="form-label fw-semibold">Select Vehicle</label>
                            <select class="form-select" id="vehicleId" name="vehicleId" required>
                                <option value="" disabled selected>Select from your registered fleet...</option>
                                <c:forEach var="v" items="${vehicles}">
                                    <option value="${v.id}">${v.make} ${v.model} (${v.year}) - Plate: ${v.licensePlate} (${v.transmission}, ${v.fuelType})</option>
                                </c:forEach>
                            </select>
                            <div class="invalid-feedback">Please select a vehicle.</div>
                        </div>

                        <div class="mb-3">
                            <label for="location" class="form-label fw-semibold">Pickup & Return Location</label>
                            <input type="text" class="form-control" id="location" name="location" placeholder="e.g. Andheri East, Mumbai" required>
                            <div class="invalid-feedback">Location is required.</div>
                        </div>

                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label for="availableFrom" class="form-label fw-semibold">Available From Date</label>
                                <input type="date" class="form-control" id="availableFrom" name="availableFrom" required>
                                <div class="invalid-feedback">Start date is required.</div>
                            </div>
                            <div class="col-md-6">
                                <label for="availableUntil" class="form-label fw-semibold">Available Until Date</label>
                                <input type="date" class="form-control" id="availableUntil" name="availableUntil" required>
                                <div class="invalid-feedback">End date is required.</div>
                            </div>
                        </div>

                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label for="pricePerDay" class="form-label fw-semibold">Price per Day (₹)</label>
                                <input type="number" step="0.01" min="0" class="form-control" id="pricePerDay" name="pricePerDay" placeholder="e.g. 1800" required>
                                <div class="invalid-feedback">Please enter a valid daily rate.</div>
                            </div>
                            <div class="col-md-6">
                                <label for="securityDeposit" class="form-label fw-semibold">Security Deposit (₹)</label>
                                <input type="number" step="0.01" min="0" class="form-control" id="securityDeposit" name="securityDeposit" placeholder="e.g. 3000 (Refundable)" value="0">
                            </div>
                        </div>

                        <div class="mb-4">
                            <label for="description" class="form-label fw-semibold">Description / Rental Rules</label>
                            <textarea class="form-control" id="description" name="description" rows="3" placeholder="Fuel policy, kilometer limits, driving rules, clean handover requirements..."></textarea>
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-primary-custom flex-grow-1 py-2">
                                <i class="bi bi-check-circle me-1"></i>Publish Rental Listing
                            </button>
                            <a href="${pageContext.request.contextPath}/rentals" class="btn btn-outline-secondary px-4 py-2">Cancel</a>
                        </div>
                    </form>
                </div>
            </c:otherwise>
        </c:choose>
    </main>

    <!-- Bootstrap 5 Bundle with Popper JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
