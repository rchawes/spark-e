# CI/CD Setup Instructions

## GitHub Actions Setup

### Required GitHub Secrets
Add these secrets to your GitHub repository:

```bash
# Docker Hub (for container registry)
DOCKER_USERNAME=your-docker-username
DOCKER_PASSWORD=your-docker-password

# Render Deployment
RENDER_API_KEY=your-render-api-key
RENDER_SERVICE_ID=your-render-service-id

# Codecov (optional)
CODECOV_TOKEN=your-codecov-token
```

### Pipeline Triggers
- **Push to main**: Full pipeline with deployment
- **Push to develop**: Tests and build only
- **Pull Request**: Tests and security scanning

### Environment Setup
```bash
# Production Environment
- Database: PostgreSQL (Render)
- Cache: Redis (if needed)
- Monitoring: Health checks enabled
- Security: HTTPS enforced

# Staging Environment  
- Database: PostgreSQL test instance
- Cache: In-memory
- Monitoring: Detailed logging
- Security: Basic auth
```

## Local Development Setup

### Prerequisites
```bash
# Install required tools
- Java 17+
- Maven 3.6+
- Docker Desktop
- PostgreSQL (optional, can use H2)
```

### Environment Variables
```bash
# Copy and configure
cp src/main/resources/application-example.properties src/main/resources/application.properties

# Set your local values
DATABASE_URL=jdbc:postgresql://localhost:5432/sparkedb
DATABASE_USERNAME=your-username
DATABASE_PASSWORD=your-password
JWT_SECRET=your-secret-key
```

### Running Tests
```bash
# Unit tests
mvn test

# Integration tests
mvn verify -P integration-tests

# Coverage report
mvn jacoco:report

# All tests with coverage
mvn clean test jacoco:report
```

## Deployment

### Docker Deployment
```bash
# Build image
docker build -t spark-e:latest .

# Run locally
docker run -p 8082:8082 \
  -e DATABASE_URL=jdbc:postgresql://host:5432/db \
  -e DATABASE_USERNAME=user \
  -e DATABASE_PASSWORD=pass \
  spark-e:latest
```

### Render Deployment
1. Connect GitHub repository to Render
2. Set environment variables in Render dashboard
3. Deploy automatically on push to main

### Monitoring
- Health checks: `/api/health` and `/api/ready`
- Logs: Available in Render dashboard
- Metrics: Basic application metrics via Actuator
