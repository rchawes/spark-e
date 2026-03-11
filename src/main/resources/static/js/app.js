// Spark-E Frontend Application
class SparkEApp {
    constructor() {
        this.apiBase = '/api';
        this.token = localStorage.getItem('jwtToken');
        this.currentUser = null;
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.checkAuthStatus();
    }

    setupEventListeners() {
        // Login/Register buttons
        document.getElementById('loginBtn').addEventListener('click', () => this.showLoginModal());
        document.getElementById('registerBtn').addEventListener('click', () => this.showRegisterModal());
        
        // Modal close buttons
        document.getElementById('closeLoginModal').addEventListener('click', () => this.hideLoginModal());
        
        // Forms
        document.getElementById('loginForm').addEventListener('submit', (e) => this.handleLogin(e));
        
        // Close modal on backdrop click
        document.getElementById('loginModal').addEventListener('click', (e) => {
            if (e.target.id === 'loginModal') {
                this.hideLoginModal();
            }
        });
    }

    // Authentication methods
    async handleLogin(e) {
        e.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        try {
            const response = await this.apiCall('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ username, password })
            });

            if (response.token) {
                this.token = response.token;
                localStorage.setItem('jwtToken', this.token);
                this.currentUser = response.user;
                this.hideLoginModal();
                this.showDashboard();
                this.showToast('Login successful!', 'success');
            }
        } catch (error) {
            this.showToast('Login failed: ' + error.message, 'error');
        }
    }

    async handleLogout() {
        this.token = null;
        this.currentUser = null;
        localStorage.removeItem('jwtToken');
        this.hideDashboard();
        this.showToast('Logged out successfully', 'success');
    }

    checkAuthStatus() {
        if (this.token) {
            // Verify token is still valid
            this.apiCall('/auth/verify')
                .then(response => {
                    this.currentUser = response.user;
                    this.showDashboard();
                })
                .catch(() => {
                    this.token = null;
                    localStorage.removeItem('jwtToken');
                });
        }
    }

    // UI methods
    showLoginModal() {
        document.getElementById('loginModal').classList.remove('hidden');
        document.getElementById('loginModal').classList.add('flex');
    }

    hideLoginModal() {
        document.getElementById('loginModal').classList.add('hidden');
        document.getElementById('loginModal').classList.remove('flex');
        document.getElementById('loginForm').reset();
    }

    showRegisterModal() {
        // TODO: Implement registration modal
        this.showToast('Registration feature coming soon!', 'warning');
    }

    showDashboard() {
        document.getElementById('dashboard').classList.remove('hidden');
        document.getElementById('loginBtn').style.display = 'none';
        document.getElementById('registerBtn').style.display = 'none';
        
        // Add logout button
        const nav = document.querySelector('nav .flex.items-center.space-x-4');
        const logoutBtn = document.createElement('button');
        logoutBtn.id = 'logoutBtn';
        logoutBtn.className = 'bg-red-500 hover:bg-red-700 px-4 py-2 rounded transition';
        logoutBtn.innerHTML = '<i class="fas fa-sign-out-alt mr-2"></i>Logout';
        logoutBtn.addEventListener('click', () => this.handleLogout());
        nav.appendChild(logoutBtn);
        
        this.loadDashboardData();
    }

    hideDashboard() {
        document.getElementById('dashboard').classList.add('hidden');
        document.getElementById('loginBtn').style.display = 'block';
        document.getElementById('registerBtn').style.display = 'block';
        
        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
            logoutBtn.remove();
        }
    }

    // API methods
    async apiCall(endpoint, options = {}) {
        const url = `${this.apiBase}${endpoint}`;
        const config = {
            headers: {
                'Content-Type': 'application/json',
                ...(this.token && { 'Authorization': `Bearer ${this.token}` })
            },
            ...options
        };

        const response = await fetch(url, config);
        
        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            throw new Error(error.message || `HTTP error! status: ${response.status}`);
        }

        return await response.json();
    }

    // Dashboard data loading
    async loadDashboardData() {
        try {
            // Load dashboard statistics
            const stats = await this.apiCall('/dashboard/stats');
            this.updateDashboardStats(stats);
            
            // Load recent jobs
            const jobs = await this.apiCall('/jobs/recent');
            this.displayRecentJobs(jobs);
            
        } catch (error) {
            console.error('Failed to load dashboard data:', error);
            // Use mock data for demo
            this.loadMockData();
        }
    }

    updateDashboardStats(stats) {
        document.getElementById('activeJobsCount').textContent = stats.activeJobs || 0;
        document.getElementById('totalClientsCount').textContent = stats.totalClients || 0;
        document.getElementById('pendingInvoicesCount').textContent = stats.pendingInvoices || 0;
        document.getElementById('monthlyRevenue').textContent = `$${stats.monthlyRevenue || 0}`;
    }

    displayRecentJobs(jobs) {
        // TODO: Implement recent jobs display
        console.log('Recent jobs:', jobs);
    }

    loadMockData() {
        // Mock data for demonstration
        const mockStats = {
            activeJobs: 5,
            totalClients: 23,
            pendingInvoices: 3,
            monthlyRevenue: 12500
        };
        this.updateDashboardStats(mockStats);
    }

    // Utility methods
    showToast(message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.textContent = message;
        
        document.body.appendChild(toast);
        toast.style.display = 'block';
        
        setTimeout(() => {
            toast.style.display = 'none';
            document.body.removeChild(toast);
        }, 3000);
    }

    formatCurrency(amount) {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(amount);
    }

    formatDate(dateString) {
        return new Date(dateString).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    }
}

// Initialize the app when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.sparkEApp = new SparkEApp();
});

// Export for potential module usage
if (typeof module !== 'undefined' && module.exports) {
    module.exports = SparkEApp;
}
