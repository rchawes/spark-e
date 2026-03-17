# Spark-E - Complete Trade Business Management System

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

## 🧪 **Testing**

### **Run Tests**
```bash
# Run all tests
mvn test

# Run specific test classes
mvn test -Dtest=InvoiceServiceTest
mvn test -Dtest=JobControllerIntegrationTest
mvn test -Dtest=CustomerRepositoryTest
```

### **Test Coverage**
- **Unit Tests**: Service layer business logic
- **Integration Tests**: Controller endpoints with MockMvc
- **Repository Tests**: JPA entity operations
- **Error Handling**: Global exception handler testing

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

- **CI/CD Pipeline** with GitHub Actions
- **Advanced Analytics** and reporting features
- **Mobile App** development
- **Multi-tenant Support** for trade companies
- **Integration** with accounting software

## 💼 **Professional Portfolio**

This project demonstrates:
- **Full-stack development** capabilities
- **System architecture** and design patterns
- **Problem-solving** with domain expertise
- **Production-ready** code quality
- **Modern development** practices

## 📞 **Get In Touch**

I'm actively looking for software engineering opportunities. If you have feedback, questions, or opportunities:

**LinkedIn**: https://www.linkedin.com/in/richard-hawes-a4533399/  
**GitHub**: https://github.com/rchawes  
**Email**: rchawes@gmail.com

## 📄 **License**

© 2026 Richard Hawes. All rights reserved.

This source code is provided for viewing and educational purposes only. 
No permission is granted to use, copy, modify, distribute, or create 
derivative works from this software without explicit written permission.

For licensing inquiries: rchawes@gmail.com

---

**Thank you for checking out Spark-E!** 🎉

*A complete trade business management system built with passion and professional expertise.*

