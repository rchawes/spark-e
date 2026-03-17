# Render Deployment Setup Guide

## 🚀 **Render Configuration Steps**

### **1. Create Render Account**
1. Sign up at [render.com](https://render.com)
2. Connect your GitHub account
3. Choose the "Free" tier to start

### **2. Create Web Service**
1. Click **"New +"** → **"Web Service"**
2. Connect your GitHub repository
3. Configure the service:

```yaml
# Render Service Configuration
Name: spark-e
Environment: Production
Branch: main
Root Directory: (leave empty)
Build Command: mvn clean package -DskipTests
Start Command: java -jar target/spark_e-0.0.1-SNAPSHOT.jar
```

### **3. Environment Variables**
Set these in Render dashboard:

```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://your-db-host:5432/sparkedb
DATABASE_USERNAME=your-db-username
DATABASE_PASSWORD=your-db-password

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-min-256-bits

# Spring Configuration
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=10000

# File Upload Configuration
FILE_UPLOAD_PATH=/var/data/uploads
MAX_FILE_SIZE=10485760
```

### **4. Database Setup**
1. In Render, create a **PostgreSQL** database
2. Note the connection details
3. Update the environment variables above

### **5. Get Service ID for GitHub Actions**
1. Go to your Render service dashboard
2. Look at the URL: `https://dashboard.render.com/web/services/srv-xxxxxxxxxxxxx`
3. Copy the service ID: `srv-xxxxxxxxxxxxx`

### **6. Get API Key**
1. Go to Render dashboard → **Account Settings** → **API Keys**
2. Create a new API key
3. Copy the key for GitHub secrets

### **7. Configure GitHub Secrets**
Add these to your GitHub repository:

```bash
# Render Deployment
RENDER_API_KEY=render-api-key-here
RENDER_SERVICE_ID=srv-xxxxxxxxxxxxx

# Optional: Docker Hub (if using Docker deployment)
DOCKER_USERNAME=your-docker-username
DOCKER_PASSWORD=your-docker-password
```

## 🔧 **Advanced Configuration**

### **Custom Domain**
1. In Render service settings → **"Custom Domains"**
2. Add your custom domain
3. Update DNS records as instructed

### **Health Checks**
Render automatically checks `/` endpoint. Our health endpoints:
- `/api/health` - Basic health check
- `/api/ready` - Readiness probe

### **Auto-Deploys**
Enable in Render service settings:
- **Auto-Deploy on pushes** to main branch
- **GitHub Actions integration** for custom pipeline

## 📊 **Monitoring**

### **Render Dashboard**
- View logs in real-time
- Monitor performance metrics
- Check deployment status
- Database performance

### **Health Endpoints**
```bash
# Health check
curl https://spark-e.onrender.com/api/health

# Readiness check
curl https://spark-e.onrender.com/api/ready
```

## 🚨 **Troubleshooting**

### **Common Issues**

#### **1. Build Failures**
```bash
# Check build logs in Render dashboard
# Common solutions:
- Increase memory allocation
- Fix dependency issues
- Check Java version compatibility
```

#### **2. Database Connection**
```bash
# Verify database is running
# Check connection string format
# Ensure credentials are correct
# Test connection locally first
```

#### **3. Port Issues**
```bash
# Render uses port 10000 by default
# Ensure Spring Boot binds to 0.0.0.0
# Check firewall settings
```

#### **4. Memory Issues**
```bash
# Upgrade to paid tier for more memory
# Optimize JVM settings:
JAVA_OPTS="-Xms512m -Xmx1024m"
```

### **Debug Mode**
Add to environment variables:
```bash
DEBUG=true
LOGGING_LEVEL_COM_ELECTRICIAN_SPARK_E=DEBUG
```

## 🎯 **Production Optimizations**

### **1. Performance**
```bash
# JVM optimization
JAVA_OPTS="-Xms1g -Xmx2g -XX:+UseG1GC"

# Database connection pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

### **2. Security**
```bash
# HTTPS only (Render provides)
# Security headers
# Rate limiting
# Input validation
```

### **3. Scaling**
```bash
# Add more instances
# Use load balancer
# Database scaling
# Caching strategy
```

## 📞 **Support**

- **Render Documentation**: https://render.com/docs
- **GitHub Issues**: Check workflow logs
- **Community**: Render Discord community

---

**Your Spark-E application will be live at: https://spark-e.onrender.com** 🚀
