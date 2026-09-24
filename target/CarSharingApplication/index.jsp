<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Car Sharing Application - Home</title>
    
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
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
                    <c:choose>
                        <c:when test="${not empty sessionScope.user}">
                            <div class="dropdown">
                                <button class="btn btn-outline-primary dropdown-toggle" type="button" id="profileDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                                    <i class="bi bi-person-circle me-1"></i>Hello, ${sessionScope.user.name}
                                </button>
                                <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0 mt-2" aria-labelledby="profileDropdown">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person me-2"></i>My Profile</a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/vehicles"><i class="bi bi-truck me-2"></i>My Vehicles</a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/rides"><i class="bi bi-compass me-2"></i>My Offered Rides</a></li>
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

    <!-- Hero Section -->
    <header class="hero-section">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-lg-6">
                    <h1 class="hero-title">
                        One Platform.<br>Two Ways to <span style="color: var(--primary-color);">Move.</span>
                    </h1>
                    <p class="hero-subtitle">
                        CoShare bridges the gap between co-riding and car rental. Share a ride with neighbors to split commute costs, or rent an entire verified vehicle for your personal weekend getaway.
                    </p>
                    <div class="d-flex flex-wrap gap-3">
                        <a href="${pageContext.request.contextPath}/rides/search" class="btn btn-primary-custom">
                            <i class="bi bi-compass me-2"></i>Find Shared Rides
                        </a>
                        <a href="${pageContext.request.contextPath}/rentals" class="btn btn-secondary-custom">
                            <i class="bi bi-key-fill me-2"></i>Browse Rental Cars
                        </a>
                    </div>
                </div>
                <div class="col-lg-6 d-none d-lg-block text-center float-animation">
                    <!-- Hero SVG Art representing travel and vehicles -->
                    <svg viewBox="0 0 500 400" width="100%" height="auto" xmlns="http://www.w3.org/2000/svg">
                        <defs>
                            <linearGradient id="grad1" x1="0%" y1="0%" x2="100%" y2="100%">
                                <stop offset="0%" style="stop-color:hsl(230, 85%, 60%);stop-opacity:1" />
                                <stop offset="100%" style="stop-color:hsl(260, 85%, 60%);stop-opacity:1" />
                            </linearGradient>
                            <linearGradient id="grad2" x1="0%" y1="0%" x2="0%" y2="100%">
                                <stop offset="0%" style="stop-color:#ffffff;stop-opacity:0.8" />
                                <stop offset="100%" style="stop-color:#e6e9fa;stop-opacity:0.4" />
                            </linearGradient>
                        </defs>
                        <!-- Ground / Roads -->
                        <path d="M 50 350 L 450 350" stroke="#dee2e6" stroke-width="4" stroke-linecap="round"/>
                        <path d="M 80 320 L 420 320" stroke="#e9ecef" stroke-width="2" stroke-linecap="round"/>
                        
                        <!-- Floating Circles background -->
                        <circle cx="150" cy="180" r="100" fill="url(#grad2)" />
                        <circle cx="380" cy="120" r="50" fill="rgba(99, 102, 241, 0.05)" />
                        
                        <!-- Main Car Drawing -->
                        <rect x="180" y="240" width="160" height="60" rx="15" fill="url(#grad1)" />
                        <path d="M 200 240 L 220 180 L 300 180 L 320 240 Z" fill="url(#grad1)" opacity="0.8" />
                        <rect x="225" y="190" width="30" height="40" fill="#ffffff" opacity="0.9" />
                        <rect x="265" y="190" width="35" height="40" fill="#ffffff" opacity="0.9" />
                        <!-- Wheels -->
                        <circle cx="215" cy="300" r="22" fill="#212529" />
                        <circle cx="215" cy="300" r="10" fill="#f8f9fa" />
                        <circle cx="305" cy="300" r="22" fill="#212529" />
                        <circle cx="305" cy="300" r="10" fill="#f8f9fa" />
                        <!-- Details -->
                        <circle cx="330" cy="260" r="6" fill="#ffc107" /> <!-- Headlight -->
                    </svg>
                </div>
            </div>
        </div>
    </header>

    <!-- Services Overview -->
    <section class="py-5 bg-white">
        <div class="container py-5">
            <div class="text-center mb-5">
                <h2 class="h1 mb-3">Designed for Flexibility and Trust</h2>
                <p class="text-muted mx-auto" style="max-width: 600px;">
                    Whether commuting daily or heading out for the weekend, explore our dual-purpose transportation platform.
                </p>
            </div>
            <div class="row g-4 justify-content-center">
                <div class="col-md-4">
                    <div class="feature-card text-center p-4">
                        <div class="feature-icon-wrapper mx-auto mb-3">
                            <i class="bi bi-compass text-primary"></i>
                        </div>
                        <h4 class="fw-bold mb-2">Commute Ride Sharing</h4>
                        <p class="text-muted small mb-3">
                            Search routes, reserve seats with transactional concurrency, and split fuel costs directly with verified car owners.
                        </p>
                        <a href="${pageContext.request.contextPath}/rides/search" class="btn btn-sm btn-outline-primary">Find Rides</a>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card text-center p-4">
                        <div class="feature-icon-wrapper mx-auto mb-3">
                            <i class="bi bi-key-fill text-primary"></i>
                        </div>
                        <h4 class="fw-bold mb-2">Daily Car Rental</h4>
                        <p class="text-muted small mb-3">
                            Rent entire vehicles for flexible day-by-day use with transparent rates, security deposits, and digital receipts.
                        </p>
                        <a href="${pageContext.request.contextPath}/rentals" class="btn btn-sm btn-outline-primary">Browse Rentals</a>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card text-center p-4">
                        <div class="feature-icon-wrapper mx-auto mb-3">
                            <i class="bi bi-shield-check text-primary"></i>
                        </div>
                        <h4 class="fw-bold mb-2">Security & Verification</h4>
                        <p class="text-muted small mb-3">
                            BCrypt password hashing, session guards, administrative moderation, and community ratings keep trips dependable.
                        </p>
                        <a href="${pageContext.request.contextPath}/auth/register" class="btn btn-sm btn-outline-secondary">Join Community</a>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="custom-footer">
        <div class="container">
            <div class="row g-4">
                <div class="col-md-4">
                    <h5 class="mb-3 text-white">CarSharing</h5>
                    <p class="text-muted" style="font-size: 0.9rem;">
                        A unified platform combining ride sharing and car rentals. Authentication system uses standard secure session tracking techniques.
                    </p>
                </div>
                <div class="col-md-4 offset-md-4 text-md-end">
                    <h5 class="mb-3 text-white">Quick Links</h5>
                    <ul class="list-unstyled text-muted" style="font-size: 0.9rem;">
                        <li><a href="${pageContext.request.contextPath}/profile" class="footer-link">My Profile</a></li>
                        <li><a href="${pageContext.request.contextPath}/auth/login" class="footer-link">Member Sign In</a></li>
                        <li><a href="${pageContext.request.contextPath}/auth/register" class="footer-link">Member Register</a></li>
                    </ul>
                </div>
            </div>
            <hr class="my-4 border-secondary">
            <div class="row align-items-center">
                <div class="col-md-6 text-center text-md-start">
                    <p class="mb-0 text-muted" style="font-size: 0.85rem;">&copy; 2026 CarSharing Application. All rights reserved.</p>
                </div>
            </div>
        </div>
    </footer>

    <!-- Bootstrap 5 Bundle with Popper JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Custom JS -->
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
