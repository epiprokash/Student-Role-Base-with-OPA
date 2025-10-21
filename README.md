# 🧑‍🎓 Student Role-Based Access Control (RBAC) Project

### Built with Spring Boot, PostgreSQL, and Open Policy Agent (OPA)

---

## 📘 Overview

This project demonstrates **Role-Based Access Control (RBAC)** using **Spring Boot**, **PostgreSQL**, and **Open Policy Agent (OPA)**.

There are two roles:

* **STUDENT** — can register, log in, and update their own department & blood group.
* **ADMIN** — has full privileges (can view all students and delete any student).

Authorization is handled through **OPA**, which provides fine-grained, externalized policy control.

---

## 🧩 Tech Stack

| Component                   | Purpose                                               |
| --------------------------- | ----------------------------------------------------- |
| **Spring Boot**             | Main backend framework                                |
| **PostgreSQL**              | Database for user & student data                      |
| **OPA (Open Policy Agent)** | Policy-based access control engine                    |
| **JWT (JSON Web Token)**    | Secure authentication mechanism                       |
| **Docker Compose**          | Orchestration for running OPA & PostgreSQL containers |

---

## ⚙️ Project Structure

```
StudentRbacApplication/
├── src/
│   ├── main/java/com/example/StudentRbacApplication/
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── StudentController.java
│   │   │   └── AdminController.java
│   │   ├── model/
│   │   ├── repo/
│   │   ├── service/
│   │   │   ├── AuthService.java
│   │   │   ├── StudentService.java
│   │   │   └── OpaService.java
│   │   ├── security/
│   │   │   └── JwtUtil.java
│   │   └── StudentRbacApplication.java
│   └── resources/
│       ├── application.properties
│       └── policy.rego
├── docker-compose.yml
└── README.md
```

---

## 🐳 Docker Setup

Before running the Spring Boot app, make sure **Docker** is installed and running.

### 🔹 Step 1: Start OPA and PostgreSQL

Run:

```bash
docker compose up -d
```

This spins up:

* **PostgreSQL** on port **5432**
* **OPA server** on port **8181**

Verify containers:

```bash
docker ps
```

You should see something like:

```
CONTAINER ID   IMAGE                     STATUS
xxxxxx         postgres:15               Up ...
xxxxxx         openpolicyagent/opa:latest Up ...
```

---

## 🔑 Spring Boot Configuration

Your `application.properties` should include:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/studentdb
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update

# OPA endpoint
app.opa-url=http://localhost:8181/v1/data/student/authz/allow

server.port=8081
```

---

## 🧠 OPA Policy (`policy.rego`)

```rego
package student.authz

default allow := false

# Admins can perform any action
allow if {
    input.user.role == "ADMIN"
}

# Students can update their own info (but not delete)
allow if {
    input.user.role == "STUDENT"
    input.action == "student:update"
    input.resource.target == "self"
}
```

OPA receives authorization queries from Spring Boot as JSON like:

```json
{
  "input": {
    "user": {"username": "john", "role": "STUDENT"},
    "action": "student:update",
    "resource": {"target": "self"}
  }
}
```

OPA evaluates the policy and returns:

```json
{"result": {"allow": true}}
```

---

## 🚀 Running the Application

### Step 1: Start Spring Boot

Run the app from your IDE or terminal:

```bash
mvn spring-boot:run
```

The app runs on: **[http://localhost:8081](http://localhost:8081)**

---

## 🧪 Testing APIs (Postman)

### 1️⃣ Register User

**POST** `/auth/register`

**Body:**

```json
{
  "username": "john",
  "password": "123456"
}
```

Response:

```json
{"token": "<JWT_TOKEN>"}
```

By default, new users are created with the **STUDENT** role.
Admins can be created manually or through database update.

---

### 2️⃣ Login

**POST** `/auth/login`

**Body:**

```json
{
  "username": "john",
  "password": "123456"
}
```

Response:

```json
{"token": "<JWT_TOKEN>"}
```

---

### 3️⃣ Student Updates Own Info

**POST** `/student/me`

**Headers:**

```
Authorization: Bearer <JWT_TOKEN>
```

**Body:**

```json
{
  "department": "CSE",
  "bloodGroup": "A+"
}
```

✅ If OPA approves → student info updated
❌ If not → `403 Forbidden by policy`

---

### 4️⃣ Admin Deletes a Student

**DELETE** `/admin/student/{id}`

**Headers:**

```
Authorization: Bearer <ADMIN_TOKEN>
```

Response:

```json
"deleted"
```

OPA checks:

```rego
allow if {
  input.user.role == "ADMIN"
}
```

---

## 🔍 How OPA Communicates with Spring Boot

1. **User makes API call** (e.g., POST `/student/me`).
2. **Spring Boot extracts JWT** → identifies `username` and `role`.
3. **Spring Boot sends OPA request**:

   ```
   POST http://localhost:8181/v1/data/student/authz/allow
   ```

   with payload:

   ```json
   {"input": {"user": {...}, "action": "student:update", "resource": {...}}}
   ```
4. **OPA evaluates policy.rego** → returns `{ "result": {"allow": true} }`
5. **Spring Boot decides** whether to proceed or return 403.

This is called **Externalized Authorization** — policies are managed outside code, by OPA.

---

## 🧱 Why Use Docker for OPA & PostgreSQL?

| Benefit                    | Description                               |
| -------------------------- | ----------------------------------------- |
| **No installation needed** | Run PostgreSQL and OPA instantly          |
| **Isolation**              | Each service runs in its own environment  |
| **Consistency**            | Same version across machines and servers  |
| **Reproducibility**        | Spin up the whole stack with one command  |
| **Ease of integration**    | Containers automatically network together |

---

## 🧰 Useful Commands

| Command                                        | Purpose                  |
| ---------------------------------------------- | ------------------------ |
| `docker compose up -d`                         | Start containers         |
| `docker ps`                                    | Check running containers |
| `docker logs opa_server`                       | View OPA logs            |
| `docker exec -it postgres_db psql -U postgres` | Access PostgreSQL shell  |
| `mvn spring-boot:run`                          | Run Spring Boot app      |

---

## 🛡️ Example JWT Payloads

**Student Token:**

```json
{
  "sub": "john",
  "role": "STUDENT"
}
```

**Admin Token:**

```json
{
  "sub": "admin1",
  "role": "ADMIN"
}
```

---

## 🧭 Summary

| Role        | Permissions                    | Example Action               |
| ----------- | ------------------------------ | ---------------------------- |
| **STUDENT** | Update own info only           | POST `/student/me`           |
| **ADMIN**   | Full access (delete, view all) | DELETE `/admin/student/{id}` |

OPA dynamically decides access control based on **policies**, not hard-coded logic.

---

## 📚 References

* [Open Policy Agent Documentation](https://www.openpolicyagent.org/docs/latest/)
* [Spring Security JWT Guide](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [PostgreSQL Docker Hub](https://hub.docker.com/_/postgres)
* [Docker Compose Documentation](https://docs.docker.com/compose/)

---

> 💡 **Tip:**
> You can modify the OPA policy (`policy.rego`) anytime to change access logic —
> no need to touch the Java code. Just update the policy and restart the OPA container!
