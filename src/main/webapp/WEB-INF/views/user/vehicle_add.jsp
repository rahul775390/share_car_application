<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Vehicle - CarSharing</title>
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
            max-width: 600px;
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

    <!-- Main Form Container -->
    <main class="container form-container mb-5">
        <div class="bg-white p-4 border rounded-4 shadow-sm">
            <h3 class="fw-bold mb-1">Add Vehicle</h3>
            <p class="text-muted mb-4">Provide specifications to list your vehicle.</p>

            <!-- Error Banner -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger d-flex align-items-center" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>
                    <div>${error}</div>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/vehicles/add" method="POST" class="needs-validation" novalidate>
                <div class="row g-3">
                    <div class="col-md-6">
                        <label for="make" class="form-label">Brand / Make</label>
                        <input type="text" class="form-control" id="make" name="make" placeholder="e.g. Toyota" required>
                        <div class="invalid-feedback">Please enter vehicle brand.</div>
                    </div>
                    <div class="col-md-6">
                        <label for="model" class="form-label">Model</label>
                        <input type="text" class="form-control" id="model" name="model" placeholder="e.g. Camry" required>
                        <div class="invalid-feedback">Please enter vehicle model.</div>
                    </div>
                    <div class="col-md-6">
                        <label for="licensePlate" class="form-label">Registration Number</label>
                        <input type="text" class="form-control" id="licensePlate" name="licensePlate" placeholder="e.g. MH-12-AB-1234" required>
                        <div class="invalid-feedback">Please enter registration number.</div>
                    </div>
                    <div class="col-md-6">
                        <label for="year" class="form-label">Manufacturing Year</label>
                        <input type="number" class="form-control" id="year" name="year" placeholder="e.g. 2022" min="1900" max="2027" required>
                        <div class="invalid-feedback">Please enter a valid year.</div>
                    </div>
                    <div class="col-md-6">
                        <label for="type" class="form-label">Vehicle Type</label>
                        <select class="form-select" id="type" name="type" required>
                            <option value="">Select Type</option>
                            <option value="Sedan">Sedan</option>
                            <option value="SUV">SUV</option>
                            <option value="Hatchback">Hatchback</option>
                            <option value="Crossover">Crossover</option>
                            <option value="Van">Van</option>
                        </select>
                        <div class="invalid-feedback">Please select a type.</div>
                    </div>
                    <div class="col-md-6">
                        <label for="fuelType" class="form-label">Fuel Type</label>
                        <select class="form-select" id="fuelType" name="fuelType" required>
                            <option value="">Select Fuel</option>
                            <option value="Petrol">Petrol</option>
                            <option value="Diesel">Diesel</option>
                            <option value="Electric">Electric</option>
                            <option value="CNG">CNG</option>
                            <option value="Hybrid">Hybrid</option>
                        </select>
                        <div class="invalid-feedback">Please select a fuel type.</div>
                    </div>
                    <div class="col-md-6">
                        <label for="transmission" class="form-label">Transmission</label>
                        <select class="form-select" id="transmission" name="transmission" required>
                            <option value="">Select Transmission</option>
                            <option value="Manual">Manual</option>
                            <option value="Automatic">Automatic</option>
                        </select>
                        <div class="invalid-feedback">Please select a transmission type.</div>
                    </div>
                    <div class="col-md-6">
                        <label for="seatingCapacity" class="form-label">Seating Capacity</label>
                        <input type="number" class="form-control" id="seatingCapacity" name="seatingCapacity" placeholder="e.g. 5" min="1" max="20" required>
                        <div class="invalid-feedback">Please enter capacity (1-20).</div>
                    </div>
                    <div class="col-12">
                        <label for="color" class="form-label">Color</label>
                        <input type="text" class="form-control" id="color" name="color" placeholder="e.g. Metallic Black" required>
                        <div class="invalid-feedback">Please enter vehicle color.</div>
                    </div>
                </div>

                <div class="mt-4 d-flex gap-2">
                    <button type="submit" class="btn btn-primary-custom flex-grow-1">Save Vehicle</button>
                    <a href="${pageContext.request.contextPath}/vehicles" class="btn btn-outline-secondary px-4">Cancel</a>
                </div>
            </form>
        </div>
    </main>

    <!-- Bootstrap 5 Bundle with Popper JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
