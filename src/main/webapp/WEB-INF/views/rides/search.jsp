<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Rides - CarSharing</title>
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
                        <a class="nav-link active" href="${pageContext.request.contextPath}/rides/search"><i class="bi bi-search me-1"></i>Find Rides</a>
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

    <!-- Main Container -->
    <main class="container search-container mb-5">
        <h2 class="fw-bold mb-1">Find Shared Rides</h2>
        <p class="text-muted mb-4">Search routes and book seats dynamically.</p>

        <!-- Search Form Card -->
        <div class="bg-white p-4 border rounded-4 shadow-sm mb-5">
            <form action="${pageContext.request.contextPath}/rides/search" method="GET">
                <div class="row g-3 align-items-end">
                    <div class="col-md-3">
                        <label for="source" class="form-label fw-semibold">From (Source)</label>
                        <input type="text" class="form-control" id="source" name="source" value="${param.source}" placeholder="City name" required>
                    </div>
                    <div class="col-md-3">
                        <label for="destination" class="form-label fw-semibold">To (Destination)</label>
                        <input type="text" class="form-control" id="destination" name="destination" value="${param.destination}" placeholder="City name" required>
                    </div>
                    <div class="col-md-3">
                        <label for="date" class="form-label fw-semibold">Date</label>
                        <input type="date" class="form-control" id="date" name="date" value="${param.date}">
                    </div>
                    <div class="col-md-2">
                        <label for="maxPrice" class="form-label fw-semibold">Max Price (₹)</label>
                        <input type="number" class="form-control" id="maxPrice" name="maxPrice" value="${param.maxPrice}" placeholder="Budget cap">
                    </div>
                    <div class="col-md-1">
                        <button type="submit" class="btn btn-primary-custom w-100 py-2"><i class="bi bi-search"></i></button>
                    </div>
                </div>
            </form>
        </div>

        <!-- Listing section -->
        <c:if test="${not empty param.source || not empty param.destination}">
            <h4 class="fw-bold mb-3">Search Results</h4>
            
            <c:choose>
                <c:when test="${empty rides}">
                    <div class="bg-white p-5 border rounded-4 shadow-sm text-center">
                        <i class="bi bi-exclamation-circle text-muted display-4 mb-3 d-block"></i>
                        <h5>No matching rides found</h5>
                        <p class="text-muted mb-0">Try expanding your dates or checking alternative transit stations.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="row g-4">
                        <c:forEach var="r" items="${rides}">
                            <div class="col-md-6">
                                <div class="card border rounded-4 shadow-sm h-100 bg-white">
                                    <div class="card-body p-4 d-flex flex-column justify-content-between">
                                        <div>
                                            <div class="d-flex justify-content-between align-items-start mb-3">
                                                <h4 class="fw-bold mb-0 text-primary">${r.source} ➔ ${r.destination}</h4>
                                                <c:choose>
                                                    <c:when test="${r.status == 'ACTIVE'}">
                                                        <span class="badge bg-success px-3 py-2 rounded-3">ACTIVE</span>
                                                    </c:when>
                                                    <c:when test="${r.status == 'FULL'}">
                                                        <span class="badge bg-warning px-3 py-2 rounded-3">FULL</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary px-3 py-2 rounded-3">${r.status}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="table-responsive">
                                                <table class="table table-borderless table-sm mb-3 align-middle" style="font-size: 0.9rem;">
                                                    <tbody>
                                                        <tr>
                                                            <td class="text-muted fw-semibold py-1" style="width: 45%;">Driver:</td>
                                                            <td class="text-dark fw-bold py-1">${r.driverName}</td>
                                                        </tr>
                                                        <tr>
                                                            <td class="text-muted fw-semibold py-1">Date / Time:</td>
                                                            <td class="text-dark py-1">${r.rideDate} at ${r.rideTime}</td>
                                                        </tr>
                                                        <tr>
                                                            <td class="text-muted fw-semibold py-1">Vehicle Details:</td>
                                                            <td class="text-dark py-1">${r.vehicleMake} ${r.vehicleModel}</td>
                                                        </tr>
                                                        <tr>
                                                            <td class="text-muted fw-semibold py-1">Available Seats:</td>
                                                            <td class="text-dark py-1">${r.availableSeats} seats left</td>
                                                        </tr>
                                                        <tr>
                                                            <td class="text-muted fw-semibold py-1">Fare (per Seat):</td>
                                                            <td class="text-success fw-bold py-1">₹${r.pricePerSeat}</td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                        <div class="border-top pt-3 mt-3 d-flex justify-content-end">
                                            <a href="${pageContext.request.contextPath}/rides/details?id=${r.id}" class="btn btn-primary-custom px-4"><i class="bi-eye me-1"></i>View Details</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>
    </main>

    <!-- Bootstrap 5 Bundle with Popper JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
