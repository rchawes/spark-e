-- Flyway migration for multi-tenant architecture
-- Version: 1.2.0

-- Create companies table for multi-tenant support
CREATE TABLE companies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    subdomain VARCHAR(50) NOT NULL UNIQUE,
    website VARCHAR(255),
    logo_url VARCHAR(500),
    contact_email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(500),
    description VARCHAR(1000),
    industry VARCHAR(50),
    company_size VARCHAR(20),
    plan_type VARCHAR(20) NOT NULL DEFAULT 'STARTER',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    max_users INTEGER,
    max_customers INTEGER,
    max_jobs INTEGER,
    max_storage_mb INTEGER,
    subscription_start TIMESTAMP,
    subscription_end TIMESTAMP,
    billing_cycle VARCHAR(10),
    settings TEXT,
    timezone VARCHAR(50) DEFAULT 'UTC',
    currency VARCHAR(3) DEFAULT 'USD',
    tax_rate DECIMAL(5,2),
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    verified_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system'
);

-- Create indexes for companies table
CREATE INDEX idx_company_subdomain ON companies(subdomain);
CREATE INDEX idx_company_active ON companies(active);
CREATE INDEX idx_company_plan ON companies(plan_type);
CREATE INDEX idx_company_subscription_end ON companies(subscription_end);

-- Add company_id to existing tables for multi-tenancy
ALTER TABLE customers ADD COLUMN company_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE jobs ADD COLUMN company_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE invoices ADD COLUMN company_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE electricians ADD COLUMN company_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE project_pictures ADD COLUMN company_id BIGINT NOT NULL DEFAULT 1;

-- Add foreign key constraints
ALTER TABLE customers ADD CONSTRAINT fk_customer_company 
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE;
ALTER TABLE jobs ADD CONSTRAINT fk_job_company 
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE;
ALTER TABLE invoices ADD CONSTRAINT fk_invoice_company 
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE;
ALTER TABLE electricians ADD CONSTRAINT fk_electrician_company 
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE;
ALTER TABLE project_pictures ADD CONSTRAINT fk_project_picture_company 
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE;

-- Add indexes for company_id in all tables
CREATE INDEX idx_customer_company_id ON customers(company_id);
CREATE INDEX idx_job_company_id ON jobs(company_id);
CREATE INDEX idx_invoice_company_id ON invoices(company_id);
CREATE INDEX idx_electrician_company_id ON electricians(company_id);
CREATE INDEX idx_project_picture_company_id ON project_pictures(company_id);

-- Create default company
INSERT INTO companies (
    name, 
    subdomain, 
    contact_email, 
    plan_type, 
    active, 
    max_users, 
    max_customers, 
    max_jobs,
    created_by
) VALUES (
    'Spark-E Demo Company',
    'demo',
    'demo@spark-e.com',
    'ENTERPRISE',
    TRUE,
    100,
    10000,
    100000,
    'migration'
);

-- Update existing records to belong to default company
UPDATE customers SET company_id = 1 WHERE company_id = 1;
UPDATE jobs SET company_id = 1 WHERE company_id = 1;
UPDATE invoices SET company_id = 1 WHERE company_id = 1;
UPDATE electricians SET company_id = 1 WHERE company_id = 1;
UPDATE project_pictures SET company_id = 1 WHERE company_id = 1;

-- Add tenant-specific audit columns
ALTER TABLE customers ADD COLUMN tenant_name VARCHAR(100);
ALTER TABLE jobs ADD COLUMN tenant_name VARCHAR(100);
ALTER TABLE invoices ADD COLUMN tenant_name VARCHAR(100);
ALTER TABLE electricians ADD COLUMN tenant_name VARCHAR(100);
ALTER TABLE project_pictures ADD COLUMN tenant_name VARCHAR(100);

