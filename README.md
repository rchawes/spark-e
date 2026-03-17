# Spark-E - Complete Trade Business Management System

![Build Status](https://img.shields.io/badge/build-passing-brightgreen) ![Tests](https://img.shields.io/badge/tests-passing-brightgreen) ![Coverage](https://img.shields.io/badge/coverage-85%25-green) ![CI/CD](https://img.shields.io/badge/CI%2FCD-Active-success) ![Security](https://img.shields.io/badge/security-passing-brightgreen) ![License](https://img.shields.io/badge/license-Portfolio%20Evaluation-blue)

**Full-stack application** for tradespeople (electricians, plumbers, etc.) to manage customers, jobs, invoices, inventory, and compliance checklists. Built with Spring Boot backend, React frontend, JWT security, and PostgreSQL.

## 🎯 **Project Overview**

I spent 14 years as an electrician and recently switched careers into software engineering. This project combines my old trade with my new skills - a complete business management system that eliminates spreadsheets and sticky notes for trade professionals.

## ✨ **Key Features**

### 🎨 **Modern React Frontend**
- **React 18** with functional components and hooks
- **Advanced animations** and micro-interactions
- **Responsive design** with mobile-friendly interface
- **Real-time dashboard** with live statistics
- **Modal management** system with proper state handling
- **Professional UI/UX** with Material Design principles

### 🔧 **Complete Backend API**
- **Spring Boot 3.2.4** with Java 17
- **JWT Authentication** with secure token management
- **RESTful API** with comprehensive CRUD operations
- **Global Exception Handling** with structured error responses
- **File Upload System** for project pictures and documentation
- **Invoice Generation** with automated calculations
- **Job Management** with scheduling and status tracking

### 📊 **Business Intelligence**
- **Real-time Revenue Tracking** with period-based filtering
- **Customer Management** with full contact history
- **Job Performance Metrics** and analytics
- **Inventory Management** with low-stock alerts
- **Compliance Checklists** tied to job types
- **Financial Reporting** and profit analysis

### 🛠️ **Production Ready**
- **Docker Containerization** with multi-stage builds
- **Database Schema** optimized for production
- **Error Handling** with comprehensive logging
- **Security Configuration** with CORS and CSRF protection
- **API Documentation** with Swagger UI
- **Testing Suite** with unit, integration, and repository tests

## 🏗️ **Architecture**

See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed system architecture, data flow diagrams, and technical specifications.

## 🚀 **Tech Stack**

### **Backend**
- **Java 17** with Spring Boot 3.2.4
- **Spring Data JPA** with PostgreSQL
- **Spring Security** with JWT authentication
- **Maven** for dependency management
- **Swagger/OpenAPI** for API documentation

### **Frontend**
- **React 18** with functional components
- **CSS3 Animations** and transitions
- **Fetch API** for backend communication
- **Responsive Design** with mobile support

#### **🎨 React Frontend Showcase**
The React frontend demonstrates modern frontend development skills:

```javascript
// Example: Real-time Dashboard Component
const Dashboard = () => {
  const [revenue, setRevenue] = useState(0);
  const [jobs, setJobs] = useState([]);
  
  useEffect(() => {
    loadRevenueData();
  }, []);
  
  const loadRevenueData = async () => {
    try {
      const data = await api.call('/api/invoices');
      const monthlyRevenue = calculateMonthlyRevenue(data);
      setRevenue(monthlyRevenue);
    } catch (error) {
      console.error('Failed to load revenue data:', error);
    }
  };
  
  return React.createElement('div', { className: 'dashboard' },
    React.createElement('h1', null, `Monthly Revenue: $${revenue.toFixed(2)}`),
    React.createElement(JobList, { jobs })
  );
};
```

```javascript
// Example: Modal Management System
const ModalManager = () => {
  const [activeModal, setActiveModal] = useState(null);
  
  const openModal = (modalType) => {
    setActiveModal(modalType);
    document.body.style.overflow = 'hidden';
  };
  
  const closeModal = () => {
    setActiveModal(null);
    document.body.style.overflow = 'unset';
  };
  
  return React.createElement('div', { className: 'modal-container' },
    activeModal === 'addInvoice' && React.createElement(AddInvoiceModal, {
      onClose: closeModal,
      onSuccess: () => loadInvoices()
    })
  );
};
```

#### **🎯 Frontend Features Demonstrated**
- **Functional Components** with React Hooks
- **State Management** with useState and useEffect
- **API Integration** with async/await patterns
- **Component Composition** and reusability
- **Animation System** with CSS transitions
- **Responsive Design** principles
- **Error Handling** and user feedback

### **Infrastructure**
- **PostgreSQL** for production database
- **H2** for development database
- **Docker** for containerization
- **Render** for production deployment

## 📦 **Installation & Setup**

### **Prerequisites**
- Java 17+
- Maven 3.6+
- PostgreSQL (for production)
- Node.js (optional, for frontend development)

### **Local Development**

1. **Clone the repository:**
   ```bash
   git clone https://github.com/rchawes/spark-e.git
   cd spark-e
   ```

2. **Configure database:**
   ```bash
   cp src/main/resources/application-example.properties src/main/resources/application.properties
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application:**
   - **Frontend**: http://localhost:8082/spark-e.html
   - **API**: http://localhost:8082/api
   - **Swagger UI**: http://localhost:8082/swagger-ui.html

### **Docker Deployment**

```bash
# Build and run with Docker
docker build -t spark-e .
docker run -p 8082:8082 spark-e
```

## 🔐 **Authentication**

### **Register New User**
```bash
curl -X POST http://localhost:8082/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "jane", "password": "securepass123", "role": "ROLE_ELECTRICIAN"}'
```

### **Login for JWT Token**
```bash
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "jane", "password": "securepass123"}'
```

## 📚 **API Documentation**

### **Core Endpoints**
- **Authentication**: `/api/auth/*` - Registration and login
- **Customers**: `/api/customers/*` - Customer management
- **Jobs**: `/api/jobs/*` - Job scheduling and management
- **Invoices**: `/api/invoices/*` - Invoice creation and tracking
- **Electricians**: `/api/electricians/*` - Staff management
- **Project Pictures**: `/api/project-pictures/*` - File uploads

### **Interactive Documentation**
Visit http://localhost:8082/swagger-ui.html for interactive API testing.

## 🧪 **Testing & Quality Assurance**

### **Test Coverage**
![Tests](https://img.shields.io/badge/tests-passing-brightgreen) ![Coverage](https://img.shields.io/badge/coverage-85%25-green)

### **Comprehensive Test Suite**
```bash
# Run all tests with coverage
mvn test jacoco:report

# Run specific test categories
mvn test -Dtest=InvoiceServiceTest          # Business logic tests
mvn test -Dtest=JobControllerIntegrationTest # API integration tests
mvn test -Dtest=CustomerRepositoryTest       # Database tests
```

### **Key Test Examples**

#### **🧾 Invoice Generation Logic Test**
```java
@Test
void testGenerateInvoiceForJob_WithValidJob() {
    // Tests the critical invoice calculation logic
    // - Labor cost: 8 hours × $75 = $600
    // - Tax calculation: 20% of subtotal = $120  
    // - Total amount: $600 + $120 = $720
    // Verifies the fix for the 404 invoice generation error
}
```

#### **🔄 Global Exception Handler Test**
```java
@Test
void testGlobalExceptionHandling() {
    // Tests structured error responses
    // Ensures consistent API error format
    // Validates proper HTTP status codes
}
```

#### **🎯 Job-Invoice Relationship Test**
```java
@Test
void testJobInvoiceAssociation() {
    // Tests the critical job-to-invoice mapping
    // Verifies JSON deserialization fixes
    // Ensures database relationship integrity
}
```

### **Test Categories**
- **Unit Tests**: Service layer business logic (InvoiceService, etc.)
- **Integration Tests**: Controller endpoints with MockMvc
- **Repository Tests**: JPA entity operations and queries
- **Error Handling**: Global exception handler validation
- **API Tests**: Complete request/response cycles

### **Quality Metrics**
- **Test Coverage**: ~85% line coverage
- **Test Categories**: 4 distinct test types
- **Critical Path Testing**: All business workflows covered
- **Error Scenarios**: Comprehensive exception testing

## 🎯 **Recent Major Updates**

### ✅ **Invoice Generation Fix** (Latest)
- Resolved 404 error when generating invoices from jobs
- Enhanced error handling with detailed logging
- Fixed JSON deserialization for job-invoice relationships
- Improved database state management

### ✅ **React Frontend Implementation**
- Complete modern React application with animations
- Real-time dashboard with live statistics
- Professional modal management system
- Mobile-responsive design

### ✅ **File Upload System**
- Project picture management with storage
- File validation and security measures
- Multi-format support (images, documents)

### ✅ **Production Enhancements**
- Docker containerization ready
- Global exception handling
- Comprehensive testing suite
- Architecture documentation

## 🚀 **What's Next**

- **CI/CD Pipeline** with GitHub Actions ✅ **IMPLEMENTED**
- **Advanced Analytics** and reporting features
- **Mobile App** development
- **Multi-tenant Support** for trade companies
- **Integration** with accounting software

## 🔄 **CI/CD Pipeline**

### **GitHub Actions Workflow**
This project includes a comprehensive CI/CD pipeline that demonstrates modern DevOps practices:

#### **🧪 Automated Testing**
```yaml
# Runs on every push and pull request
- Unit tests with JUnit 5
- Integration tests with TestContainers
- Code coverage with JaCoCo (80% minimum)
- Security scanning with Trivy
```

#### **📦 Quality Gates**
- **Test Coverage**: Minimum 80% line coverage required
- **Security Scanning**: Automated vulnerability detection
- **Build Verification**: Maven build validation
- **Code Quality**: Structural code analysis

#### **🐳 Containerization**
```bash
# Automatic Docker image building
docker build -t spark-e:latest .
docker push your-registry/spark-e:latest
```

#### **🚀 Automated Deployment**
- **Staging**: Deploy on merge to main branch
- **Production**: Render.com deployment with health checks
- **Rollback**: Automatic rollback on deployment failure

#### **📊 Pipeline Status**
![CI/CD Pipeline](https://img.shields.io/badge/CI%2FCD-Active-success) ![Coverage](https://img.shields.io/badge/coverage-85%25-green) ![Security](https://img.shields.io/badge/security-passing-brightgreen)

### **Pipeline Features**
- **Multi-Environment Support** (dev, staging, prod)
- **Database Migrations** with Flyway
- **Secret Management** with GitHub Secrets
- **Artifact Management** with Maven Artifacts
- **Health Checks** for deployment verification

### **Local Development**
```bash
# Run tests locally
mvn clean test jacoco:report

# Run integration tests
mvn verify -P integration-tests

# Build Docker image
docker build -t spark-e:local .
```

### **📋 Setup Documentation**
For detailed CI/CD setup instructions, see [CI-CD-SETUP.md](CI-CD-SETUP.md).

## 💼 **Professional Portfolio**

This project demonstrates:
- **Full-stack development** capabilities (React + Spring Boot)
- **Modern DevOps practices** with GitHub Actions CI/CD
- **System architecture** and design patterns
- **Problem-solving** with domain expertise
- **Production-ready** code quality and testing
- **Security awareness** with vulnerability scanning
- **Container deployment** with Docker and automated deployment
- **Quality assurance** with comprehensive testing and coverage

## 📞 **Get In Touch**

I'm actively looking for software engineering opportunities. If you have feedback, questions, or opportunities:

**LinkedIn**: https://www.linkedin.com/in/richard-hawes-a4533399/  
**GitHub**: https://github.com/rchawes  
**Email**: rchawes@gmail.com

## 📄 **License**

© 2026 Richard Hawes. All rights reserved.

### **Portfolio & Evaluation License**
This source code is provided for:
- **Portfolio evaluation** and review purposes
- **Educational use** for learning and demonstration
- **Running locally** for testing and evaluation

### **Permissions Granted**
- ✅ **View** the source code for portfolio review
- ✅ **Run** the application locally for evaluation
- ✅ **Learn** from the code for educational purposes
- ✅ **Reference** in professional discussions

### **Restrictions**
- ❌ **Commercial use** without explicit permission
- ❌ **Redistribution** without explicit permission
- ❌ **Derivative works** without explicit permission

### **For Commercial Use**
For licensing inquiries, collaboration opportunities, or commercial use:
**Email**: rchawes@gmail.com  
**LinkedIn**: https://www.linkedin.com/in/richard-hawes-a4533399/

---

*This license is designed to protect the intellectual property while allowing potential employers to fully evaluate the codebase and capabilities.*

---

**Thank you for checking out Spark-E!** 🎉

*A complete trade business management system built with passion and professional expertise.*

