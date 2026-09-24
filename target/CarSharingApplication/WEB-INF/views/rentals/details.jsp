<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${listing.vehicleMake} ${listing.vehicleModel} - Car Rental</title>
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
            max-width: 950px;
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
    <main class="container details-container mb-5">
        
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>${param.error}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="row g-4">
            
            <!-- Left Column: Vehicle & Listing Details -->
            <div class="col-lg-7">
                <div class="bg-white p-4 border rounded-4 shadow-sm h-100">
                    <div class="d-flex justify-content-between align-items-start mb-3">
                        <div>
                            <h2 class="fw-bold mb-1">${listing.vehicleMake} ${listing.vehicleModel}</h2>
                            <span class="text-muted"><i class="bi bi-calendar3 me-1"></i>Year ${listing.vehicleYear} • ${listing.vehicleType}</span>
                        </div>
                        <span class="badge ${listing.status == 'ACTIVE' ? 'bg-success' : 'bg-secondary'} px-3 py-2 rounded-3">${listing.status}</span>
                    </div>

                    <p class="text-muted mb-4"><i class="bi bi-geo-alt-fill text-danger me-1"></i>Pickup Location: <strong>${listing.location}</strong></p>

                    <h5 class="fw-bold mb-3">Vehicle Specifications</h5>
                    <div class="table-responsive mb-4">
                        <table class="table table-bordered align-middle">
                            <tbody>
                                <tr>
                                    <th class="bg-light fw-semibold" style="width: 45%;">License Plate</th>
                                    <td><code>${listing.vehiclePlate}</code></td>
                                </tr>
                                <tr>
                                    <th class="bg-light fw-semibold">Transmission</th>
                                    <td>${listing.transmission}</td>
                                </tr>
                                <tr>
                                    <th class="bg-light fw-semibold">Fuel Type</th>
                                    <td>${listing.fuelType}</td>
                                </tr>
                                <tr>
                                    <th class="bg-light fw-semibold">Seating Capacity</th>
                                    <td>${listing.seatingCapacity} Passengers</td>
                                </tr>
                                <tr>
                                    <th class="bg-light fw-semibold">Daily Rental Rate</th>
                                    <td class="text-success fw-bold">₹${listing.pricePerDay} / day</td>
                                </tr>
                                <tr>
                                    <th class="bg-light fw-semibold">Security Deposit</th>
                                    <td>₹${listing.securityDeposit} (Refundable upon return)</td>
                                </tr>
                                <tr>
                                    <th class="bg-light fw-semibold">Availability Window</th>
                                    <td>${listing.availableFrom} to ${listing.availableUntil}</td>
                                </tr>
                                <tr>
                                    <th class="bg-light fw-semibold">Vehicle Owner</th>
                                    <td>${listing.ownerName} (Ph: ${listing.ownerPhone})</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <c:if test="${not empty listing.description}">
                        <h5 class="fw-bold mb-2">Rental Policies & Description</h5>
                        <div class="p-3 bg-light rounded-3 text-secondary small">
                            ${listing.description}
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- Right Column: Reservation & Checkout Box -->
            <div class="col-lg-5">
                <div class="bg-white p-4 border rounded-4 shadow-sm h-100 d-flex flex-column justify-content-between">
                    <div>
                        <div class="d-flex justify-content-between align-items-baseline mb-3">
                            <div>
                                <span class="h3 fw-bold text-success">₹${listing.pricePerDay}</span>
                                <span class="text-muted small"> / day</span>
                            </div>
                            <span class="badge bg-light text-dark border">Deposit: ₹${listing.securityDeposit}</span>
                        </div>

                        <hr class="mb-4">

                        <c:choose>
                            <c:when test="${empty sessionScope.user}">
                                <div class="p-4 bg-light rounded-4 text-center">
                                    <i class="bi bi-person-lock display-5 text-primary mb-2 d-block"></i>
                                    <h6 class="fw-bold">Login to Reserve</h6>
                                    <p class="small text-muted mb-3">Create an account or login to book this rental car.</p>
                                    <a href="${pageContext.request.contextPath}/auth/login" class="btn btn-primary-custom w-100">Sign In to Rent</a>
                                </div>
                            </c:when>

                            <c:when test="${sessionScope.user.id == listing.ownerId}">
                                <div class="p-4 bg-light rounded-4 text-center text-muted">
                                    <i class="bi bi-info-circle display-5 text-primary mb-2 d-block"></i>
                                    <h6 class="fw-bold">Your Rental Listing</h6>
                                    <p class="small mb-3">You own this vehicle. Owners cannot rent their own vehicles.</p>
                                    <a href="${pageContext.request.contextPath}/rentals/my" class="btn btn-outline-primary w-100">Manage My Listings</a>
                                </div>
                            </c:when>

                            <c:otherwise>
                                <form action="${pageContext.request.contextPath}/rentals/book" method="POST" id="bookingForm" class="needs-validation" novalidate>
                                    <input type="hidden" name="listingId" value="${listing.id}">
                                    
                                    <div class="mb-3">
                                        <label for="startDate" class="form-label fw-semibold small">Pickup Date</label>
                                        <input type="date" class="form-control" id="startDate" name="startDate" 
                                               min="${listing.availableFrom}" max="${listing.availableUntil}" required>
                                        <div class="invalid-feedback">Valid start date required.</div>
                                    </div>

                                    <div class="mb-3">
                                        <label for="endDate" class="form-label fw-semibold small">Return Date</label>
                                        <input type="date" class="form-control" id="endDate" name="endDate" 
                                               min="${listing.availableFrom}" max="${listing.availableUntil}" required>
                                        <div class="invalid-feedback">Valid return date required.</div>
                                    </div>

                                    <div class="mb-4">
                                        <label for="paymentMethod" class="form-label fw-semibold small">Payment Method</label>
                                        <select class="form-select" id="paymentMethod" name="paymentMethod" required>
                                            <option value="CARD">💳 Credit / Debit Card</option>
                                            <option value="UPI" selected>📱 UPI (GPay / PhonePe / Paytm)</option>
                                            <option value="NETBANKING">🏦 Net Banking</option>
                                        </select>
                                    </div>

                                    <!-- Dynamic Price Preview -->
                                    <div class="p-3 bg-light rounded-3 mb-4 small" id="priceSummary">
                                        <div class="d-flex justify-content-between mb-1">
                                            <span class="text-muted">Rental Days:</span>
                                            <span class="fw-bold" id="totalDaysText">Select dates</span>
                                        </div>
                                        <div class="d-flex justify-content-between mb-1">
                                            <span class="text-muted">Daily Subtotal:</span>
                                            <span class="fw-bold" id="dailyTotalText">₹0</span>
                                        </div>
                                        <div class="d-flex justify-content-between mb-2">
                                            <span class="text-muted">Refundable Deposit:</span>
                                            <span class="fw-bold text-dark">₹${listing.securityDeposit}</span>
                                        </div>
                                        <div class="border-top pt-2 d-flex justify-content-between align-items-center">
                                            <span class="fw-bold">Total Estimated:</span>
                                            <span class="h5 fw-bold text-success mb-0" id="grandTotalText">₹${listing.securityDeposit}</span>
                                        </div>
                                    </div>

                                    <button type="submit" class="btn btn-primary-custom w-100 py-2">
                                        <i class="bi bi-shield-check me-1"></i>Confirm & Reserve Car
                                    </button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="border-top pt-3 mt-4 text-center">
                        <a href="${pageContext.request.contextPath}/rentals" class="btn btn-sm btn-outline-secondary w-100">
                            <i class="bi bi-arrow-left me-1"></i>Back to All Rentals
                        </a>
                    </div>
                </div>
            </div>

        </div>
    </main>

    <!-- Bootstrap 5 Bundle with Popper JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        const ratePerDay = ${listing.pricePerDay};
        const deposit = ${listing.securityDeposit};
        const startInput = document.getElementById("startDate");
        const endInput = document.getElementById("endDate");

        function updatePricing() {
            if (!startInput || !endInput || !startInput.value || !endInput.value) return;
            const start = new Date(startInput.value);
            const end = new Date(endInput.value);
            if (end < start) {
                document.getElementById("totalDaysText").innerText = "Invalid date range";
                return;
            }
            const diffDays = Math.round((end - start) / (1000 * 60 * 60 * 24)) + 1;
            const dailyTotal = diffDays * ratePerDay;
            const grandTotal = dailyTotal + deposit;

            document.getElementById("totalDaysText").innerText = diffDays + " day(s)";
            document.getElementById("dailyTotalText").innerText = "₹" + dailyTotal;
            document.getElementById("grandTotalText").innerText = "₹" + grandTotal;
        }

        if (startInput && endInput) {
            startInput.addEventListener("change", updatePricing);
            endInput.addEventListener("change", updatePricing);
        }
    </script>
</body>
</html>
