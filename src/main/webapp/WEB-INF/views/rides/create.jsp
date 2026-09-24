<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Publish a Ride - CarSharing</title>
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
            max-width: 650px;
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
        <div class="bg-white p-4 border rounded-4 shadow-sm">
            <h3 class="fw-bold mb-1">Publish a Ride Share</h3>
            <p class="text-muted mb-4">Offer seats on your commute routes to split fuel expenses.</p>

            <!-- Error Banner -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger d-flex align-items-center" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>
                    <div>${error}</div>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${empty vehicles}">
                    <div class="text-center p-4 bg-light rounded-3">
                        <i class="bi bi-exclamation-circle text-warning display-4 mb-3 d-block"></i>
                        <h5>No vehicles available</h5>
                        <p class="text-muted">You must register an available vehicle under your ownership first before you can publish a ride.</p>
                        <a href="${pageContext.request.contextPath}/vehicles/add" class="btn btn-primary-custom px-4 mt-2">Add Vehicle</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <form action="${pageContext.request.contextPath}/rides/create" method="POST" class="needs-validation" novalidate>
                        <div class="row g-3">
                            <div class="col-12">
                                <label for="vehicleId" class="form-label">Select Vehicle</label>
                                <select class="form-select" id="vehicleId" name="vehicleId" required>
                                    <option value="">Choose your vehicle</option>
                                    <c:forEach var="v" items="${vehicles}">
                                        <option value="${v.id}">${v.make} ${v.model} (${v.licensePlate})</option>
                                    </c:forEach>
                                </select>
                                <div class="invalid-feedback">Please select a vehicle.</div>
                            </div>
                            <div class="col-md-6">
                                <label for="source" class="form-label">Source City / Location</label>
                                <input type="text" class="form-control" id="source" name="source" placeholder="e.g. Pune" required>
                                <div class="invalid-feedback">Source location is required.</div>
                            </div>
                            <div class="col-md-6">
                                <label for="destination" class="form-label">Destination City / Location</label>
                                <input type="text" class="form-control" id="destination" name="destination" placeholder="e.g. Mumbai" required>
                                <div class="invalid-feedback">Destination location is required.</div>
                            </div>
                            <div class="col-md-6">
                                <label for="date" class="form-label">Departure Date</label>
                                <input type="date" class="form-control" id="date" name="date" required>
                                <div class="invalid-feedback">Please select departure date.</div>
                            </div>
                            <div class="col-md-6">
                                <label for="time" class="form-label">Departure Time</label>
                                <input type="time" class="form-control" id="time" name="time" required>
                                <div class="invalid-feedback">Please select departure time.</div>
                            </div>
                            <div class="col-md-6">
                                <label for="seats" class="form-label">Available Seats</label>
                                <input type="number" class="form-control" id="seats" name="seats" placeholder="e.g. 3" min="1" required>
                                <div class="invalid-feedback">Available seats must be at least 1.</div>
                            </div>
                            <div class="col-md-6">
                                <label for="price" class="form-label">Price per Seat (₹)</label>
                                <input type="number" class="form-control" id="price" name="price" placeholder="e.g. 250" min="0" step="0.01" required>
                                <div class="invalid-feedback">Please enter seat price (0 or positive).</div>
                            </div>
                            <div class="col-12">
                                <label for="description" class="form-label">Ride Details / Remarks (Optional)</label>
                                <textarea class="form-control" id="description" name="description" rows="3" placeholder="Describe pickup spots, luggage options, detours..."></textarea>
                            </div>
                        </div>

                        <div class="mt-4 d-flex gap-2">
                            <button type="submit" class="btn btn-primary-custom flex-grow-1">Publish Ride</button>
                            <a href="${pageContext.request.contextPath}/rides" class="btn btn-outline-secondary px-4">Cancel</a>
                        </div>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <!-- Bootstrap 5 Bundle with Popper JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
