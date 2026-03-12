// Spark-E Frontend Application
class SparkEApp {
    constructor() {
        this.apiBase = 'http://localhost:8080/api';
        this.token = localStorage.getItem('jwtToken');
        this.currentUser = null;
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.checkAuthStatus();
    }

    setupEventListeners() {
        console.log('Setting up event listeners');
        
        // Check if elements exist
        const loginBtn = document.getElementById('loginBtn');
        const registerBtn = document.getElementById('registerBtn');
        
        console.log('Login button found:', loginBtn);
        console.log('Register button found:', registerBtn);
        
        // Login/Register buttons
        if (loginBtn) {
            console.log('Adding login button listener');
            loginBtn.addEventListener('click', () => {
                console.log('Login button clicked');
                this.showLoginModal();
            });
        } else {
            console.error('Login button not found!');
        }
        
        if (registerBtn) {
            console.log('Adding register button listener');
            registerBtn.addEventListener('click', () => {
                console.log('Register button clicked');
                this.showRegisterModal();
            });
        } else {
            console.error('Register button not found!');
        }
        
        // Modal close buttons
        const closeLoginModal = document.getElementById('closeLoginModal');
        const closeRegisterModal = document.getElementById('closeRegisterModal');
        
        console.log('Close login modal found:', closeLoginModal);
        console.log('Close register modal found:', closeRegisterModal);
        
        if (closeLoginModal) {
            closeLoginModal.addEventListener('click', () => this.hideLoginModal());
        }
        if (closeRegisterModal) {
            closeRegisterModal.addEventListener('click', () => this.hideRegisterModal());
        }
        
        // Forms
        const loginForm = document.getElementById('loginForm');
        const registerForm = document.getElementById('registerForm');
        
        console.log('Login form found:', loginForm);
        console.log('Register form found:', registerForm);
        
        if (loginForm) {
            loginForm.addEventListener('submit', (e) => this.handleLogin(e));
        }
        if (registerForm) {
            registerForm.addEventListener('submit', (e) => this.handleRegister(e));
        }
        
        // Close modal on backdrop click
        const loginModal = document.getElementById('loginModal');
        const registerModal = document.getElementById('registerModal');
        
        if (loginModal) {
            loginModal.addEventListener('click', (e) => {
                if (e.target.id === 'loginModal') {
                    this.hideLoginModal();
                }
            });
        }
        
        if (registerModal) {
            registerModal.addEventListener('click', (e) => {
                if (e.target.id === 'registerModal') {
                    this.hideRegisterModal();
                }
            });
        }
        
        // Dashboard action buttons
        const newJobBtn = document.getElementById('newJobBtn');
        const addClientBtn = document.getElementById('addClientBtn');
        const createInvoiceBtn = document.getElementById('createInvoiceBtn');
        
        console.log('Action buttons found:', { newJobBtn, addClientBtn, createInvoiceBtn });
        
        if (newJobBtn) {
            newJobBtn.addEventListener('click', () => {
                console.log('New Job button clicked');
                this.showJobModal();
            });
        }
        
        if (addClientBtn) {
            addClientBtn.addEventListener('click', () => {
                console.log('Add Client button clicked');
                this.showAddClientModal();
            });
        }
        
        if (createInvoiceBtn) {
            createInvoiceBtn.addEventListener('click', () => {
                console.log('Create Invoice button clicked');
                this.showCreateInvoiceModal();
            });
        }
        
        // View data buttons
        const viewClientsBtn = document.getElementById('viewClientsBtn');
        const viewJobsBtn = document.getElementById('viewJobsBtn');
        const viewInvoicesBtn = document.getElementById('viewInvoicesBtn');
        const viewRevenueBtn = document.getElementById('viewRevenueBtn');
        
        console.log('View buttons found:', { viewClientsBtn, viewJobsBtn, viewInvoicesBtn, viewRevenueBtn });
        
        if (viewClientsBtn) {
            viewClientsBtn.addEventListener('click', () => {
                console.log('View Clients button clicked');
                this.showClientsModal();
            });
        }
        
        if (viewJobsBtn) {
            viewJobsBtn.addEventListener('click', () => {
                console.log('View Jobs button clicked');
                this.showJobsModal();
            });
        }
        
        if (viewInvoicesBtn) {
            viewInvoicesBtn.addEventListener('click', () => {
                console.log('View Invoices button clicked');
                this.showInvoicesModal();
            });
        }
        
        if (viewRevenueBtn) {
            viewRevenueBtn.addEventListener('click', () => {
                console.log('View Revenue button clicked');
                this.showRevenueModal();
            });
        }
        
        // Client modal close button
        const closeClientModal = document.getElementById('closeClientModal');
        if (closeClientModal) {
            closeClientModal.addEventListener('click', () => this.hideClientModal());
        }
        
        // Client form
        const clientForm = document.getElementById('clientForm');
        if (clientForm) {
            clientForm.addEventListener('submit', (e) => this.handleAddClient(e));
        }
        
        // Close client modal on backdrop click
        const clientModal = document.getElementById('clientModal');
        if (clientModal) {
            clientModal.addEventListener('click', (e) => {
                if (e.target.id === 'clientModal') {
                    this.hideClientModal();
                }
            });
        }
        
        // Job modal close button
        const closeJobModal = document.getElementById('closeJobModal');
        if (closeJobModal) {
            closeJobModal.addEventListener('click', () => this.hideJobModal());
        }
        
        // Job form
        const jobForm = document.getElementById('jobForm');
        if (jobForm) {
            jobForm.addEventListener('submit', (e) => this.handleAddJob(e));
        }
        
        // Close job modal on backdrop click
        const jobModal = document.getElementById('jobModal');
        if (jobModal) {
            jobModal.addEventListener('click', (e) => {
                if (e.target.id === 'jobModal') {
                    this.hideJobModal();
                }
            });
        }
        
        // View modal close buttons
        const closeClientsModal = document.getElementById('closeClientsModal');
        const closeJobsModal = document.getElementById('closeJobsModal');
        const closeInvoicesModal = document.getElementById('closeInvoicesModal');
        const closeRevenueModal = document.getElementById('closeRevenueModal');
        
        if (closeClientsModal) {
            closeClientsModal.addEventListener('click', () => this.hideClientsModal());
        }
        if (closeJobsModal) {
            closeJobsModal.addEventListener('click', () => this.hideJobsModal());
        }
        if (closeInvoicesModal) {
            closeInvoicesModal.addEventListener('click', () => this.hideInvoicesModal());
        }
        if (closeRevenueModal) {
            closeRevenueModal.addEventListener('click', () => this.hideRevenueModal());
        }
    }

