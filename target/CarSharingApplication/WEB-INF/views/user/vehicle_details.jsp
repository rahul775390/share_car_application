<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vehicle Details - CarSharing</title>
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
            max-width: 700px;
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
                </ul>
                <div class="d-flex align-items-center">
                    <div class="dropdown">
                        <button class="btn btn-outline-primary dropdown-toggle" type="button" id="profileDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="bi bi-person-circle me-1"></i>Hello, ${sessionScope.user.name}
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0 mt-2" aria-labelledby="profileDropdown">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person me-2"></i>My Profile</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/vehicles"><i class="bi bi-truck me-2"></i>My Vehicles</a></li>
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

    <!-- Details Container -->
    <main class="container details-container mb-5">
        <div class="bg-white p-4 border rounded-4 shadow-sm">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h3 class="fw-bold mb-0">${vehicle.make} ${vehicle.model}</h3>
                <c:choose>
                    <c:when test="${vehicle.status == 'AVAILABLE'}">
                        <span class="badge bg-success px-3 py-2 rounded-3">AVAILABLE</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge bg-danger px-3 py-2 rounded-3">UNAVAILABLE</span>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="table-responsive mb-4">
                <table class="table table-bordered align-middle">
                    <tbody>
                        <tr>
                            <th class="bg-light fw-semibold" style="width: 40%;">Registration Number</th>
                            <td class="fw-bold text-primary">${vehicle.licensePlate}</td>
                        </tr>
                        <tr>
                            <th class="bg-light fw-semibold">Brand / Make</th>
                            <td>${vehicle.make}</td>
                        </tr>
                        <tr>
                            <th class="bg-light fw-semibold">Model</th>
                            <td>${vehicle.model}</td>
                        </tr>
                        <tr>
                            <th class="bg-light fw-semibold">Manufacturing Year</th>
                            <td>${vehicle.year}</td>
                        </tr>
                        <tr>
                            <th class="bg-light fw-semibold">Vehicle Type</th>
                            <td>${vehicle.type}</td>
                        </tr>
                        <tr>
                            <th class="bg-light fw-semibold">Fuel Type</th>
                            <td>${vehicle.fuelType}</td>
                        </tr>
                        <tr>
                            <th class="bg-light fw-semibold">Transmission</th>
                            <td>${vehicle.transmission}</td>
                        </tr>
                        <tr>
                            <th class="bg-light fw-semibold">Color</th>
                            <td>${vehicle.color}</td>
                        </tr>
                        <tr>
                            <th class="bg-light fw-semibold">Seating Capacity</th>
                            <td>${vehicle.seatingCapacity} Passengers</td>
                        </tr>
                        <tr>
                            <th class="bg-light fw-semibold">Date Registered</th>
                            <td>${vehicle.createdAt}</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div class="d-flex gap-2">
                <c:if test="${sessionScope.user.id == vehicle.ownerId}">
                    <a href="${pageContext.request.contextPath}/vehicles/edit?id=${vehicle.id}" class="btn btn-primary-custom flex-grow-1">
                        <i class="bi bi-pencil me-2"></i>Edit Vehicle
                    </a>
                </c:if>
                <a href="${pageContext.request.contextPath}/vehicles" class="btn btn-outline-secondary px-4">Back to List</a>
            </div>
        </div>
    </main>

    <!-- Bootstrap 5 Bundle with Popper JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
