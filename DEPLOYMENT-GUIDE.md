# Spark-E Cloud Database Deployment Guide

## 🗄️ Scalable Database Architecture

This guide sets up Spark-E with a production-ready PostgreSQL database on AWS RDS with Redis caching for enterprise scalability.

## 🏗️ Architecture Overview

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Spark-E App   │────│   AWS RDS        │────│   Redis Cache   │
│   (Spring Boot) │    │   PostgreSQL      │    │   (ElastiCache)  │
│   Port: 8080    │    │   Multi-AZ        │    │   Cluster Mode   │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         │                       │                       │
         │              ┌──────────────────┐         │
         └──────────────│   AWS S3         │─────────┘
                        │   File Storage     │
                        └──────────────────┘
```

## 🚀 Quick Start

### 1. AWS RDS PostgreSQL Setup

```bash
# Create RDS instance (via AWS Console or CLI)
aws rds create-db-instance \
    --db-instance-identifier spark-e-prod \
    --db-instance-class db.t3.medium \
    --engine postgres \
    --engine-version 15.4 \
    --master-username spark_e_admin \
    --master-user-password YourSecurePassword123! \
    --allocated-storage 100 \
    --storage-type gp2 \
    --vpc-security-group-ids sg-xxxxxxxxx \
    --db-subnet-group-name default \
    --backup-retention-period 7 \
    --multi-az \
    --publicly-accessible \
    --region us-west-2
```

### 2. Redis ElastiCache Setup

```bash
# Create Redis cluster
aws elasticache create-replication-group \
    --replication-group-id spark-e-redis \
    --replication-group-description "Spark-E Redis Cache" \
    --num-cache-clusters 1 \
    --engine redis \
    --cache-node-type cache.t3.micro \
    --security-group-ids sg-xxxxxxxxx \
    --subnet-group-name default \
    --automatic-failover-enabled \
    --region us-west-2
```

### 3. Application Configuration

Copy the appropriate configuration file:

```bash
# Development
cp database-config-dev.properties src/main/resources/application.properties

# Production
cp database-config-prod.properties src/main/resources/application.properties
```

### 4. Environment Variables

Set these environment variables for production:

```bash
export DB_PASSWORD="YourSecureDatabasePassword123!"
export REDIS_PASSWORD="YourSecureRedisPassword123!"
export AWS_ACCESS_KEY="AKIAIOSFODNN7EXAMPLE"
export AWS_SECRET_KEY="wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
export JWT_SECRET="your-256-bit-secret-key-here-must-be-very-long-and-secure-for-production-use"
```

## 🔧 Database Setup

### Option 1: Automated Setup (Recommended)

```bash
# Run the setup script
psql -h spark-e-prod.c3usd2k3l4e.us-west-2.rds.amazonaws.com \
     -U spark_e_admin -d spark_e_prod -f database-setup.sql
```

### Option 2: Manual Setup

```bash
# Connect to database
psql -h your-rds-endpoint.rds.amazonaws.com \
     -U spark_e_admin -d spark_e_prod

# Run individual setup commands
\i database-setup.sql
```

## 📊 Performance Optimization

### Database Configuration

The setup includes these optimizations:

- **Connection Pooling**: HikariCP with 20 max connections
- **Batch Processing**: Hibernate batch inserts/updates
- **Indexing Strategy**: Optimized indexes for common queries
- **Partitioning**: Ready for large dataset partitioning
- **Caching**: Redis integration for frequently accessed data

### Monitoring Queries

```sql
-- Monitor slow queries
SELECT query, mean_time, calls, total_time
FROM pg_stat_statements
WHERE mean_time > 100
ORDER BY mean_time DESC
LIMIT 10;

-- Monitor index usage
SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read
FROM pg_stat_user_indexes
ORDER BY idx_scan DESC;
```

## 🔒 Security Configuration

### Database Security

- **Encryption**: TLS/SSL enabled by default
- **Access Control**: Role-based permissions
- **Audit Logging**: Complete audit trail
- **Network Security**: VPC with security groups
- **Backup**: Automated daily backups with 7-day retention

### Application Security

```properties
# JWT Configuration (Production)
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# Database Security
spring.datasource.password=${DB_PASSWORD}
spring.jpa.show-sql=false
```

## 🚀 Deployment Commands

### Development Environment

```bash
# Start with local PostgreSQL
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Production Environment