-- Update tenant names
UPDATE customers SET tenant_name = 'Spark-E Demo Company' WHERE company_id = 1;
UPDATE jobs SET tenant_name = 'Spark-E Demo Company' WHERE company_id = 1;
UPDATE invoices SET tenant_name = 'Spark-E Demo Company' WHERE company_id = 1;
UPDATE electricians SET tenant_name = 'Spark-E Demo Company' WHERE company_id = 1;
UPDATE project_pictures SET tenant_name = 'Spark-E Demo Company' WHERE company_id = 1;

-- Create tenant statistics view
CREATE OR REPLACE VIEW tenant_statistics AS
SELECT 
    c.id as company_id,
    c.name as company_name,
    c.subdomain,
    c.plan_type,
    c.active,
    COUNT(DISTINCT cu.id) as customer_count,
    COUNT(DISTINCT j.id) as job_count,
    COUNT(DISTINCT i.id) as invoice_count,
    COUNT(DISTINCT e.id) as electrician_count,
    COALESCE(SUM(i.total_amount), 0) as total_revenue,
    c.max_customers,
    c.max_jobs,
    CASE 
        WHEN COUNT(DISTINCT cu.id) >= c.max_customers THEN 'CUSTOMER_LIMIT_REACHED'
        WHEN COUNT(DISTINCT j.id) >= c.max_jobs THEN 'JOB_LIMIT_REACHED'
        ELSE 'WITHIN_LIMITS'
    END as limit_status
FROM companies c
LEFT JOIN customers cu ON c.id = cu.company_id
LEFT JOIN jobs j ON c.id = j.company_id
LEFT JOIN invoices i ON c.id = i.company_id
LEFT JOIN electricians e ON c.id = e.company_id
GROUP BY c.id, c.name, c.subdomain, c.plan_type, c.active, c.max_customers, c.max_jobs;

-- Add RLS (Row Level Security) policies for PostgreSQL
-- Note: This is optional and requires PostgreSQL 9.5+
-- ALTER TABLE customers ENABLE ROW LEVEL SECURITY;
-- ALTER TABLE jobs ENABLE ROW LEVEL SECURITY;
-- ALTER TABLE invoices ENABLE ROW LEVEL SECURITY;
-- ALTER TABLE electricians ENABLE ROW LEVEL SECURITY;
-- ALTER TABLE project_pictures ENABLE ROW LEVEL SECURITY;

-- Create function for tenant isolation
CREATE OR REPLACE FUNCTION check_tenant_access()
RETURNS TRIGGER AS $$
BEGIN
    -- This would be used with RLS to ensure tenant isolation
    -- For now, it's a placeholder for future RLS implementation
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create audit trigger for tenant changes
CREATE OR REPLACE FUNCTION audit_tenant_changes()
RETURNS TRIGGER AS $$
BEGIN
    -- Update tenant name when company changes
    IF TG_OP = 'UPDATE' THEN
        NEW.tenant_name = (SELECT name FROM companies WHERE id = NEW.company_id);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for tenant audit
CREATE TRIGGER audit_customer_tenant
    BEFORE INSERT OR UPDATE ON customers
    FOR EACH ROW EXECUTE FUNCTION audit_tenant_changes();

CREATE TRIGGER audit_job_tenant
    BEFORE INSERT OR UPDATE ON jobs
    FOR EACH ROW EXECUTE FUNCTION audit_tenant_changes();

CREATE TRIGGER audit_invoice_tenant
    BEFORE INSERT OR UPDATE ON invoices
    FOR EACH ROW EXECUTE FUNCTION audit_tenant_changes();

CREATE TRIGGER audit_electrician_tenant
    BEFORE INSERT OR UPDATE ON electricians
    FOR EACH ROW EXECUTE FUNCTION audit_tenant_changes();

CREATE TRIGGER audit_project_picture_tenant
    BEFORE INSERT OR UPDATE ON project_pictures
    FOR EACH ROW EXECUTE FUNCTION audit_tenant_changes();
