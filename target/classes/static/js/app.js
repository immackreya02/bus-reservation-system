// ===== BusGo - Main JavaScript File =====
// Included on every page for shared utilities and nav behavior

// ===== TOAST NOTIFICATIONS =====
function showToast(message, type = 'info') {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    const toast = document.createElement('div');
    const icons = { success: '✅', error: '❌', warning: '⚠️', info: 'ℹ️' };
    toast.className = `toast ${type}`;
    toast.innerHTML = `<span>${icons[type] || 'ℹ️'}</span><span>${message}</span>`;
    container.appendChild(toast);
    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease forwards';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

// ===== ALERT BOX =====
function showAlert(boxId, message, type = 'error') {
    const box = document.getElementById(boxId);
    if (!box) return;
    box.className = `alert alert-${type}`;
    box.textContent = message;
    box.style.display = 'block';
    setTimeout(() => box.style.display = 'none', 5000);
}

// ===== NAVBAR: Show/hide links based on auth state =====
async function initNavbar() {
    try {
        const res = await fetch('/api/me');
        const loginBtn = document.getElementById('loginBtn');
        const registerBtn = document.getElementById('registerBtn');
        const logoutBtn = document.getElementById('logoutBtn');
        const historyLink = document.getElementById('historyLink');
        const profileLink = document.getElementById('profileLink');
        const adminLink = document.getElementById('adminLink');
        const userGreeting = document.getElementById('userGreeting');

        if (res.ok) {
            const user = await res.json();
            if (loginBtn) loginBtn.style.display = 'none';
            if (registerBtn) registerBtn.style.display = 'none';
            if (logoutBtn) logoutBtn.style.display = 'inline-flex';
            if (historyLink) historyLink.style.display = 'inline-block';
            if (profileLink) profileLink.style.display = 'inline-block';
            if (userGreeting) {
                userGreeting.textContent = `👋 ${user.name}`;
                userGreeting.style.display = 'inline';
            }
            if (adminLink && user.role === 'ADMIN') {
                adminLink.style.display = 'inline-block';
            }
        } else {
            if (logoutBtn) logoutBtn.style.display = 'none';
            if (historyLink) historyLink.style.display = 'none';
            if (profileLink) profileLink.style.display = 'none';
            if (adminLink) adminLink.style.display = 'none';
        }
    } catch (e) {
        // Not logged in or error — silently ignore
    }
}

// ===== HIGHLIGHT ACTIVE NAV LINK =====
function highlightActiveNav() {
    const path = window.location.pathname;
    document.querySelectorAll('.nav-link').forEach(link => {
        const href = link.getAttribute('href');
        if (href && href !== '#' && path.startsWith(href)) {
            link.classList.add('active');
        }
    });
}

// ===== ADD SLIDE-OUT ANIMATION CSS =====
(function addSlideOutCSS() {
    const style = document.createElement('style');
    style.textContent = `
        @keyframes slideOut {
            to { transform: translateX(110%); opacity: 0; }
        }
    `;
    document.head.appendChild(style);
})();

// ===== RUN ON EVERY PAGE LOAD =====
document.addEventListener('DOMContentLoaded', () => {
    initNavbar();
    highlightActiveNav();
});