    // Authentication methods
    async handleLogin(e) {
        e.preventDefault();
        const username = document.getElementById('loginUsername').value;
        const password = document.getElementById('loginPassword').value;

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

    async handleRegister(e) {
        e.preventDefault();
        
        const username = document.getElementById('registerUsername').value;
        const password = document.getElementById('registerPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const role = document.getElementById('userRole').value;

        // Validate passwords match
        if (password !== confirmPassword) {
            this.showToast('Passwords do not match!', 'error');
            return;
        }

        try {
            const response = await this.apiCall('/auth/register', {
                method: 'POST',
                body: JSON.stringify({ username, password, role })
            });

            this.hideRegisterModal();
            this.showToast('Registration successful! Please login.', 'success');
            
            // Auto-fill login form and show login modal
            document.getElementById('loginUsername').value = username;
            this.showLoginModal();
            
        } catch (error) {
            this.showToast('Registration failed: ' + error.message, 'error');
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
        document.getElementById('registerModal').classList.remove('hidden');
        document.getElementById('registerModal').classList.add('flex');
    }

    hideRegisterModal() {
        document.getElementById('registerModal').classList.add('hidden');
        document.getElementById('registerModal').classList.remove('flex');
        document.getElementById('registerForm').reset();
    }

    showAddClientModal() {
        document.getElementById('clientModal').classList.remove('hidden');
        document.getElementById('clientModal').classList.add('flex');
    }

    hideClientModal() {
        document.getElementById('clientModal').classList.add('hidden');
        document.getElementById('clientModal').classList.remove('flex');
        document.getElementById('clientForm').reset();
    }

    showClientsModal() {
        document.getElementById('clientsModal').classList.remove('hidden');
        document.getElementById('clientsModal').classList.add('flex');
        this.loadClients();
    }

    hideClientsModal() {
        document.getElementById('clientsModal').classList.add('hidden');
        document.getElementById('clientsModal').classList.remove('flex');
    }

    showJobsModal() {
        document.getElementById('jobsModal').classList.remove('hidden');
        document.getElementById('jobsModal').classList.add('flex');
        this.loadJobs();
    }

    hideJobsModal() {
        document.getElementById('jobsModal').classList.add('hidden');
        document.getElementById('jobsModal').classList.remove('flex');
    }

    showInvoicesModal() {
        document.getElementById('invoicesModal').classList.remove('hidden');
        document.getElementById('invoicesModal').classList.add('flex');
        this.loadInvoices();
    }

    hideInvoicesModal() {
        document.getElementById('invoicesModal').classList.add('hidden');
        document.getElementById('invoicesModal').classList.remove('flex');
    }

    showRevenueModal() {
        document.getElementById('revenueModal').classList.remove('hidden');
        document.getElementById('revenueModal').classList.add('flex');
        this.loadRevenue();
    }

    hideRevenueModal() {
        document.getElementById('revenueModal').classList.add('hidden');
        document.getElementById('revenueModal').classList.remove('flex');
    }

    showCreateInvoiceModal() {
        this.showToast('Create Invoice feature - This would open a form to create new invoices', 'info');
    }

    async loadClients() {
        try {
            const clients = await this.apiCall('/customers');
            const clientsList = document.getElementById('clientsList');
            clientsList.innerHTML = clients.map(client => `
                <div class="bg-gray-50 p-4 rounded-lg border border-gray-200">
                    <div class="flex justify-between items-start">
                        <div>
                            <h4 class="font-semibold text-lg">${client.firstName} ${client.lastName}</h4>
                            <p class="text-gray-600 text-sm">${client.email}</p>
                            <p class="text-gray-600 text-sm">${client.phone}</p>
                            <p class="text-gray-600 text-sm">${client.address}</p>
                        </div>
                        <div class="flex space-x-2">
                            <button onclick="window.sparkEApp.editClient(${client.id})" class="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded text-sm">Edit</button>
                            <button onclick="window.sparkEApp.deleteClient(${client.id})" class="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded text-sm">Delete</button>
                        </div>
                    </div>
                </div>
            `).join('');
        } catch (error) {
            this.showToast('Failed to load clients: ' + error.message, 'error');
        }
    }

    async loadJobs() {
        try {
            const jobs = await this.apiCall('/jobs');
            const jobsList = document.getElementById('jobsList');
            jobsList.innerHTML = jobs.map(job => `
                <div class="bg-gray-50 p-4 rounded-lg border border-gray-200">
                    <div class="flex justify-between items-start">
                        <div>
                            <h4 class="font-semibold text-lg">${job.description}</h4>
                            <p class="text-gray-600 text-sm">Status: <span class="px-2 py-1 rounded text-xs font-semibold ${job.status === 'COMPLETED' ? 'bg-green-100 text-green-800' : job.status === 'IN_PROGRESS' ? 'bg-yellow-100 text-yellow-800' : 'bg-blue-100 text-blue-800'}">${job.status}</span></p>
                            <p class="text-gray-600 text-sm">Hours: ${job.hoursWorked || 0}</p>
                        </div>
                        <div class="flex space-x-2">
                            <button onclick="window.sparkEApp.editJob(${job.id})" class="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded text-sm">Edit</button>
                            <button onclick="window.sparkEApp.deleteJob(${job.id})" class="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded text-sm">Delete</button>
                        </div>
                    </div>
                </div>
            `).join('');
        } catch (error) {
            this.showToast('Failed to load jobs: ' + error.message, 'error');
        }
    }

    async loadInvoices() {
        try {
            const invoices = await this.apiCall('/invoices');
            const invoicesList = document.getElementById('invoicesList');
            invoicesList.innerHTML = invoices.map(invoice => `
                <div class="bg-gray-50 p-4 rounded-lg border border-gray-200">
                    <div class="flex justify-between items-start">
                        <div>
                            <h4 class="font-semibold text-lg">Invoice #${invoice.id}</h4>
                            <p class="text-gray-600 text-sm">Amount: $${invoice.amount || '0.00'}</p>
                            <p class="text-gray-600 text-sm">Status: <span class="px-2 py-1 rounded text-xs font-semibold ${invoice.status === 'PAID' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'}">${invoice.status}</span></p>
                        </div>
                        <div class="flex space-x-2">
                            <button onclick="window.sparkEApp.editInvoice(${invoice.id})" class="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded text-sm">Edit</button>
                            <button onclick="window.sparkEApp.deleteInvoice(${invoice.id})" class="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded text-sm">Delete</button>
                        </div>
                    </div>
                </div>
            `).join('');
        } catch (error) {
            this.showToast('Failed to load invoices: ' + error.message, 'error');
        }
    }

    async loadRevenue() {
        try {
            const stats = await this.apiCall('/dashboard/stats');
            const revenueContent = document.getElementById('revenueContent');
            revenueContent.innerHTML = `
                <div class="grid md:grid-cols-2 gap-6">
                    <div class="bg-blue-50 p-6 rounded-lg">
                        <h4 class="font-semibold text-lg text-blue-800">Total Revenue</h4>
                        <p class="text-3xl font-bold text-blue-600">$${stats.monthlyRevenue || '0.00'}</p>
                    </div>
                    <div class="bg-green-50 p-6 rounded-lg">
                        <h4 class="font-semibold text-lg text-green-800">Paid Invoices</h4>
                        <p class="text-3xl font-bold text-green-600">${stats.totalClients || 0}</p>
                    </div>
                </div>
                <div class="mt-6 bg-gray-100 p-4 rounded-lg">
                    <h4 class="font-semibold text-lg text-gray-800">Revenue Breakdown</h4>
                    <p class="text-gray-600">Active Jobs: ${stats.activeJobs || 0}</p>
                    <p class="text-gray-600">Pending Invoices: ${stats.pendingInvoices || 0}</p>
                </div>
            `;
        } catch (error) {
            this.showToast('Failed to load revenue data: ' + error.message, 'error');
        }
    }

    hideJobModal() {
        document.getElementById('jobModal').classList.add('hidden');
        document.getElementById('jobModal').classList.remove('flex');
        document.getElementById('jobForm').reset();
    }

    async handleAddClient(e) {
        e.preventDefault();
        
        const firstName = document.getElementById('clientFirstName').value;
        const lastName = document.getElementById('clientLastName').value;
        const email = document.getElementById('clientEmail').value;
        const phone = document.getElementById('clientPhone').value;
        const address = document.getElementById('clientAddress').value;

        try {
            const response = await this.apiCall('/customers', {
                method: 'POST',
                body: JSON.stringify({ firstName, lastName, email, phone, address })
            });

            this.hideClientModal();
            this.showToast('Client added successfully!', 'success');
            
            // Refresh dashboard stats
            this.loadDashboardData();
            
        } catch (error) {
            this.showToast('Failed to add client: ' + error.message, 'error');
        }
    }

    async handleAddJob(e) {
        e.preventDefault();
        
        const description = document.getElementById('jobDescription').value;
        const status = document.getElementById('jobStatus').value;
        const hoursWorked = parseFloat(document.getElementById('jobHours').value) || 0;

        try {
            const response = await this.apiCall('/jobs', {
                method: 'POST',
                body: JSON.stringify({ description, status, hoursWorked })
            });

            this.hideJobModal();
            this.showToast('Job created successfully!', 'success');
            
            // Refresh dashboard stats
            this.loadDashboardData();
            
        } catch (error) {
            this.showToast('Failed to create job: ' + error.message, 'error');
        }
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
            // Use mock data for demo since endpoints don't exist yet
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