```bash
# Build JAR file
mvn clean package -DskipTests

# Deploy to production
java -jar -Dspring.profiles.active=prod \
          -DDATABASE_URL=jdbc:postgresql://prod-endpoint:5432/spark_e_prod \
          -DDATABASE_PASSWORD=${DB_PASSWORD} \
          target/spark-e-0.0.1-SNAPSHOT.jar
```

## 📈 Scaling Strategy

### Vertical Scaling

```bash
# Upgrade RDS instance
aws rds modify-db-instance \
    --db-instance-identifier spark-e-prod \
    --db-instance-class db.t3.large \
    --apply-immediately

# Scale Redis cluster
aws elasticache modify-replication-group \
    --replication-group-id spark-e-redis \
    --cache-node-type cache.t3.small
```

### Horizontal Scaling (Read Replicas)

```bash
# Add read replica
aws rds create-db-instance-read-replica \
    --db-instance-identifier spark-e-prod-replica-1 \
    --source-db-instance-identifier spark-e-prod \
    --db-instance-class db.t3.medium
```

## 🔍 Monitoring & Health Checks

### Application Health

```bash
# Health endpoints
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
```

### Database Health

```sql
-- Check connection count
SELECT count(*) FROM pg_stat_activity;

-- Check database size
SELECT pg_size_pretty(pg_database_size('spark_e_prod'));

-- Check table sizes
SELECT schemaname, tablename, 
       pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables 
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

## 🛠️ Maintenance

### Regular Maintenance

```bash
# Weekly vacuum and analyze
psql -h prod-endpoint -U spark_e_admin -d spark_e_prod -c "VACUUM ANALYZE;"

# Update statistics
psql -h prod-endpoint -U spark_e_admin -d spark_e_prod -c "ANALYZE;"

# Check for bloat
SELECT schemaname, tablename, 
       pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size,
       pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename) - pg_relation_size(schemaname||'.'||tablename)) AS bloat
FROM pg_tables 
WHERE schemaname = 'public';
```

## 🚨 Troubleshooting

### Common Issues

1. **Connection Timeout**
   ```properties
   spring.datasource.hikari.connection-timeout=30000
   ```

2. **Memory Issues**
   ```properties
   spring.jpa.properties.hibernate.jdbc.batch_size=10
   ```

3. **Slow Queries**
   ```sql
   EXPLAIN ANALYZE SELECT * FROM jobs WHERE status = 'COMPLETED';
   ```

## 📞 Support

### AWS Resources
- **RDS Support**: AWS Console > RDS > Support
- **ElastiCache Support**: AWS Console > ElastiCache > Support
- **CloudWatch**: Monitor performance metrics

### Application Logs
```bash
# View application logs
tail -f /var/log/spark-e/application.log

# View database logs
aws rds describe-db-log-files --db-instance-identifier spark-e-prod
```

## 🔄 Backup & Recovery

### Automated Backups

- **Daily**: Automated snapshots
- **Weekly**: Full database export
- **Point-in-time**: 7-day recovery window
- **Cross-region**: Optional disaster recovery

### Manual Backup

```bash
# Export database
pg_dump -h prod-endpoint -U spark_e_admin spark_e_prod > backup_$(date +%Y%m%d).sql

# Restore database
psql -h prod-endpoint -U spark_e_admin spark_e_prod < backup_20231201.sql
```

---

## ✅ Production Checklist

- [ ] RDS PostgreSQL instance created and configured
- [ ] Redis ElastiCache cluster set up
- [ ] Security groups configured
- [ ] Database schema deployed
- [ ] Environment variables set
- [ ] SSL/TLS verified
- [ ] Backup strategy implemented
- [ ] Monitoring configured
- [ ] Performance testing completed
- [ ] Security audit passed

This setup provides enterprise-grade scalability, security, and performance for Spark-E production deployment.
