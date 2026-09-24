<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - CarSharing</title>
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
        .stat-card {
            background: white;
            border-radius: 16px;
            padding: 24px;
            border: 1px solid var(--border-color);
            transition: var(--transition);
        }
        .stat-card:hover {
            transform: translateY(-4px);
            box-shadow: var(--shadow-md);
        }
        .stat-icon {
            width: 48px;
            height: 48px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.4rem;
        }
        .nav-pills .nav-link {
            border-radius: 10px;
            font-weight: 600;
            color: var(--text-secondary);
            padding: 10px 20px;
        }
        .nav-pills .nav-link.active {
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            color: white;
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
                        <button class="btn btn-outline-danger dropdown-toggle" type="button" id="profileDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="bi bi-shield-lock-fill me-1"></i>Admin: ${sessionScope.user.name}
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0 mt-2" aria-labelledby="profileDropdown">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person me-2"></i>My Profile</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/vehicles"><i class="bi bi-truck me-2"></i>My Vehicles</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/rides"><i class="bi bi-compass me-2"></i>My Rides</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/bookings"><i class="bi bi-journal-text me-2"></i>My Bookings</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger active" href="${pageContext.request.contextPath}/admin/dashboard"><i class="bi bi-speedometer2 me-2"></i>Admin Dashboard</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-secondary" href="${pageContext.request.contextPath}/auth/logout"><i class="bi bi-box-arrow-right me-2"></i>Logout</a></li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Container -->
    <main class="container mb-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="fw-bold mb-1">Administrative Control Center</h2>
                <p class="text-muted mb-0">Platform overview, user moderation, vehicle fleet, and ride management.</p>
            </div>
            <span class="badge bg-danger-subtle text-danger px-3 py-2 rounded-pill fw-bold">
                <i class="bi bi-shield-check me-1"></i>Admin Mode
            </span>
        </div>

        <!-- Alert messages -->
        <c:if test="${param.userUpdated == 'true'}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i>User status updated successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${param.roleUpdated == 'true'}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i>User administrative role updated successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${param.rideCancelled == 'true'}">
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>Ride has been administratively cancelled.
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <!-- Metric Stat Cards -->
        <div class="row g-4 mb-4">
            <div class="col-md-3 col-sm-6">
                <div class="stat-card">
                    <div class="d-flex align-items-center justify-content-between">
                        <div>
                            <div class="text-muted small fw-semibold">TOTAL USERS</div>
                            <h3 class="fw-bold mb-0 mt-1">${stats.totalUsers}</h3>
                            <small class="text-success"><i class="bi bi-check-circle me-1"></i>${stats.activeUsers} active</small>
                        </div>
                        <div class="stat-icon bg-primary-subtle text-primary">
                            <i class="bi bi-people-fill"></i>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3 col-sm-6">
                <div class="stat-card">
                    <div class="d-flex align-items-center justify-content-between">
                        <div>
                            <div class="text-muted small fw-semibold">ACTIVE VEHICLES</div>
                            <h3 class="fw-bold mb-0 mt-1">${stats.totalVehicles}</h3>
                            <small class="text-muted">Registered in fleet</small>
                        </div>
                        <div class="stat-icon bg-success-subtle text-success">
                            <i class="bi bi-truck"></i>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3 col-sm-6">
                <div class="stat-card">
                    <div class="d-flex align-items-center justify-content-between">
                        <div>
                            <div class="text-muted small fw-semibold">ACTIVE RIDES</div>
                            <h3 class="fw-bold mb-0 mt-1">${stats.activeRides}</h3>
                            <small class="text-muted">${stats.totalRides} all-time posted</small>
                        </div>
                        <div class="stat-icon bg-warning-subtle text-warning">
                            <i class="bi bi-compass"></i>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3 col-sm-6">
                <div class="stat-card">
                    <div class="d-flex align-items-center justify-content-between">
                        <div>
                            <div class="text-muted small fw-semibold">TOTAL BOOKINGS</div>
                            <h3 class="fw-bold mb-0 mt-1">${stats.totalRideBookings}</h3>
                            <small class="text-success fw-bold">₹${stats.totalRideRevenue} revenue</small>
                        </div>
                        <div class="stat-icon bg-info-subtle text-info">
                            <i class="bi bi-journal-check"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Navigation Tabs -->
        <ul class="nav nav-pills mb-4 gap-2" id="adminTabs" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" id="users-tab" data-bs-toggle="pill" data-bs-target="#users-pane" type="button" role="tab"><i class="bi bi-person-gear me-2"></i>User Management (${users.size()})</button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="vehicles-tab" data-bs-toggle="pill" data-bs-target="#vehicles-pane" type="button" role="tab"><i class="bi bi-car-front me-2"></i>Vehicles Fleet (${vehicles.size()})</button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="rides-tab" data-bs-toggle="pill" data-bs-target="#rides-pane" type="button" role="tab"><i class="bi bi-map me-2"></i>Rides Overview (${rides.size()})</button>
            </li>
        </ul>

        <!-- Tab Contents -->
        <div class="tab-content" id="adminTabsContent">
            
            <!-- Users Tab -->
            <div class="tab-pane fade show active" id="users-pane" role="tabpanel">
                <div class="card border rounded-4 shadow-sm bg-white">
                    <div class="card-body p-4">
                        <h5 class="fw-bold mb-3">All Registered Users</h5>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>Name</th>
                                        <th>Email</th>
                                        <th>Phone</th>
                                        <th>City</th>
                                        <th>Role</th>
                                        <th>Status</th>
                                        <th>Joined</th>
                                        <th class="text-end">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="u" items="${users}">
                                        <tr>
                                            <td>#${u.id}</td>
                                            <td class="fw-bold">${u.name}</td>
                                            <td>${u.email}</td>
                                            <td>${u.phone}</td>
                                            <td>${u.city}</td>
                                            <td>
                                                <span class="badge ${u.role == 'ADMIN' ? 'bg-danger' : 'bg-secondary'}">${u.role}</span>
                                            </td>
                                            <td>
                                                <span class="badge ${u.status == 'ACTIVE' ? 'bg-success' : 'bg-danger'}">${u.status}</span>
                                            </td>
                                            <td class="small text-muted">${u.createdAt}</td>
                                            <td class="text-end">
                                                <div class="btn-group btn-group-sm">
                                                    <c:choose>
                                                        <c:when test="${u.status == 'ACTIVE'}">
                                                            <a href="${pageContext.request.contextPath}/admin/user-status?id=${u.id}&status=SUSPENDED" class="btn btn-outline-warning" onclick="return confirm('Suspend user ${u.name}?');">Suspend</a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <a href="${pageContext.request.contextPath}/admin/user-status?id=${u.id}&status=ACTIVE" class="btn btn-outline-success">Activate</a>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <c:if test="${u.role != 'ADMIN'}">
                                                        <a href="${pageContext.request.contextPath}/admin/user-role?id=${u.id}&role=ADMIN" class="btn btn-outline-danger" onclick="return confirm('Promote ${u.name} to ADMIN?');">Promote</a>
                                                    </c:if>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Vehicles Tab -->
            <div class="tab-pane fade" id="vehicles-pane" role="tabpanel">
                <div class="card border rounded-4 shadow-sm bg-white">
                    <div class="card-body p-4">
                        <h5 class="fw-bold mb-3">All Vehicles in Registry</h5>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>Vehicle</th>
                                        <th>License Plate</th>
                                        <th>Owner</th>
                                        <th>Type / Fuel</th>
                                        <th>Seats</th>
                                        <th>Status</th>
                                        <th>Added</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="v" items="${vehicles}">
                                        <tr>
                                            <td>#${v.id}</td>
                                            <td class="fw-bold">${v.make} ${v.model} (${v.year})</td>
                                            <td><code>${v.licensePlate}</code></td>
                                            <td>${v.ownerName != null ? v.ownerName : 'Owner #' += v.ownerId}</td>
                                            <td>${v.type} • ${v.fuelType}</td>
                                            <td>${v.seatingCapacity} seats</td>
                                            <td>
                                                <span class="badge ${v.status == 'AVAILABLE' ? 'bg-success' : v.status == 'UNAVAILABLE' ? 'bg-warning' : 'bg-secondary'}">${v.status}</span>
                                            </td>
                                            <td class="small text-muted">${v.createdAt}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Rides Tab -->
            <div class="tab-pane fade" id="rides-pane" role="tabpanel">
                <div class="card border rounded-4 shadow-sm bg-white">
                    <div class="card-body p-4">
                        <h5 class="fw-bold mb-3">All Posted Commute Rides</h5>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>Route</th>
                                        <th>Driver</th>
                                        <th>Vehicle</th>
                                        <th>Date & Time</th>
                                        <th>Available</th>
                                        <th>Fare</th>
                                        <th>Status</th>
                                        <th class="text-end">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="r" items="${rides}">
                                        <tr>
                                            <td>#${r.id}</td>
                                            <td class="fw-bold text-primary">${r.source} ➔ ${r.destination}</td>
                                            <td>${r.driverName} (${r.driverPhone})</td>
                                            <td>${r.vehicleMake} ${r.vehicleModel} (${r.vehiclePlate})</td>
                                            <td>${r.rideDate} at ${r.rideTime}</td>
                                            <td>${r.availableSeats} of ${r.totalSeats} seats</td>
                                            <td class="fw-bold text-success">₹${r.pricePerSeat}</td>
                                            <td>
                                                <span class="badge ${r.status == 'ACTIVE' ? 'bg-success' : r.status == 'FULL' ? 'bg-warning' : 'bg-secondary'}">${r.status}</span>
                                            </td>
                                            <td class="text-end">
                                                <c:if test="${r.status == 'ACTIVE' || r.status == 'FULL'}">
                                                    <a href="${pageContext.request.contextPath}/admin/cancel-ride?id=${r.id}" onclick="return confirm('Administratively cancel ride #${r.id}?');" class="btn btn-sm btn-outline-danger">Cancel</a>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
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
