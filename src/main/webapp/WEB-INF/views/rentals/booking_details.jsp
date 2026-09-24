<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Rental Booking Details - CarSharing</title>
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
                            <li><a class="dropdown-item active" href="${pageContext.request.contextPath}/rentals/bookings"><i class="bi bi-calendar-check me-2"></i>My Car Rentals</a></li>
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
    <main class="container details-container mb-5">
        <div class="bg-white p-4 border rounded-4 shadow-sm">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h3 class="fw-bold mb-0">Car Rental Reservation</h3>
                <span class="badge ${booking.status == 'CONFIRMED' ? 'bg-success' : booking.status == 'CANCELLED' ? 'bg-danger' : 'bg-secondary'} px-3 py-2 rounded-3">${booking.status}</span>
            </div>

            <div class="table-responsive mb-4">
                <table class="table table-bordered align-middle">
                    <tbody>
                        <tr>
                            <th class="bg-light fw-semibold" style="width: 40%;">Reservation ID</th>
                            <td class="fw-bold text-primary">#RNT-${booking.id}</td>
                        </tr>
                        <tr>
                            <th class="bg-light fw-semibold">Vehicle</th>
                            <td>${booking.vehicleMake} ${booking.vehicleModel} (<code>${booking.vehiclePlate}</code>)</td>
                        </tr>
                        <tr>
                            <th class="bg-light fw-semibold">Pickup & Return Location</th>
                            <td>${booking.location}</td>
                        </tr>
                        <tr>
                            <th class="bg-light fw-semibold">Rental Start Date</th>
                            <td>${booking.startDate}</td>
                        </tr>
                        <tr>
                            <th class="bg-light fw-semibold">Rental End Date</th>
                            <td>${booking.endDate}</td>
                        </tr>
                        <tr>
                            <th class="bg-light fw-semibold">Vehicle Owner</th>
                            <td>${booking.ownerName} (Ph: ${booking.ownerPhone})</td>
                        </tr>
                        <tr>
                            <th class="bg-light fw-semibold">Daily Rate</th>
                            <td>₹${booking.pricePerDay} / day</td>
                        </tr>
                        <tr>
                            <th class="bg-light fw-semibold">Security Deposit</th>
                            <td>₹${booking.securityDeposit}</td>
                        </tr>
                        <tr>
                            <th class="bg-light fw-semibold">Total Paid</th>
                            <td class="text-success fw-bold">₹${booking.totalPrice}</td>
                        </tr>
                        <tr>
                            <th class="bg-light fw-semibold">Booked On</th>
                            <td>${booking.createdAt}</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- Payment Receipt -->
            <c:if test="${not empty payment}">
                <div class="card bg-light border-0 rounded-4 p-3 mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <span class="fw-bold small text-muted"><i class="bi bi-receipt me-1"></i>PAYMENT RECEIPT</span>
                        <span class="badge ${payment.status == 'COMPLETED' ? 'bg-success' : 'bg-warning'} px-2 py-1">${payment.status}</span>
                    </div>
                    <div class="row g-2 small">
                        <div class="col-sm-6">
                            <span class="text-muted">Transaction ID:</span>
                            <div class="fw-bold text-dark font-monospace">${payment.transactionId}</div>
                        </div>
                        <div class="col-sm-3">
                            <span class="text-muted">Method:</span>
                            <div class="fw-bold text-dark">${payment.paymentMethod}</div>
                        </div>
                        <div class="col-sm-3">
                            <span class="text-muted">Amount:</span>
                            <div class="fw-bold text-success">₹${payment.amount}</div>
                        </div>
                    </div>
                </div>
            </c:if>

            <div class="d-flex gap-2">
                <c:if test="${booking.status == 'CONFIRMED'}">
                    <a href="${pageContext.request.contextPath}/rentals/cancel?id=${booking.id}" 
                       onclick="return confirm('Are you sure you want to cancel this reservation?');" 
                       class="btn btn-danger flex-grow-1">
                        <i class="bi bi-x-circle me-1"></i>Cancel Reservation
                    </a>
                </c:if>
                <a href="${pageContext.request.contextPath}/rentals/bookings" class="btn btn-outline-secondary px-4">Back to Reservations</a>
            </div>
        </div>
    </main>

    <!-- Bootstrap 5 Bundle with Popper JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
