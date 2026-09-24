<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ride Details - CarSharing</title>
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
        .details-container {
            max-width: 800px;
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

    <!-- Main Details Container -->
    <main class="container details-container mb-5">
        <div class="row g-4">
            
            <!-- Ride Main Specifications Card -->
            <div class="col-lg-8">
                <div class="bg-white p-4 border rounded-4 shadow-sm h-100">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h3 class="fw-bold mb-0 text-primary">${ride.source} ➔ ${ride.destination}</h3>
                        <c:choose>
                            <c:when test="${ride.status == 'ACTIVE'}">
                                <span class="badge bg-success px-3 py-2 rounded-3">ACTIVE</span>
                            </c:when>
                            <c:when test="${ride.status == 'FULL'}">
                                <span class="badge bg-warning px-3 py-2 rounded-3">FULL</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-secondary px-3 py-2 rounded-3">${ride.status}</span>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-bordered align-middle">
                            <tbody>
                                <tr>
                                    <th class="bg-light fw-semibold" style="width: 40%;">Departure Date</th>
                                    <td>${ride.rideDate}</td>
                                </tr>
                                <tr>
                                    <th class="bg-light fw-semibold">Departure Time</th>
                                    <td>${ride.rideTime}</td>
                                </tr>
                                <tr>
                                    <th class="bg-light fw-semibold">Available Seats</th>
                                    <td class="fw-bold text-dark">${ride.availableSeats} of ${ride.totalSeats} seats remaining</td>
                                </tr>
                                <tr>
                                    <th class="bg-light fw-semibold">Price per Seat</th>
                                    <td class="text-success fw-bold">₹${ride.pricePerSeat}</td>
                                </tr>
                                <tr>
                                    <th class="bg-light fw-semibold">Vehicle Utilized</th>
                                    <td>${ride.vehicleMake} ${ride.vehicleModel} (${ride.vehiclePlate})</td>
                                </tr>
                                <tr>
                                    <th class="bg-light fw-semibold">Driver Details</th>
                                    <td>${ride.driverName} (Ph: ${ride.driverPhone})</td>
                                </tr>
                                <tr>
                                    <th class="bg-light fw-semibold">Description</th>
                                    <td>${ride.description}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <!-- Manage ride buttons for Driver -->
                    <c:if test="${sessionScope.user.id == ride.driverId}">
                        <div class="border-top pt-4 mt-4 d-flex gap-2">
                            <c:if test="${ride.status == 'ACTIVE' || ride.status == 'FULL'}">
                                <a href="${pageContext.request.contextPath}/rides/edit?id=${ride.id}" class="btn btn-primary-custom flex-grow-1"><i class="bi bi-pencil me-2"></i>Edit Ride</a>
                                <a href="${pageContext.request.contextPath}/rides/cancel?id=${ride.id}" onclick="return confirm('Are you sure you want to cancel this ride?');" class="btn btn-danger px-4">Cancel Ride</a>
                            </c:if>
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- Booking panel side card -->
            <div class="col-lg-4">
                <div class="bg-white p-4 border rounded-4 shadow-sm h-100 d-flex flex-column justify-content-between">
                    <div>
                        <h4 class="fw-bold mb-3"><i class="bi bi-journal-check text-primary me-2"></i>Reservation</h4>
                        
                        <!-- Error Alert banner -->
                        <c:if test="${not empty param.error}">
                            <div class="alert alert-danger small mb-3" role="alert">
                                <i class="bi bi-exclamation-triangle-fill me-1"></i>${param.error}
                            </div>
                        </c:if>

                        <c:choose>
                            <c:when test="${empty sessionScope.user}">
                                <div class="p-3 bg-light rounded-3 text-center">
                                    <i class="bi bi-person-lock display-6 d-block mb-2 text-primary"></i>
                                    <h6 class="fw-bold mb-1">Login to Book</h6>
                                    <p class="small text-muted mb-3">Please sign in to reserve seats on this ride.</p>
                                    <a href="${pageContext.request.contextPath}/auth/login" class="btn btn-primary-custom w-100 btn-sm">Login to Reserve</a>
                                </div>
                            </c:when>
                            <c:when test="${sessionScope.user.id == ride.driverId}">
                                <div class="p-3 bg-light rounded-3 text-center text-muted">
                                    <i class="bi bi-info-circle display-6 d-block mb-2 text-primary"></i>
                                    <small>You are the Driver of this ride share. Drivers cannot book seats on their own published routes.</small>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${ride.status == 'ACTIVE' && ride.availableSeats > 0}">
                                        <form action="${pageContext.request.contextPath}/bookings/book" method="POST" class="needs-validation" novalidate>
                                            <input type="hidden" name="rideId" value="${ride.id}">
                                            
                                            <div class="mb-4">
                                                <label for="seats" class="form-label fw-semibold">Seats to Book</label>
                                                <select class="form-select" id="seats" name="seats" required>
                                                    <c:forEach var="i" begin="1" end="${ride.availableSeats}">
                                                        <option value="${i}">${i} Seat(s) - ₹${i * ride.pricePerSeat}</option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                            <button type="submit" class="btn btn-primary-custom w-100">Confirm Booking</button>
                                        </form>
                                    </c:when>
                                    <c:when test="${ride.status == 'FULL'}">
                                        <div class="p-3 bg-warning-subtle text-warning-emphasis rounded-3 text-center">
                                            <i class="bi-exclamation-triangle-fill display-6 d-block mb-2"></i>
                                            <h6 class="fw-bold mb-0">RIDE IS FULL</h6>
                                            <small>All seats on this commute route are booked.</small>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="p-3 bg-secondary-subtle text-secondary-emphasis rounded-3 text-center">
                                            <i class="bi-x-circle-fill display-6 d-block mb-2"></i>
                                            <h6 class="fw-bold mb-0">UNAVAILABLE</h6>
                                            <small>This ride share has been cancelled or completed.</small>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="border-top pt-3 mt-3 text-center">
                        <a href="${pageContext.request.contextPath}/rides/search?source=${ride.source}&destination=${ride.destination}" class="btn btn-sm btn-outline-secondary w-100">Back to List</a>
                    </div>
                </div>
            </div>

        </div>
    </main>

    <!-- Bootstrap 5 Bundle with Popper JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
