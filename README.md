# Spark_e

Backend API for tradespeople (electricians, plumbers, etc.) to manage customers, jobs, invoices, inventory, and compliance checklists. Built with Spring Boot, Spring Security (JWT), JPA, and PostgreSQL.

## About Me

I spent 14 years as an electrician and recently switched careers into software engineering. This project combines my old trade with my new skills. It's a backend API for managing the day-to-day chaos of running a trade business - customers, job scheduling, parts inventory, invoicing, and safety paperwork. No more spreadsheets and sticky notes.

## Why I Built This

I wanted to solve a real problem I actually understood. Electricians juggle a lot of moving parts. I've lived it. So I built Spark_e to scratch my own itch and to prove I can build useful software.

## What's Inside

Customer management with full CRUD operations and contact history tracking. Job scheduling with create, assign, and status tracking. Parts inventory with stock levels and low-stock alerts. Invoice automation that triggers when a job is marked complete - it calculates labor based on hours worked times hourly rate plus materials used times price. Compliance checklists tied to job types, so a consumer unit replacement has specific safety items that must be checked off. Reporting for electrician performance metrics, inventory status, and customer profitability. JWT security protecting all endpoints. Swagger UI for interactive API documentation.

## What I Learned

JPA relationships took time to get right. I drew a lot of diagrams to figure out OneToMany and ManyToOne mappings. I forgot the mappedBy attribute more than once. Spring Security and JWT took three attempts before I understood filter chains, authentication managers, and why CSRF gets disabled for stateless APIs. I tried using Lombok but kept hitting cryptic TypeTag.UNKNOWN errors, so I wrote all my getters and setters manually. Honestly that helped me understand my entities better. Spring Data query methods are great until they aren't - I learned when to fall back to explicit Query annotations. Debugging was painful at first. I spent hours staring at stack traces. But every error taught me something.

## Tech Stack

Java 17, Spring Boot 3.2.4, Spring Data JPA, Spring Security with JWT, PostgreSQL for production and H2 for development, Maven, and Swagger via springdoc-openapi.

## How to Run It

### Local Development with H2

Clone the repository:

    git clone https://github.com/rchawes/spark-e.git
    cd spark-e

Copy the example configuration:

    cp src/main/resources/application-example.properties src/main/resources/application.properties

Run the application:

    mvn spring-boot:run

Open Swagger UI at http://localhost:8080/swagger-ui.html

### Production on Render

Create a PostgreSQL database on Render. Deploy with these environment variables:

DATABASE_URL - your PostgreSQL connection string
DATABASE_USERNAME - database user
DATABASE_PASSWORD - database password
JWT_SECRET - generate a long random string with openssl rand -base64 64
SPRING_PROFILES_ACTIVE - set to prod

## API Overview

Authentication endpoints for registration and login at /api/auth/register and /api/auth/login. Business endpoints for customers, jobs, parts, and invoices under /api. Reporting endpoints for performance metrics. All business endpoints require a valid JWT token.

Example registration:

    curl -X POST http://localhost:8080/api/auth/register \
      -H "Content-Type: application/json" \
      -d '{"username": "jane", "password": "securepass123", "role": "ROLE_ELECTRICIAN"}'

Example login which returns your JWT token:

    curl -X POST http://localhost:8080/api/auth/login \
      -H "Content-Type: application/json" \
      -d '{"username": "jane", "password": "securepass123"}'

## Testing

Run unit tests with mvn test. Generate coverage reports with mvn jacoco:report. For manual testing, use the Swagger UI at /swagger-ui.html.

## What's Next

I need to add proper unit and integration tests. I know I should have done this earlier but I was focused on getting features working. I'm considering building a simple React frontend to make this a full-stack demonstration. Docker containerization is on the list too. Eventually I'd like to set up CI/CD with GitHub Actions.

## Get In Touch

I'm actively looking for my first software engineering role. If you have feedback, opportunities, or just want to chat about the project:

LinkedIn: https://www.linkedin.com/in/richard-hawes-a4533399/
GitHub: https://github.com/rchawes


Thank you!
=======
Thanks for checking out my project.

