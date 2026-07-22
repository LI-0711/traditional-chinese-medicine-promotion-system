# Traditional Chinese Medicine Promotion System

[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.5-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![CI](https://github.com/LI-0711/traditional-chinese-medicine-promotion-system/actions/workflows/ci.yml/badge.svg)](https://github.com/LI-0711/traditional-chinese-medicine-promotion-system/actions/workflows/ci.yml)

A full-stack educational platform that makes Traditional Chinese Medicine (TCM) knowledge easier to discover and learn. The system combines a multilingual herb catalogue, knowledge quizzes, personal learning progress, favourites, and profile management in one responsive web application.

> Portfolio project by Li Peilin, Bachelor of Software Engineering (Information Systems), Universiti Kebangsaan Malaysia (UKM).

## Highlights

- Multilingual interface in English, Chinese, and Malay
- Searchable local herb catalogue with descriptions, usage, and precautions
- Optional live herb discovery backed by the Wikipedia API
- Knowledge library, quizzes, recent results, and Bronze/Silver/Gold progress levels
- Account registration and login with BCrypt password hashing
- Personal favourites and profile avatar upload with size, type, and file-signature validation
- Responsive UI with theme preferences stored in the browser
- Layered Spring architecture: Controller → Service → Repository → MySQL

## Technology Stack

| Area | Technology |
| --- | --- |
| Backend | Java 17, Spring Boot 3, Spring Web |
| Data | Spring Data JPA, Hibernate, MySQL 8 |
| Security | Spring Security, BCrypt |
| Frontend | HTML5, CSS3, vanilla JavaScript |
| Testing | JUnit 5, Spring Boot Test, H2 |
| Tooling | Maven Wrapper, Docker Compose, GitHub Actions |

## Architecture

```text
Browser (responsive HTML/CSS/JS)
        │ REST/JSON
        ▼
Spring MVC Controllers
        ▼
Business Services ──────► Wikipedia API (optional search)
        ▼
Spring Data JPA
        ▼
MySQL 8
```

## Main Modules

| Module | Capabilities |
| --- | --- |
| Account | Register, login, BCrypt password storage, profile details |
| Herb discovery | Browse/search bundled herbs and query an external source |
| Learning | TCM knowledge content, quizzes, accuracy, topic statistics |
| Personalisation | Favourites, five most recent quiz attempts, progress rank |
| Avatar | JPG/PNG/WebP upload, 5 MB limit, signature validation, safe paths |

## Run Locally

Prerequisites: Java 17 and Docker Desktop (or an existing MySQL 8 instance).

1. Start MySQL:

   ```bash
   docker compose up -d db
   ```

2. Set the database variables. The values below match `compose.yaml`:

   **PowerShell**

   ```powershell
   $env:DB_URL="jdbc:mysql://localhost:3306/tcm_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai"
   $env:DB_USERNAME="tcm_user"
   $env:DB_PASSWORD="tcm_dev_password"
   ```

3. Start the application:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

4. Open [http://localhost:8080](http://localhost:8080).

Hibernate creates the required tables on first launch. Uploaded avatars are stored under `uploads/avatars/` and are excluded from Git.

## Configuration

| Variable | Purpose | Local default |
| --- | --- | --- |
| `DB_URL` | JDBC connection URL | Local `tcm_system` database |
| `DB_USERNAME` | Database username | `tcm_user` |
| `DB_PASSWORD` | Database password | `change-me` |
| `AVATAR_UPLOAD_DIR` | Avatar storage directory | `uploads/avatars` |
| `JPA_SHOW_SQL` | Print SQL statements | `false` |

Never commit real credentials. Use environment variables or a secret manager outside local development.

## Testing

```powershell
.\mvnw.cmd clean test
```

Tests use an isolated in-memory H2 database. GitHub Actions runs the same test suite on every push and pull request.

## API Overview

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/user/register` | Create an account |
| `POST` | `/user/login` | Verify credentials |
| `GET` | `/user/profile` | Retrieve profile information |
| `POST` | `/user/avatar` | Upload a validated avatar |
| `GET` | `/herb/search` | Search database herbs |
| `GET` | `/herb/external/search` | Search the external herb source |
| `POST` | `/favorite/add` | Save a favourite herb |
| `GET` | `/favorite/list` | List a user's favourites |
| `POST` | `/quiz/save` | Save a quiz attempt |
| `GET` | `/quiz/progress` | Retrieve accumulated learning progress |

## Project Status and Roadmap

The core learning experience is implemented. Planned improvements include token-based authentication, role-based administration, API documentation with OpenAPI, database migrations, and broader automated test coverage.

This application is an educational portfolio project. Herb content is for general learning only and is not medical advice.

## Documentation

- [Requirements analysis](documentation/requirement-analysis.md)
- [Repository documentation](documentation/README.md)

## Author

Li Peilin — Software Engineering (Information Systems), UKM
