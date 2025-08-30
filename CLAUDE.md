# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
This is a Spring Boot 3.5.5 application named "jober" built with Java 21 and Maven. It serves as a backend API with support for web services, database migrations, and Thymeleaf templating.

## Common Commands

### Build and Run
- `./mvnw clean install` - Build the project and run tests
- `./mvnw spring-boot:run` - Run the application in development mode
- `./mvnw test` - Run all tests
- `./mvnw test -Dtest=JoberApplicationTests` - Run a specific test class

### Development
- `./mvnw spring-boot:run` - Starts the application with Spring Boot DevTools for hot reload
- Application runs on default port 8080
- Test endpoint available at `/api/hello`

## Architecture

### Package Structure
- `com.ezlevup.jober` - Root package containing main application class
- `com.ezlevup.jober.controller` - REST controllers for API endpoints

### Key Technologies
- **Spring Boot 3.5.5** - Main application framework
- **Spring Web** - REST API support
- **Thymeleaf** - Template engine for server-side rendering
- **Flyway** - Database migration management
- **MySQL/H2** - Database support (MySQL for production, H2 for development/testing)
- **Lombok** - Code generation for boilerplate reduction
- **Spring Boot DevTools** - Development-time enhancements

### Database Configuration
- Flyway is configured for database migrations
- Supports both MySQL (production) and H2 (development/testing)
- Migration files should be placed in `src/main/resources/db/migration/`

### API Structure
- REST controllers use `@RestController` annotation
- CORS is configured with `@CrossOrigin` for cross-origin requests
- API endpoints follow `/api/*` pattern
- JSON responses are automatically handled by Spring Boot

### Testing
- Uses JUnit 5 with Spring Boot Test
- Main test class: `JoberApplicationTests.java`
- Integration tests use `@SpringBootTest` annotation