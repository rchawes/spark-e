# Spark-E Architecture Diagram

## System Overview
```
┌─────────────────────────────────────────────────────────────────────────┐
│                    Frontend (React SPA)                        │
│  ┌─────────────────────────────────────────────────────┐     │
│  │ spark-e.html (React Components)              │     │
│  │ • Dashboard, Jobs, Invoices, Clients          │     │
│  │ • Authentication, Picture Upload             │     │
│  │ • State Management (useState, useEffect)       │     │
│  └─────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────────────────┘
                           │ HTTP/HTTPS (REST API)
                           ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    Backend (Spring Boot)                       │
│  ┌─────────────────────────────────────────────────────┐     │
│  │ Controllers (REST Endpoints)               │     │
│  │ • AuthController, JobController           │     │
│  │ • InvoiceController, CustomerController     │     │
│  │ • ElectricianController, ProjectPictureController │     │
│  └─────────────────────────────────────────────────────┘     │
│  ┌─────────────────────────────────────────────────────┐     │
│  │ Services (Business Logic)                   │     │
│  │ • CustomUserDetailsService, FileStorageService  │     │
│  └─────────────────────────────────────────────────────┘     │
│  ┌─────────────────────────────────────────────────────┐     │
│  │ Repositories (Data Access)                  │     │
│  │ • JPA Repositories for all entities        │     │
│  └─────────────────────────────────────────────────────┘     │
│  ┌─────────────────────────────────────────────────────┐     │
│  │ Models (Entities)                           │     │
│  │ • User, Customer, Job, Invoice             │     │
│  │ • Electrician, ProjectPicture              │     │
│  └─────────────────────────────────────────────────────┘     │
│  ┌─────────────────────────────────────────────────────┐     │
│  │ Security (JWT + Spring Security)             │     │
│  │ • JWT Authentication, Authorization           │     │
│  └─────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    Database Layer                              │
│  ┌─────────────────────────────────────────────────────┐     │
│  │ H2 In-Memory (Development)               │     │
│  │ PostgreSQL (Production)                  │     │
│  └─────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────────────────┘
```

## Data Flow
```
1. User Authentication
   Frontend → AuthController → JWT Token → Secure API Calls

2. Business Operations
   Frontend → Controllers → Services → Repositories → Database

3. File Management
   Frontend → ProjectPictureController → FileStorageService → File System + Database

4. Invoice Generation
   Jobs → InvoiceController → PDF Generation → Client Billing
```

## Key Technologies
- **Frontend**: React 18, HTML5, CSS3, JavaScript ES6+
- **Backend**: Spring Boot 3.5.11, Java 17, Spring Security 6
- **Database**: H2 (Dev), PostgreSQL (Prod), JPA/Hibernate
- **Security**: JWT Authentication, Role-based Authorization
- **Documentation**: SpringDoc OpenAPI 3.0
- **Build**: Maven 3.8+, Spring Boot Plugin

## API Endpoints Summary
```
Authentication:   /api/auth/**
Customers:        /api/customers/**
Jobs:             /api/jobs/**
Invoices:         /api/invoices/**
Electricians:      /api/electricians/**
Project Pictures:  /api/project-pictures/**
```

## Security Model
```
JWT Token Flow:
1. Login → Validate Credentials → Generate JWT
2. API Calls → Include JWT in Authorization Header
3. Backend → Validate JWT → Process Request
4. Roles: ROLE_ELECTRICIAN, ROLE_ADMIN, ROLE_MANAGER
```

## File Storage Architecture
```
Upload Flow:
1. Frontend → Drag & Drop / File Select
2. Validation → File Type, Size Check
3. API → Multipart Form Data
4. Storage → Unique Filename + Directory Organization
5. Database → Metadata + File Path Reference
```

This architecture provides:
- ✅ **Scalability**: Layered design with clear separation
- ✅ **Security**: JWT-based authentication with role management
- ✅ **Maintainability**: Clean code structure with Spring Boot patterns
- ✅ **Extensibility**: Easy to add new features and endpoints
- ✅ **Performance**: Optimized data access and caching strategies
