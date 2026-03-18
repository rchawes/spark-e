package com.electrician.spark_e.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Enhanced OpenAPI configuration for comprehensive API documentation
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sparkEOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Spark-E API")
                .description("Enterprise-grade Electrician Trade Management System API\n\n" +
                    "## Overview\n" +
                    "Spark-E is a comprehensive business management platform for electricians and trade professionals. " +
                    "This API provides complete CRUD operations for customers, jobs, invoices, and more.\n\n" +
                    "## Authentication\n" +
                    "All API endpoints (except authentication) require JWT token authentication. " +
                    "Include the token in the Authorization header: `Bearer <token>`\n\n" +
                    "## Pagination\n" +
                    "List endpoints support pagination with the following parameters:\n" +
                    "- `page`: Page number (default: 0)\n" +
                    "- `size`: Items per page (default: 10, max: 100)\n" +
                    "- `sortBy`: Field to sort by (default: id)\n" +
                    "- `sortDir`: Sort direction (asc|desc, default: asc)\n\n" +
                    "## Error Handling\n" +
                    "The API returns standard HTTP status codes and includes error details in the response body:\n" +
                    "```json\n" +
                    "{\n" +
                    "  \"timestamp\": \"2024-01-01T12:00:00Z\",\n" +
                    "  \"status\": 400,\n" +
                    "  \"error\": \"Bad Request\",\n" +
                    "  \"message\": \"Validation failed\",\n" +
                    "  \"path\": \"/api/customers\"\n" +
                    "}\n" +
                    "```")
                .version("2.0.0")
                .contact(new Contact()
                    .name("Spark-E Development Team")
                    .email("support@spark-e.com")
                    .url("https://spark-e.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8082")
                    .description("Development Server"),
                new Server()
                    .url("https://api.spark-e.com")
                    .description("Production Server"),
                new Server()
                    .url("https://staging-api.spark-e.com")
                    .description("Staging Server")))
            .tags(List.of(
                createAuthenticationTag(),
                createCustomerTag(),
                createJobTag(),
                createInvoiceTag(),
                createDashboardTag(),
                createElectricianTag(),
                createHealthTag()))
            .components(new Components()
                .securitySchemes(createSecuritySchemes())
                .schemas(createSchemas())
                .responses(createResponses()))
            .security(List.of(new SecurityRequirement().addList("JWT")));
    }

    private Tag createAuthenticationTag() {
        return new Tag()
            .name("Authentication")
            .description("User authentication and authorization endpoints");
    }

    private Tag createCustomerTag() {
        return new Tag()
            .name("Customers")
            .description("Customer management operations including CRUD, search, and statistics");
    }

    private Tag createJobTag() {
        return new Tag()
            .name("Jobs")
            .description("Job management operations including creation, updates, and status tracking");
    }

    private Tag createInvoiceTag() {
        return new Tag()
            .name("Invoices")
            .description("Invoice management operations including generation, payment tracking, and reporting");
    }

    private Tag createDashboardTag() {
        return new Tag()
            .name("Dashboard")
            .description("Dashboard metrics, statistics, and business intelligence data");
    }

    private Tag createElectricianTag() {
        return new Tag()
            .name("Electricians")
            .description("Electrician management operations (admin only)");
    }

    private Tag createHealthTag() {
        return new Tag()
            .name("Health")
            .description("Application health check and monitoring endpoints");
    }

    private SecurityScheme createJWTSecurityScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("JWT token obtained from /api/auth/login endpoint");
    }

    private Components createSecuritySchemes() {
        return new Components()
            .addSecuritySchemes("JWT", createJWTSecurityScheme());
    }

    private Components createSchemas() {
        return new Components()
            .addSchemas("Customer", createCustomerSchema())
            .addSchemas("Job", createJobSchema())
            .addSchemas("Invoice", createInvoiceSchema())
            .addSchemas("ErrorResponse", createErrorResponseSchema())
            .addSchemas("PaginatedResponse", createPaginatedResponseSchema());
    }

    private Schema createCustomerSchema() {
        return new Schema()
            .type("object")
            .title("Customer")
            .description("Customer entity representing trade business clients")
            .addProperty("id", new Schema().type("integer").format("int64").description("Unique customer identifier"))
            .addProperty("firstName", new Schema().type("string").description("Customer's first name"))
            .addProperty("lastName", new Schema().type("string").description("Customer's last name"))
            .addProperty("email", new Schema().type("string").format("email").description("Customer's email address"))
            .addProperty("phone", new Schema().type("string").description("Customer's phone number"))
            .addProperty("address", new Schema().type("string").description("Customer's address"))
            .addProperty("companyName", new Schema().type("string").description("Customer's company name"))
            .addProperty("notes", new Schema().type("string").description("Additional notes about customer"))
            .addProperty("isActive", new Schema().type("boolean").description("Whether customer is active"))
            .addProperty("createdAt", new Schema().type("string").format("date-time").description("Creation timestamp"))
            .addProperty("updatedAt", new Schema().type("string").format("date-time").description("Last update timestamp"))
            .addProperty("createdBy", new Schema().type("string").description("User who created the customer"));
    }

    private Schema createJobSchema() {
        return new Schema()
            .type("object")
            .title("Job")
            .description("Job entity representing electrical work assignments")
            .addProperty("id", new Schema().type("integer").format("int64").description("Unique job identifier"))
            .addProperty("customerId", new Schema().type("integer").format("int64").description("Associated customer ID"))
            .addProperty("electricianId", new Schema().type("integer").format("int64").description("Assigned electrician ID"))
            .addProperty("jobTitle", new Schema().type("string").description("Job title"))
            .addProperty("description", new Schema().type("string").description("Job description"))
            .addProperty("status", new Schema().type("string").description("Job status (PENDING, IN_PROGRESS, COMPLETED, CANCELLED)"))
            .addProperty("startDate", new Schema().type("string").format("date-time").description("Job start date"))
            .addProperty("endDate", new Schema().type("string").format("date-time").description("Job end date"))
            .addProperty("estimatedCost", new Schema().type("number").format("decimal").description("Estimated job cost"))
            .addProperty("location", new Schema().type("string").description("Job location"))
            .addProperty("notes", new Schema().type("string").description("Additional job notes"))
            .addProperty("priority", new Schema().type("string").description("Job priority"))
            .addProperty("createdAt", new Schema().type("string").format("date-time").description("Creation timestamp"))
            .addProperty("updatedAt", new Schema().type("string").format("date-time").description("Last update timestamp"))
            .addProperty("createdBy", new Schema().type("string").description("User who created the job"));
    }

    private Schema createInvoiceSchema() {
        return new Schema()
            .type("object")
            .title("Invoice")
            .description("Invoice entity for billing customers")
            .addProperty("id", new Schema().type("integer").format("int64").description("Unique invoice identifier"))
            .addProperty("jobId", new Schema().type("integer").format("int64").description("Associated job ID"))
            .addProperty("invoiceNumber", new Schema().type("string").description("Invoice number"))
            .addProperty("issueDate", new Schema().type("string").format("date").description("Invoice issue date"))
            .addProperty("dueDate", new Schema().type("string").format("date").description("Invoice due date"))
            .addProperty("paidDate", new Schema().type("string").format("date").description("Payment date"))
            .addProperty("laborCost", new Schema().type("number").format("decimal").description("Labor cost"))
            .addProperty("materialsCost", new Schema().type("number").format("decimal").description("Materials cost"))
            .addProperty("tax", new Schema().type("number").format("decimal").description("Tax amount"))
            .addProperty("totalAmount", new Schema().type("number").format("decimal").description("Total invoice amount"))
            .addProperty("paid", new Schema().type("boolean").description("Payment status"))
            .addProperty("notes", new Schema().type("string").description("Invoice notes"))
            .addProperty("createdAt", new Schema().type("string").format("date-time").description("Creation timestamp"))
            .addProperty("updatedAt", new Schema().type("string").format("date-time").description("Last update timestamp"))
            .addProperty("createdBy", new Schema().type("string").description("User who created the invoice"));
    }

    private Schema createErrorResponseSchema() {
        return new Schema()
            .type("object")
            .title("ErrorResponse")
            .description("Standard error response format")
            .addProperty("timestamp", new Schema().type("string").format("date-time").description("Error timestamp"))
            .addProperty("status", new Schema().type("integer").format("int32").description("HTTP status code"))
            .addProperty("error", new Schema().type("string").description("Error type"))
            .addProperty("message", new Schema().type("string").description("Error message"))
            .addProperty("path", new Schema().type("string").description("Request path"));
    }

    private Schema createPaginatedResponseSchema() {
        return new Schema()
            .type("object")
            .title("PaginatedResponse")
            .description("Paginated response format")
            .addProperty("content", new Schema().type("array").description("Page content"))
            .addProperty("pageable", new Schema().type("object").description("Pagination metadata"))
            .addProperty("totalElements", new Schema().type("integer").format("int64").description("Total elements"))
            .addProperty("totalPages", new Schema().type("integer").format("int32").description("Total pages"))
            .addProperty("size", new Schema().type("integer").format("int32").description("Page size"))
            .addProperty("number", new Schema().type("integer").format("int32").description("Current page number"))
            .addProperty("first", new Schema().type("boolean").description("Is first page"))
            .addProperty("last", new Schema().type("boolean").description("Is last page"));
    }

    private Components createResponses() {
        return new Components()
            .addResponses("BadRequest", new ApiResponse()
                .description("Bad Request - Invalid input data")
                .content(new Content()
                    .addMediaType("application/json", new MediaType()
                        .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))))
            .addResponses("Unauthorized", new ApiResponse()
                .description("Unauthorized - Invalid or missing authentication")
                .content(new Content()
                    .addMediaType("application/json", new MediaType()
                        .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))))
            .addResponses("NotFound", new ApiResponse()
                .description("Not Found - Resource does not exist")
                .content(new Content()
                    .addMediaType("application/json", new MediaType()
                        .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))))
            .addResponses("InternalServerError", new ApiResponse()
                .description("Internal Server Error - Unexpected server error")
                .content(new Content()
                    .addMediaType("application/json", new MediaType()
                        .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))));
    }
}
