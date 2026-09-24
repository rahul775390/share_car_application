// JavaScript Core File for Car Sharing Application

document.addEventListener("DOMContentLoaded", () => {
    console.log("Car Sharing Application initialized.");

    // Dynamic Navigation Scroll styling
    const navbar = document.querySelector(".custom-navbar");
    if (navbar) {
        window.addEventListener("scroll", () => {
            if (window.scrollY > 50) {
                navbar.style.boxShadow = "var(--shadow-md)";
                navbar.style.background = "rgba(255, 255, 255, 0.95)";
            } else {
                navbar.style.boxShadow = "none";
                navbar.style.background = "rgba(255, 255, 255, 0.85)";
            }
        });
    }

    // Client-side Input Validations
    setupValidation();
});

function setupValidation() {
    const forms = document.querySelectorAll(".needs-validation");
    Array.from(forms).forEach(form => {
        form.addEventListener("submit", event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add("was-validated");
        }, false);
    });
}

// Display simple bootstrap toast alerts dynamically
function showToast(message, type = "success") {
    const toastContainer = document.getElementById("toast-container");
    if (!toastContainer) {
        // Create container if not exists
        const container = document.createElement("div");
        container.id = "toast-container";
        container.className = "toast-container position-fixed bottom-0 end-0 p-3";
        document.body.appendChild(container);
    }

    const toastId = "toast_" + Date.now();
    const bgClass = type === "success" ? "bg-success" : type === "danger" ? "bg-danger" : "bg-warning";
    
    const toastHtml = `
        <div id="${toastId}" class="toast align-items-center text-white ${bgClass} border-0" role="alert" aria-live="assertive" aria-atomic="true" data-bs-delay="4000">
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;

    document.getElementById("toast-container").insertAdjacentHTML("beforeend", toastHtml);
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement);
    toast.show();

    toastElement.addEventListener("hidden.bs.toast", () => {
        toastElement.remove();
    });
}
