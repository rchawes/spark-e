-- Flyway migration for audit fields and enhanced Customer entity
-- Version: 1.1.0

-- Add audit columns to all existing tables
ALTER TABLE customers ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE customers ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE customers ADD COLUMN created_by VARCHAR(100) NOT NULL DEFAULT 'system';

-- Add new business columns to customers table
ALTER TABLE customers ADD COLUMN company_name VARCHAR(100);
ALTER TABLE customers ADD COLUMN notes VARCHAR(500);
ALTER TABLE customers ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;

-- Update existing customers with default values
UPDATE customers SET created_by = 'migration' WHERE created_by = 'system';
UPDATE customers SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
UPDATE customers SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL;

-- Add audit columns to jobs table
ALTER TABLE jobs ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE jobs ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE jobs ADD COLUMN created_by VARCHAR(100) NOT NULL DEFAULT 'system';

-- Update existing jobs with default values
UPDATE jobs SET created_by = 'migration' WHERE created_by = 'system';
UPDATE jobs SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
UPDATE jobs SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL;

-- Add audit columns to invoices table
ALTER TABLE invoices ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE invoices ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE invoices ADD COLUMN created_by VARCHAR(100) NOT NULL DEFAULT 'system';

-- Update existing invoices with default values
UPDATE invoices SET created_by = 'migration' WHERE created_by = 'system';
UPDATE invoices SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
UPDATE invoices SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL;

-- Add audit columns to electricians table
ALTER TABLE electricians ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE electricians ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE electricians ADD COLUMN created_by VARCHAR(100) NOT NULL DEFAULT 'system';

-- Update existing electricians with default values
UPDATE electricians SET created_by = 'migration' WHERE created_by = 'system';
UPDATE electricians SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
UPDATE electricians SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL;

-- Add audit columns to project_pictures table
ALTER TABLE project_pictures ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE project_pictures ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE project_pictures ADD COLUMN created_by VARCHAR(100) NOT NULL DEFAULT 'system';

-- Update existing project_pictures with default values
UPDATE project_pictures SET created_by = 'migration' WHERE created_by = 'system';
UPDATE project_pictures SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
UPDATE project_pictures SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL;
