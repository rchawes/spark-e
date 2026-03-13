-- Spark-E PostgreSQL Database Setup Script
-- Production-ready schema for scalable cloud deployment

-- Create database
CREATE DATABASE IF NOT EXISTS spark_e_prod;
CREATE DATABASE IF NOT EXISTS spark_e_dev;
CREATE DATABASE IF NOT EXISTS spark_e_test;

-- Use production database
\c spark_e_prod;

-- Create extensions for UUID generation and JSON support
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Create user roles
CREATE ROLE IF NOT EXISTS spark_e_read;
CREATE ROLE IF NOT EXISTS spark_e_write;
CREATE ROLE IF NOT EXISTS spark_e_admin;

-- Create application user
CREATE USER IF NOT EXISTS spark_e_app WITH PASSWORD 'secureAppPassword123!';

-- Grant permissions
GRANT spark_e_read TO spark_e_app;
GRANT spark_e_write TO spark_e_app;
GRANT spark_e_admin TO spark_e_app;
GRANT ALL PRIVILEGES ON DATABASE spark_e_prod TO spark_e_app;

-- Create optimized indexes for performance
-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) DEFAULT 'ELECTRICIAN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Electricians table (extended user profile)
CREATE TABLE IF NOT EXISTS electricians (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    hourly_rate DECIMAL(10,2) DEFAULT 75.00,
    certification_level VARCHAR(20),
    license_number VARCHAR(50),
    insurance_number VARCHAR(50),
    years_experience INTEGER DEFAULT 0,
    specialties TEXT[], -- Array of specialties
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_electricians_user_id ON electricians(user_id);
CREATE INDEX IF NOT EXISTS idx_electricians_email ON electricians(email);
CREATE INDEX IF NOT EXISTS idx_electricians_active ON electricians(is_active);
CREATE INDEX IF NOT EXISTS idx_electricians_specialties ON electricians USING GIN(specialties);

-- Clients table
CREATE TABLE IF NOT EXISTS clients (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    address TEXT,
    city VARCHAR(50),
    state VARCHAR(2),
    zip_code VARCHAR(10),
    company VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_clients_email ON clients(email);
CREATE INDEX IF NOT EXISTS idx_clients_name ON clients(first_name, last_name);
CREATE INDEX IF NOT EXISTS idx_clients_city ON clients(city);
CREATE INDEX IF NOT EXISTS idx_clients_state ON clients(state);

-- Job types table
CREATE TABLE IF NOT EXISTS job_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    base_price DECIMAL(10,2),
    estimated_hours INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_job_types_name ON job_types(name);

-- Jobs table
CREATE TABLE IF NOT EXISTS jobs (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT REFERENCES clients(id) ON DELETE CASCADE,
    electrician_id BIGINT REFERENCES electricians(id) ON DELETE SET NULL,
    job_type_id BIGINT REFERENCES job_types(id),
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'SCHEDULED' CHECK (status IN ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    priority VARCHAR(10) DEFAULT 'NORMAL' CHECK (priority IN ('LOW', 'NORMAL', 'HIGH', 'URGENT')),
    scheduled_date TIMESTAMP,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    estimated_hours INTEGER,
    actual_hours INTEGER,
    labor_cost DECIMAL(10,2),
    materials_cost DECIMAL(10,2),
    total_cost DECIMAL(10,2),
    location_address TEXT,
    location_lat DECIMAL(10,8),
    location_lng DECIMAL(11,8),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_jobs_client_id ON jobs(client_id);
CREATE INDEX IF NOT EXISTS idx_jobs_electrician_id ON jobs(electrician_id);
CREATE INDEX IF NOT EXISTS idx_jobs_status ON jobs(status);
CREATE INDEX IF NOT EXISTS idx_jobs_priority ON jobs(priority);
CREATE INDEX IF NOT EXISTS idx_jobs_scheduled_date ON jobs(scheduled_date);
CREATE INDEX IF NOT EXISTS idx_jobs_location ON jobs USING GIST(location_lat, location_lng);

-- Parts table
CREATE TABLE IF NOT EXISTS parts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    sku VARCHAR(50) UNIQUE,
    supplier VARCHAR(100),
    cost DECIMAL(10,2),
    price DECIMAL(10,2),
    quantity_in_stock INTEGER DEFAULT 0,
    reorder_level INTEGER DEFAULT 5,
    category VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_parts_sku ON parts(sku);
CREATE INDEX IF NOT EXISTS idx_parts_name ON parts(name);
CREATE INDEX IF NOT EXISTS idx_parts_category ON parts(category);
CREATE INDEX IF NOT EXISTS idx_parts_reorder ON parts(quantity_in_stock, reorder_level);

-- Invoices table
CREATE TABLE IF NOT EXISTS invoices (
    id BIGSERIAL PRIMARY KEY,
    job_id BIGINT REFERENCES jobs(id) ON DELETE CASCADE,
    invoice_number VARCHAR(50) UNIQUE,
    issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP,
    paid_date TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PAID', 'OVERDUE', 'CANCELLED')),
    subtotal DECIMAL(10,2),
    tax_rate DECIMAL(5,4) DEFAULT 0.0825,
    tax_amount DECIMAL(10,2),
    total_amount DECIMAL(10,2),
    labor_cost DECIMAL(10,2),
    materials_cost DECIMAL(10,2),
    paid BOOLEAN DEFAULT false,
    payment_method VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_invoices_job_id ON invoices(job_id);
CREATE INDEX IF NOT EXISTS idx_invoices_number ON invoices(invoice_number);
CREATE INDEX IF NOT EXISTS idx_invoices_status ON invoices(status);
CREATE INDEX IF NOT EXISTS idx_invoices_issue_date ON invoices(issue_date);
CREATE INDEX IF NOT EXISTS idx_invoices_paid ON invoices(paid);

-- Job photos table
CREATE TABLE IF NOT EXISTS job_photos (
    id BIGSERIAL PRIMARY KEY,
    job_id BIGINT REFERENCES jobs(id) ON DELETE CASCADE,
    photo_url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500),
    description TEXT,
    photo_type VARCHAR(20) DEFAULT 'BEFORE' CHECK (photo_type IN ('BEFORE', 'DURING', 'AFTER', 'DOCUMENT')),
    file_size BIGINT,
    mime_type VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_job_photos_job_id ON job_photos(job_id);
CREATE INDEX IF NOT EXISTS idx_job_photos_type ON job_photos(photo_type);
CREATE INDEX IF NOT EXISTS idx_job_photos_created ON job_photos(created_at);

-- Audit log for compliance
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(50) NOT NULL,
    record_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL CHECK (action IN ('INSERT', 'UPDATE', 'DELETE')),
    old_values JSONB,
    new_values JSONB,
    user_id BIGINT REFERENCES users(id),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_table ON audit_log(table_name);
CREATE INDEX IF NOT EXISTS idx_audit_record ON audit_log(record_id);
CREATE INDEX IF NOT EXISTS idx_audit_action ON audit_log(action);
CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON audit_log(timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_user ON audit_log(user_id);

-- Create triggers for automatic timestamp updates
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply triggers to tables with updated_at columns
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_electricians_updated_at BEFORE UPDATE ON electricians FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_clients_updated_at BEFORE UPDATE ON clients FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_jobs_updated_at BEFORE UPDATE ON jobs FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_parts_updated_at BEFORE UPDATE ON parts FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_invoices_updated_at BEFORE UPDATE ON invoices FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Create audit trigger function
CREATE OR REPLACE FUNCTION audit_trigger_function()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'DELETE' THEN
        INSERT INTO audit_log (table_name, record_id, action, old_values, user_id)
        VALUES (TG_TABLE_NAME, OLD.id, 'DELETE', row_to_json(OLD), NULL);
        RETURN OLD;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO audit_log (table_name, record_id, action, old_values, new_values, user_id)
        VALUES (TG_TABLE_NAME, NEW.id, 'UPDATE', row_to_json(OLD), row_to_json(NEW), NULL);
        RETURN NEW;
    ELSIF TG_OP = 'INSERT' THEN
        INSERT INTO audit_log (table_name, record_id, action, new_values, user_id)
        VALUES (TG_TABLE_NAME, NEW.id, 'INSERT', row_to_json(NEW), NULL);
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Apply audit triggers
CREATE TRIGGER audit_users AFTER INSERT OR UPDATE OR DELETE ON users FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();
CREATE TRIGGER audit_electricians AFTER INSERT OR UPDATE OR DELETE ON electricians FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();
CREATE TRIGGER audit_clients AFTER INSERT OR UPDATE OR DELETE ON clients FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();
CREATE TRIGGER audit_jobs AFTER INSERT OR UPDATE OR DELETE ON jobs FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();
CREATE TRIGGER audit_parts AFTER INSERT OR UPDATE OR DELETE ON parts FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();
CREATE TRIGGER audit_invoices AFTER INSERT OR UPDATE OR DELETE ON invoices FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

-- Insert sample data for development
INSERT INTO users (username, password, email, role) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NocePjrj7k8Q5W5qHG8fE8W8V', 'admin@spark-e.com', 'ADMIN')
ON CONFLICT (username) DO NOTHING;

INSERT INTO job_types (name, description, base_price, estimated_hours) VALUES 
('Residential Service Call', 'Standard residential electrical service', 150.00, 2),
('Commercial Installation', 'Commercial electrical installation', 500.00, 8),
('Emergency Repair', '24/7 emergency electrical repair', 250.00, 3),
('Panel Upgrade', 'Electrical panel upgrade/replacement', 1200.00, 6),
('Lighting Installation', 'Interior/exterior lighting installation', 300.00, 4)
ON CONFLICT (name) DO NOTHING;

-- Grant read-only access for analytics
CREATE USER IF NOT EXISTS spark_e_analytics WITH PASSWORD 'analyticsPassword123!';
GRANT CONNECT ON DATABASE spark_e_prod TO spark_e_analytics;
GRANT USAGE ON SCHEMA public TO spark_e_analytics;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO spark_e_analytics;
GRANT SELECT ON ALL SEQUENCES IN SCHEMA public TO spark_e_analytics;

COMMIT;

-- Database optimization settings
ALTER SYSTEM SET shared_buffers = '256MB';
ALTER SYSTEM SET effective_cache_size = '1GB';
ALTER SYSTEM SET maintenance_work_mem = '64MB';
ALTER SYSTEM SET checkpoint_completion_target = 0.9;
ALTER SYSTEM SET wal_buffers = '16MB';
ALTER SYSTEM SET default_statistics_target = 100;

SELECT pg_reload_conf();

-- Vacuum and analyze for performance optimization
VACUUM ANALYZE;

-- Create partitioned table for large datasets (optional for future scaling)
-- This can be implemented when data grows significantly
-- CREATE TABLE jobs_partitioned (LIKE jobs INCLUDING ALL) PARTITION BY RANGE (created_at);
