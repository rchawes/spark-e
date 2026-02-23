Spark_e – My First Real Spring Boot Project

Hi, I'm Richard 👋 – I spent 14 years as an electrician and recently decided to switch careers into software engineering. This project is my way of combining my old trade with my new skills. It's a backend API for tradespeople (electricians, plumbers, etc.) to manage customers, jobs, parts, invoices, and compliance checklists. No more spreadsheets and sticky notes!

🧠 Why I Built This
I wanted something that solved a real problem I understood. Electricians juggle a lot: customers, job scheduling, parts inventory, invoicing, and safety paperwork. I've lived it. So I built Spark_e to scratch my own itch – and to prove I can build useful software.

🛠️ What's Inside
Customers, Electricians, Jobs – basic CRUD, relationships between them.

Parts inventory – track stock, get low‑stock alerts.

Invoice automation – when a job is marked COMPLETED, an invoice is generated automatically. It calculates labor (hours worked × hourly rate) and materials (parts used × price).

Compliance checklists – each job type (like "consumer unit replacement") can have a required safety checklist. You mark items complete and get a compliance report.

Reporting – electrician performance (jobs per month, total invoiced), inventory report, customer profitability.

Security – user registration/login with JWT. Endpoints are protected; you need a token.

Swagger UI – auto‑generated API docs at /swagger-ui.html. Makes testing easy.

🧰 Technologies I Used
Java 17

Spring Boot 3.2.4

Spring Data JPA

Spring Security + JWT

H2 (for local dev) / PostgreSQL (for production)

Maven

Swagger (springdoc‑openapi)

📚 What I Learned (the hard way)
JPA relationships – I drew a lot of diagrams to figure out @OneToMany, @ManyToOne, @JoinColumn. And yes, I forgot the mappedBy more than once.

Spring Security – JWT took me three tries. I now understand filters, authentication managers, and why CSRF is disabled for stateless APIs.

Lombok – gave me cryptic errors (something about TypeTag.UNKNOWN). After hours of debugging, I gave up and wrote all getters/setters manually. Honestly, it helped me understand my entities better.

Derived query methods – Spring Data is magic until it isn't. I learned to fall back to @Query when my method names got too fancy.

Debugging – I spent way too much time staring at stack traces. But every error taught me something.

🚀 How to Run It
Locally (with H2)
Clone the repo:
git clone https://github.com/richard/spark_e.git

Open in IntelliJ (or your favorite IDE).

Make sure you have Java 17 and Maven.

Run SparkEApplication.java – the main class.

API will be at http://localhost:8080.
Swagger UI: http://localhost:8080/swagger-ui.html

With Docker (optional, if you add a Dockerfile later)
(I haven't done this yet – on my todo list!)

Production (on Render)
Set up a PostgreSQL database (I used Render's free tier).

Deploy the app as a web service on Render, with these environment variables:

DATABASE_URL

DATABASE_USERNAME

DATABASE_PASSWORD

JWT_SECRET (a long random string)

SPRING_PROFILES_ACTIVE=prod

Render builds and runs it. Your API will have a live URL.

🧪 Testing the API
After starting the app, hit the Swagger UI to explore endpoints.
To get a token:

POST /api/auth/register with a JSON body like:

json
{
  "username": "jane",
  "password": "pass",
  "role": "ROLE_ELECTRICIAN"
}
POST /api/auth/login with same credentials – returns a token.

Click "Authorize" in Swagger and enter Bearer <token>.

Then you can try creating customers, jobs, etc.

🧗 What's Next
Write unit and integration tests (I know I should have done this earlier).

Maybe a simple React frontend – I want to see if I can build a full‑stack app.

Deploy properly and share the live link on my resume.

🙏 Thanks
If you're reading this, thanks for checking out my project. I'm actively looking for my first developer job – if you have feedback or want to chat, reach out!

Richard
[https://www.linkedin.com/in/richard-hawes-a4533399/] | [https://github.com/rchawes]
