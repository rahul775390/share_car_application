<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Bookings - CarSharing</title>
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
        .bookings-container {
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
                            <li><a class="dropdown-item active" href="${pageContext.request.contextPath}/bookings"><i class="bi bi-journal-text me-2"></i>My Bookings</a></li>
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
    <main class="container bookings-container mb-5">
        <h2 class="fw-bold mb-1">My Bookings</h2>
        <p class="text-muted mb-4">Track the statuses and trip logs of seats you reserved.</p>

        <!-- Feedback Alert banners -->
        <c:if test="${param.added == 'true'}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i>Booking confirmed successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${param.cancelled == 'true'}">
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>Booking cancelled successfully.
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty bookings}">
                <div class="bg-white p-5 border rounded-4 shadow-sm text-center my-5">
                    <div class="display-1 text-muted mb-3"><i class="bi bi-journal-text"></i></div>
                    <h4 class="fw-bold">No bookings registered yet</h4>
                    <p class="text-muted">You haven't reserved any rides. Browse available routes to find rides near you.</p>
                    <a href="${pageContext.request.contextPath}/rides/search" class="btn btn-primary-custom mt-2">Find a Ride Share</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="row g-4">
                    <c:forEach var="b" items="${bookings}">
                        <div class="col-md-6">
                            <div class="card border rounded-4 shadow-sm h-100 bg-white">
                                <div class="card-body p-4 d-flex flex-column justify-content-between">
                                    <div>
                                        <div class="d-flex justify-content-between align-items-start mb-3">
                                            <h4 class="fw-bold mb-0 text-primary">${b.source} ➔ ${b.destination}</h4>
                                            <c:choose>
                                                <c:when test="${b.status == 'CONFIRMED'}">
                                                    <span class="badge bg-success px-3 py-2 rounded-3">CONFIRMED</span>
                                                </c:when>
                                                <c:when test="${b.status == 'CANCELLED'}">
                                                    <span class="badge bg-danger px-3 py-2 rounded-3">CANCELLED</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary px-3 py-2 rounded-3">${b.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="table-responsive">
                                            <table class="table table-borderless table-sm mb-3 align-middle" style="font-size: 0.9rem;">
                                                <tbody>
                                                    <tr>
                                                        <td class="text-muted fw-semibold py-1" style="width: 45%;">Departure Date:</td>
                                                        <td class="text-dark fw-bold py-1">${b.rideDate} at ${b.rideTime}</td>
                                                    </tr>
                                                    <tr>
                                                        <td class="text-muted fw-semibold py-1">Driver Details:</td>
                                                        <td class="text-dark py-1">${b.driverName} (Ph: ${b.driverPhone})</td>
                                                    </tr>
                                                    <tr>
                                                        <td class="text-muted fw-semibold py-1">Reserved seats:</td>
                                                        <td class="text-dark py-1">${b.seatsBooked} seat(s)</td>
                                                    </tr>
                                                    <tr>
                                                        <td class="text-muted fw-semibold py-1">Total Paid:</td>
                                                        <td class="text-success fw-bold py-1">₹${b.totalPrice}</td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                    <div class="border-top pt-3 mt-3 d-flex flex-wrap gap-2 justify-content-between align-items-center">
                                        <a href="${pageContext.request.contextPath}/bookings/details?id=${b.id}" class="btn btn-sm btn-outline-secondary px-3"><i class="bi bi-eye me-1"></i>View details</a>
                                        <c:if test="${b.status == 'CONFIRMED'}">
                                            <a href="${pageContext.request.contextPath}/bookings/cancel?id=${b.id}" onclick="return confirm('Are you sure you want to cancel this booking?');" class="btn btn-sm btn-danger px-3">Cancel Booking</a>
                                        </c:if>
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
</body>
</html>
