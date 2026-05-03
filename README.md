# Hotel Reservation System

A comprehensive web-based hotel reservation management system built with Java, Jakarta EE, and MySQL. This enterprise-grade application demonstrates advanced Java web development patterns, database design, and security best practices.

![Java](https://img.shields.io/badge/Java-11+-orange)
![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-6.0-blue)
![Maven](https://img.shields.io/badge/Maven-3.8+-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue)
![License](https://img.shields.io/badge/License-MIT-green)

## 📋 Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [Security Highlights](#security-highlights)
- [Database Schema](#database-schema)
- [API & Controllers](#api--controllers)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

**Ocean View Resort - Hotel Reservation System** is a full-featured hotel management platform designed for small to medium-sized hotels. It enables staff members to manage room inventory, handle guest reservations, process billing, and maintain comprehensive reservation history. The system implements role-based access control, email notifications, and sophisticated reservation availability checking to prevent double-booking scenarios.

### Key Highlights:

- ✅ **Role-Based Access Control** (Admin & Receptionist)
- ✅ **Advanced Availability Checking** (Double-booking prevention at database level)
- ✅ **Automated Email Notifications** (Reservations & password resets)
- ✅ **Secure Authentication** (bcrypt password hashing, password reset tokens)
- ✅ **Comprehensive Billing System** (Dynamic pricing with extras & discounts)
- ✅ **RESTful Servlet Architecture** (Clean separation of concerns)
- ✅ **Connection Pooling** (HikariCP for performance)

## ✨ Key Features

### 1. **Reservation Management**

- Create, view, and manage guest reservations
- Filter reservations by status (CONFIRMED, COMPLETED, CANCELLED)
- Intelligent double-booking prevention
- Guest information capture (name, contact, address, special requests)
- Automated email notifications to guests
- Date-based availability checking

### 2. **Room Management**

- Complete room inventory system (4 room types: SINGLE, DOUBLE, DELUXE, SUITE)
- Dynamic pricing per room type
- Room availability tracking
- Maintenance status management
- Room facility associations (Food, Amenities, Services)
- Extra pricing for premium facilities

### 3. **Billing & Checkout**

- Automated bill generation based on stay duration
- Discount application system
- Extra facility charges calculation
- Comprehensive billing history
- Billing email notifications to guests
- Currency precision (2 decimal places)

### 4. **User Authentication & Authorization**

- Secure user registration and login
- Role-based access control (ADMIN, RECEPTIONIST)
- Password reset with token-based workflow
- Session management
- Secure password hashing with bcrypt
- 30-minute password reset token validity

### 5. **Admin Features**

- Staff user management
- Room inventory management
- View system-wide analytics
- Dashboard with key statistics
- Add/edit room details
- Manage room facilities

### 6. **Dashboard & Analytics**

- Real-time dashboard with key metrics
- Reservation statistics
- Room occupancy overview
- Revenue tracking
- User activity monitoring

## 🛠️ Technology Stack

### Backend


| Technology          | Version | Purpose                    |
| ------------------- | ------- | -------------------------- |
| **Java**            | 11+     | Core language              |
| **Jakarta EE**      | 6.0     | Servlet & JSP framework    |
| **Jakarta Servlet** | 6.1.0   | HTTP request handling      |
| **Jakarta Mail**    | 2.0.4   | Email notifications (SMTP) |
| **Jakarta JSP API** | 3.1.0   | Server-side templating     |

### Database


| Technology          | Version | Purpose             |
| ------------------- | ------- | ------------------- |
| **MySQL**           | 8.0+    | Relational database |
| **HikariCP**        | 5.1.0   | Connection pooling  |
| **MySQL Connector** | 8.4.0   | JDBC driver         |

### Security & Utilities


| Technology      | Version | Purpose                   |
| --------------- | ------- | ------------------------- |
| **jBCrypt**     | 0.4     | Password hashing          |
| **dotenv-java** | 3.0.0   | Environment configuration |
| **SLF4J**       | 1.7.36  | Logging framework         |

### Development & Testing


| Technology   | Version | Purpose               |
| ------------ | ------- | --------------------- |
| **Maven**    | 3.8+    | Build management      |
| **JUnit 5**  | 5.13.2  | Unit testing          |
| **Mockito**  | 5.14.2  | Mocking framework     |
| **SpotBugs** | 4.9.8   | Code quality analysis |

## 🏗️ Architecture

### Layered Architecture Pattern

```
┌─────────────────────────────────────┐
│     Presentation Layer (JSP)        │
│  Views, Forms, User Interface       │
└──────────────────┬──────────────────┘
                   │
┌──────────────────▼──────────────────┐
│    Controllers Layer (Servlets)     │
│  Request handling, routing, filters │
└──────────────────┬──────────────────┘
                   │
┌──────────────────▼──────────────────┐
│     Service Layer (Business Logic)  │
│  Reservations, User, Email Service  │
└──────────────────┬──────────────────┘
                   │
┌──────────────────▼──────────────────┐
│  Data Access Layer (DAO Pattern)    │
│  User, Reservation, Room DAOs       │
└──────────────────┬──────────────────┘
                   │
┌──────────────────▼──────────────────┐
│      Database Layer (MySQL)         │
│  User, Room, Reservation Tables     │
└─────────────────────────────────────┘
```

### Design Patterns Used

1. **Singleton Pattern** - ApplicationComponents (Dependency Injection)
2. **Data Mapper Pattern** - DAO layer converts ResultSet to model objects
3. **Factory Pattern** - Service and DAO instantiation
4. **Repository Pattern** - DAO interfaces for data abstraction
5. **DTO Pattern** - Data Transfer Objects for API responses
6. **Filter Pattern** - AuthFilter for cross-cutting concerns

### Key Architectural Decisions

- **Manual DI Container**: Uses `ApplicationComponents` singleton instead of Spring for lightweight deployment
- **PreparedStatements**: All database queries use parameterized queries to prevent SQL injection
- **Connection Pooling**: HikariCP with optimized settings (min 2, max 10 connections)
- **Database-Level Constraints**: Unique constraints, foreign keys, and complex queries for data integrity

## 📋 Prerequisites

Before running the application, ensure you have the following installed:

- **Java Development Kit (JDK)** 11 or higher

  ```bash
  java -version  # Should show Java 11+
  ```
- **Apache Maven** 3.8.0 or higher

  ```bash
  mvn -version   # Should show Maven 3.8+
  ```
- **MySQL Server** 8.0 or higher

  ```bash
  mysql --version  # Should show MySQL 8.0+
  ```
- **Git** (for version control)

  ```bash
  git --version
  ```
- **IDE/Text Editor** (Recommended: IntelliJ IDEA, VS Code, or Eclipse)

## 💻 Installation & Setup

### Step 1: Clone the Repository

```bash
# Clone the repository
git clone https://github.com/DilakshaDissanayake/Hotel-Reservation-System.git

# Navigate to project directory
cd Hotel-Reservation-System
```

### Step 2: Database Setup

```bash
# Open MySQL client
mysql -u root -p

# Execute the database creation script
source database/hotel_reservation_db_create.sql

# Execute the database seeding script (optional - adds sample data)
source database/hotel_reservation_db_seed.sql

# Exit MySQL
exit
```

### Step 3: Configure Environment Variables

Create a `.env` file in the project root directory:

```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=hotel_reservation_db
DB_USER=your_mysql_username
DB_PASSWORD=your_mysql_password

# Email Configuration (SMTP)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
MAIL_FROM=noreply@hotelreservation.com

# Email Settings
MAIL_SMTP_AUTH=true
MAIL_START_TLS=true
MAIL_SSL_ENABLE=false

# Server Configuration
SERVER_PORT=8080
LOG_LEVEL=INFO
```

### Step 4: Build the Project

```bash
# Clean and build the project
mvn clean package

# Or just compile without creating WAR
mvn clean compile
```

## ⚙️ Configuration

### Environment Variables Reference


| Variable          | Required | Default              | Description                          |
| ----------------- | -------- | -------------------- | ------------------------------------ |
| `DB_HOST`         | Yes      | localhost            | MySQL server hostname                |
| `DB_PORT`         | Yes      | 3306                 | MySQL server port                    |
| `DB_NAME`         | Yes      | hotel_reservation_db | Database name                        |
| `DB_USER`         | Yes      | root                 | MySQL username                       |
| `DB_PASSWORD`     | Yes      | -                    | MySQL password                       |
| `MAIL_HOST`       | Yes      | -                    | SMTP server address                  |
| `MAIL_PORT`       | Yes      | 587                  | SMTP port (587 for TLS, 465 for SSL) |
| `MAIL_USERNAME`   | Yes      | -                    | SMTP authentication username         |
| `MAIL_PASSWORD`   | Yes      | -                    | SMTP authentication password         |
| `MAIL_FROM`       | Yes      | -                    | Sender email address                 |
| `MAIL_SMTP_AUTH`  | No       | true                 | Enable SMTP authentication           |
| `MAIL_START_TLS`  | No       | true                 | Enable STARTTLS                      |
| `MAIL_SSL_ENABLE` | No       | false                | Enable SSL (use with port 465)       |

### Gmail Configuration Example

If using Gmail for SMTP:

1. Enable 2-Factor Authentication on your Google account
2. Generate an App Password: https://myaccount.google.com/apppasswords
3. Use the generated password in `MAIL_PASSWORD`

```env
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your.email@gmail.com
MAIL_PASSWORD=xxxx xxxx xxxx xxxx  # 16-character app password
MAIL_FROM=your.email@gmail.com
```

## 🚀 Running the Application

### Option 1: Using Apache Tomcat (Recommended)

```bash
# Build the WAR file
mvn clean package -DskipTests

# Deploy to Tomcat
# Copy target/Hotel-Reservation-System-1.0-SNAPSHOT.war to TOMCAT_HOME/webapps/

# Start Tomcat
cd $TOMCAT_HOME/bin
./startup.sh  # On Windows: startup.bat

# Access the application
# Open browser: http://localhost:8080/Hotel-Reservation-System-1.0-SNAPSHOT
```

### Option 2: Using Maven Tomcat Plugin

```bash
# Run directly with Maven
mvn tomcat7:run

# Or with newer Tomcat 9/10
mvn org.codehaus.mojo:tomcat-maven-plugin:2.2:run

# Application will be available at:
# http://localhost:8080/Hotel-Reservation-System
```

### Option 3: Using Embedded Server (Development)

```bash
# For development with live reload
mvn clean compile
mvn tomcat7:run
```

### Default Login Credentials

After seeding the database, use these credentials:


| Role         | Username     | Password |
| ------------ | ------------ | -------- |
| Admin        | admin        | admin123 |
| Receptionist | receptionist | pass123  |

⚠️ **Important**: Change these credentials in production!

## 📁 Project Structure

```
Hotel-Reservation-System/
├── src/
│   ├── main/
│   │   ├── java/com/example/hotelreservationsystem/
│   │   │   ├── config/                    # Configuration classes
│   │   │   │   └── ApplicationComponents.java     # DI Container
│   │   │   │   └── DatabaseConnectionPool.java   # Connection pooling
│   │   │   │   └── EmailConfig.java              # Email settings
│   │   │   │
│   │   │   ├── controller/                # Servlet Controllers
│   │   │   │   ├── AuthController.java           # Login/Registration
│   │   │   │   ├── ReservationController.java    # Reservation CRUD
│   │   │   │   ├── RoomController.java           # Room management
│   │   │   │   ├── BillingController.java        # Billing operations
│   │   │   │   ├── DashboardController.java      # Dashboard analytics
│   │   │   │   └── ...
│   │   │   │
│   │   │   ├── service/                  # Business Logic Layer
│   │   │   │   ├── ReservationService.java       # Reservation interface
│   │   │   │   ├── impl/
│   │   │   │   │   ├── ReservationServiceImpl.java
│   │   │   │   │   └── EmailServiceImpl.java
│   │   │   │   └── UserService.java
│   │   │   │
│   │   │   ├── dao/                      # Data Access Layer
│   │   │   │   ├── ReservationDAO.java           # Data interface
│   │   │   │   ├── ReservationDAOImpl.java       # Implementation
│   │   │   │   └── UserDAO.java
│   │   │   │
│   │   │   ├── model/                    # Entity Classes
│   │   │   │   ├── User.java
│   │   │   │   ├── Reservations.java
│   │   │   │   ├── Rooms.java
│   │   │   │   ├── Bills.java
│   │   │   │   ├── Facilities.java
│   │   │   │   └── ReservationGuest.java
│   │   │   │
│   │   │   ├── dto/                      # Data Transfer Objects
│   │   │   │   ├── ReservationSummaryDTO.java
│   │   │   │   ├── BillDetailsDTO.java
│   │   │   │   └── DashboardStatsDTO.java
│   │   │   │
│   │   │   ├── filter/                   # HTTP Filters
│   │   │   │   └── AuthFilter.java              # Authentication filter
│   │   │   │
│   │   │   ├── exception/                # Custom Exceptions
│   │   │   │   └── DatabaseException.java
│   │   │   │
│   │   │   └── util/                     # Utility Classes
│   │   │       └── PasswordUtils.java
│   │   │
│   │   ├── resources/                    # Application resources
│   │   │   └── application.properties
│   │   │
│   │   └── webapp/                       # Web Application Files
│   │       ├── index.jsp                         # Home page
│   │       ├── assets/
│   │       │   ├── css/
│   │       │   │   └── app.css                   # Main stylesheet
│   │       │   ├── js/
│   │       │   │   └── app.js                    # Client-side logic
│   │       │   └── images/
│   │       │
│   │       └── WEB-INF/
│   │           ├── views/                        # JSP templates
│   │           │   ├── auth/                     # Auth pages
│   │           │   ├── reservations/             # Reservation views
│   │           │   ├── room/                     # Room views
│   │           │   ├── biling/                   # Billing views
│   │           │   ├── dashboard/                # Dashboard views
│   │           │   ├── profile/                  # Profile views
│   │           │   ├── staff/                    # Staff management
│   │           │   └── include/                  # Shared components
│   │           │       ├── header.jsp            # Navigation bar
│   │           │       ├── footer.jsp            # Footer
│   │           │       └── app.jsp               # Master layout
│   │           └── web.xml                       # Deployment descriptor
│   │
│   └── test/
│       ├── java/com/example/hotelreservationsystem/
│       │   ├── integration/                      # Integration tests
│       │   ├── filter/                           # Filter tests
│       │   ├── service/                          # Service tests
│       │   └── ...
│       └── resources/                            # Test resources
│
├── database/
│   ├── hotel_reservation_db_create.sql   # Database schema
│   └── hotel_reservation_db_seed.sql     # Sample data
│
├── docs/
│   └── hotel-usecase-advanced.puml       # Use case diagram
│
├── pom.xml                               # Maven configuration
├── mvnw                                  # Maven wrapper (Unix)
├── mvnw.cmd                              # Maven wrapper (Windows)
└── README.md                             # This file
```

## 🔒 Security Highlights

### Authentication & Authorization

- **Password Hashing**: Uses bcrypt with cost factor 12 (2^12 = 4096 rounds)
- **Session Management**: Server-side session tracking with role-based access
- **AuthFilter**: Implements @WebFilter to enforce authentication globally
- **Password Reset Tokens**: 30-minute validity, single-use, SHA-256 hashed
- **Minimum Password Requirements**: 8+ characters enforced

### Database Security

- **Parameterized Queries**: All PreparedStatements prevent SQL injection
- **Foreign Key Constraints**: Referential integrity at database level
- **Unique Constraints**: Username and email uniqueness enforced
- **Row-Level Locking**: FOR UPDATE prevents race conditions

### Double-Booking Prevention

Implemented at database level using NOT EXISTS subquery:

```sql
NOT EXISTS (
    SELECT 1 FROM reservations 
    WHERE room_id = ? 
    AND status != 'CANCELLED'
    AND NOT (check_out_date <= ? OR check_in_date >= ?)
)
```

### Role-Based Access Control

```
PUBLIC PATHS (No login required):
├── /login
├── /forgot-password
├── /reset-password
└── /assets/*

ADMIN-ONLY:
├── /staff
├── /rooms/edit
└── /rooms (POST)

AUTHENTICATED (Any role):
├── /reservations
├── /dashboard
├── /billing
└── /profile
```

## 🗄️ Database Schema

### Key Tables

#### Users Table

```sql
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(60) NOT NULL,
  last_name VARCHAR(60) NOT NULL,
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(120) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('ADMIN','RECEPTIONIST') DEFAULT 'RECEPTIONIST',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Rooms Table

```sql
CREATE TABLE rooms (
  id INT AUTO_INCREMENT PRIMARY KEY,
  room_number VARCHAR(20) NOT NULL UNIQUE,
  room_type ENUM('SINGLE','DOUBLE','DELUXE','SUITE') NOT NULL,
  rate_per_night DECIMAL(10,2) NOT NULL,
  status ENUM('AVAILABLE','MAINTENANCE') DEFAULT 'AVAILABLE'
);
```

#### Reservations Table

```sql
CREATE TABLE reservations (
  reservation_id BIGINT PRIMARY KEY,
  guest_count INT DEFAULT 1,
  room_type ENUM('SINGLE','DOUBLE','DELUXE','SUITE') NOT NULL,
  check_in_date DATE NOT NULL,
  check_out_date DATE NOT NULL,
  room_id INT NOT NULL,
  status ENUM('CONFIRMED','COMPLETED','CANCELLED') DEFAULT 'CONFIRMED',
  FOREIGN KEY (room_id) REFERENCES rooms(id),
  INDEX idx_res_room_dates (room_id, check_in_date, check_out_date)
);
```

#### Bills Table

```sql
CREATE TABLE bills (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  reservation_id BIGINT NOT NULL UNIQUE,
  number_of_nights INT NOT NULL,
  rate_per_night DECIMAL(10,2) NOT NULL,
  extra_charges DECIMAL(10,2) DEFAULT 0,
  discount DECIMAL(10,2) DEFAULT 0,
  total_cost DECIMAL(10,2) NOT NULL,
  FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id)
);
```

### Database Relationships

```
users (1) ─┬─ (Many) password_reset_tokens
           │
           └─ (Many) reservations_created_by_user

rooms (1) ─┬─ (Many) reservations
           │
           ├─ (Many) room_facilities
           │
           └─ (Many) bills

facilities (1) ─ (Many) room_facilities

reservations (1) ─┬─ (1) rooms
                 ├─ (1) bills
                 │
                 └─ (Many) reservation_guests
```

## 🎮 API & Controllers

### AuthController

```
POST   /login              - Authenticate user
POST   /register           - Create new user account
GET    /logout             - End user session
GET    /forgot-password    - Password recovery form
POST   /reset-password     - Reset password with token
```

### ReservationController

```
GET    /reservations           - List all reservations
POST   /reservations           - Create new reservation
GET    /reservations/create    - Show creation form
GET    /reservations/{id}      - View reservation details
POST   /reservations/{id}      - Update reservation
DELETE /reservations/{id}      - Cancel reservation
```

### RoomController

```
GET    /rooms         - List available rooms
POST   /rooms         - Add new room (Admin)
GET    /rooms/{id}    - View room details
GET    /rooms/edit    - Edit room form (Admin)
```

### BillingController

```
GET    /billing                 - View billing dashboard
GET    /billing/{reservationId} - Generate bill
POST   /billing/{reservationId} - Process payment
```

### DashboardController

```
GET    /dashboard   - View dashboard with analytics
GET    /dashboard/stats - Get JSON statistics
```

### ProfileController

```
GET    /profile              - View user profile
POST   /profile              - Update profile
GET    /profile/settings     - Manage settings
```

## 🧪 Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ReservationServiceImplTest

# Run tests with coverage
mvn test jacoco:report

# Run integration tests only
mvn test -Dtest=*IntegrationTest
```

### Test Categories

#### Unit Tests

- Service layer business logic
- DAO query generation
- Model validation
- Utility functions

#### Integration Tests

- Controller → Service → DAO flow
- Database integration
- Email notification triggering
- Authentication & authorization

#### Filter Tests

- AuthFilter public/protected path routing
- Role-based access control
- Session validation

### Test Coverage

The project includes comprehensive tests:

- **ServiceImplTest**: 90%+ coverage of business logic
- **ControllerIntegrationTest**: End-to-end request handling
- **AuthFilterTest**: Security rule validation
- **DTOTest**: Data transfer object serialization

# 🤝 Contributing

We welcome contributions to improve the Hotel Reservation System!

### How to Contribute

1. **Fork the repository**

   ```bash
   git clone https://github.com/yourusername/Hotel-Reservation-System.git
   ```
2. **Create a feature branch**

   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Make your changes**

   - Follow the existing code style
   - Add tests for new features
   - Update documentation as needed
4. **Commit your changes**

   ```bash
   git commit -m "Add: description of your feature"
   git commit -m "Fix: description of bug fix"
   git commit -m "Docs: update README"
   ```
5. **Push to your branch**

   ```bash
   git push origin feature/your-feature-name
   ```
6. **Create a Pull Request**

   - Describe your changes clearly
   - Reference any related issues
   - Ensure all tests pass

### Coding Standards

- **Java Style**: Follow Google Java Style Guide
- **Naming**: Use descriptive, camelCase names
- **Comments**: Write clear comments for complex logic
- **Testing**: Write tests for all new features
- **Database**: Use parameterized queries
- **Security**: Never hardcode credentials

### Commit Message Format

```
Type: Brief description (50 chars)

Detailed explanation of changes (72 chars per line)

- Bullet point 1
- Bullet point 2

Fixes: #issue_number
```

Where Type is one of:

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Formatting
- `refactor`: Code restructuring
- `test`: Adding tests
- `chore`: Build/config changes

## 📜 License

This project is licensed under the MIT License - see the LICENSE file for details.

```
MIT License

Copyright (c) 2024 Dilaksha Dissanayake

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

## 📚 Additional Resources

- [Jakarta EE Documentation](https://jakarta.ee/learn/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [HikariCP Guide](https://github.com/brettwooldridge/HikariCP)
- [jBCrypt Documentation](https://www.mindrot.org/projects/jbcrypt/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

## 🐛 Known Issues

- No HTTPS enforcement (add to production deployment)
- No explicit rate limiting on login endpoints
- Consider implementing Argon2 for additional password security
- Session timeout should be explicitly configured

## 📞 Support & Contact

For issues, questions, or feedback:

1. **GitHub Issues**: [Create an issue](https://github.com/DilakshaDissanayake/Hotel-Reservation-System/issues)
2. **Email**: dilaksha@example.com
3. **LinkedIn**: [Dilaksha Dissanayake](https://linkedin.com/in/dilaksha-dissanayake)

## 👨‍💼 Author

**Dilaksha Dissanayake**

- 🎓 BSc in Software Engineering (CMU)
- 💻 Full-stack Java Developer
- 🌐 GitHub: [@DilakshaDissanayake](https://github.com/DilakshaDissanayake)
- 💼 LinkedIn: [Dilaksha Dissanayake](https://linkedin.com/in/dilaksha-dissanayake)

## 🙏 Acknowledgments

- **Jakarta EE Community** for excellent web framework
- **MySQL Community** for reliable database
- **JUnit & Mockito** maintainers for testing tools
- **CMU Instructors** for guidance on advanced programming

<div align="center">

### ⭐ If you find this project helpful, please consider giving it a star!

Made with ❤️ by Dilaksha Dissanayake

**Last Updated**: May 2026

</div>
