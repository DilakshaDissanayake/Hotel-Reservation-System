# Hotel Reservation System - Complete Project Documentation

**Version**: 1.0-SNAPSHOT
**Last Updated**: May 2026
**Author**: Dilaksha Dissanayake
**Status**: Production Ready

## 📑 Table of Contents

1. [System Overview](#system-overview)
2. [Architecture & Design](#architecture--design)
3. [Technology Stack Details](#technology-stack-details)
4. [Module Documentation](#module-documentation)
5. [Data Flow & Processes](#data-flow--processes)
6. [API Reference](#api-reference)
7. [Database Documentation](#database-documentation)
8. [Security Architecture](#security-architecture)
9. [Configuration Guide](#configuration-guide)
10. [Deployment Guide](#deployment-guide)
11. [Development Guide](#development-guide)
12. [Troubleshooting](#troubleshooting)
13. [Performance Tuning](#performance-tuning)
14. [Maintenance & Operations](#maintenance--operations)

## System Overview

### What is the Hotel Reservation System?

The Hotel Reservation System is an enterprise-grade web application designed to manage hotel operations. It provides a comprehensive solution for:

- **Reservation Management**: Create, modify, and track guest reservations
- **Room Inventory**: Manage room availability and details
- **Billing System**: Generate and track bills for completed stays
- **User Management**: Handle staff accounts with role-based access
- **Email Notifications**: Automated communication with guests
- **Analytics Dashboard**: Real-time insights into hotel operations

### Core Business Functions

1. **Reservation Workflow**

   - Guest arrives → Receptionist creates reservation
   - System checks room availability
   - Reservation stored with guest details
   - Notification sent to guest
   - Check-in → Stay → Check-out
   - Bill generated automatically
   - Billing email sent to guest
2. **Room Management**

   - Track room inventory by type
   - Manage room availability
   - Associate facilities with rooms
   - Track maintenance status
3. **User Access Control**

   - Admin: Full system access, staff management, room operations
   - Receptionist: Reservation management, billing, guest interaction

### System Constraints & Business Rules

```
BUSINESS RULES:
├── No double-booking: Same room cannot have overlapping reservations
├── Room Types: SINGLE, DOUBLE, DELUXE, SUITE (fixed enum)
├── Reservation Status: CONFIRMED, COMPLETED, CANCELLED
├── Room Status: AVAILABLE, MAINTENANCE
├── Role-Based Access: ADMIN, RECEPTIONIST, USER (guest)
├── Password Policy: Minimum 8 characters, bcrypt hashed
├── Bill Generation: Automatic on checkout
├── Notification: Email on reservation & billing
└── Session Management: Server-side with timeout

CONSTRAINTS:
├── Each room: Unique room number
├── Each user: Unique username & email
├── Reservation ID: BIGINT to handle high volume
├── Decimal Precision: 2 decimal places for pricing
└── Date Range: Check-out must be after check-in
```

---

## Architecture & Design

### Layered Architecture Deep Dive

```
┌─────────────────────────────────────────────────────────────────┐
│                   PRESENTATION LAYER                            │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  JSP Templates (.jsp files in WEB-INF/views)            │   │
│  │  ├── auth/ (login, register, forgot password)           │   │
│  │  ├── reservations/ (list, create, details)              │   │
│  │  ├── room/ (inventory, edit)                            │   │
│  │  ├── biling/ (view bills, payment)                      │   │
│  │  ├── dashboard/ (analytics, statistics)                 │   │
│  │  ├── profile/ (user settings)                           │   │
│  │  ├── staff/ (staff management)                          │   │
│  │  └── include/ (header, footer, layout templates)        │   │
│  └─────────────────────────────────────────────────────────┘   │
└────────────────────────────────┬────────────────────────────────┘
                                 │
┌────────────────────────────────▼────────────────────────────────┐
│                  REQUEST LAYER (Filters)                        │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  AuthFilter (@WebFilter("/*"))                          │   │
│  │  ├── Intercepts ALL requests                            │   │
│  │  ├── Checks authentication status                       │   │
│  │  ├── Enforces role-based access                         │   │
│  │  ├── Manages public vs protected routes                 │   │
│  │  └── Handles session validation                         │   │
│  └─────────────────────────────────────────────────────────┘   │
└────────────────────────────────┬────────────────────────────────┘
                                 │
┌────────────────────────────────▼────────────────────────────────┐
│                    CONTROLLER LAYER                             │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  Servlet Controllers (HttpServlet extensions)           │   │
│  │  ├── AuthController                                     │   │
│  │  │   ├── doPost() - login, register                    │   │
│  │  │   └── doGet() - forgot password form                │   │
│  │  ├── ReservationController                              │   │
│  │  │   ├── doGet() - list, details                       │   │
│  │  │   └── doPost() - create, update                     │   │
│  │  ├── RoomController                                     │   │
│  │  │   ├── doGet() - list rooms                          │   │
│  │  │   └── doPost() - add room (admin)                   │   │
│  │  ├── BillingController                                  │   │
│  │  │   ├── doGet() - view bill                           │   │
│  │  │   └── doPost() - process payment                    │   │
│  │  └── [Other Controllers...]                             │   │
│  └─────────────────────────────────────────────────────────┘   │
│  Responsibilities:                                              │
│  ├── Parse HTTP requests                                       │
│  ├── Validate input parameters                                 │
│  ├── Call service layer methods                                │
│  ├── Handle responses & redirects                              │
│  └── Error handling & logging                                  │
└────────────────────────────────┬────────────────────────────────┘
                                 │
┌────────────────────────────────▼────────────────────────────────┐
│                     SERVICE LAYER                               │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  ReservationServiceImpl (Interface: ReservationService)  │   │
│  │  ├── createReservation()                                │   │
│  │  │   ├── Validate dates & room                         │   │
│  │  │   ├── Check availability                            │   │
│  │  │   ├── Create reservation via DAO                    │   │
│  │  │   ├── Insert guest details                          │   │
│  │  │   └── Trigger email notification                    │   │
│  │  ├── getRooms(), addRoom(), updateRoom()               │   │
│  │  ├── calculateBill()                                    │   │
│  │  └── [Business logic methods]                           │   │
│  │                                                          │   │
│  │  EmailServiceImpl (Interface: EmailService)              │   │
│  │  ├── sendPasswordResetEmail()                           │   │
│  │  ├── sendBillingEmail()                                 │   │
│  │  └── sendReservationConfirmation()                      │   │
│  │                                                          │   │
│  │  UserServiceImpl (Interface: UserService)                │   │
│  │  ├── authenticate()                                     │   │
│  │  ├── registerUser()                                     │   │
│  │  └── resetPassword()                                    │   │
│  └─────────────────────────────────────────────────────────┘   │
│  Responsibilities:                                              │
│  ├── Implement business rules                                  │
│  ├── Coordinate with DAOs                                      │
│  ├── Perform calculations & validations                        │
│  ├── Handle transactions                                       │
│  └── Integrate with external services (email)                  │
└────────────────────────────────┬────────────────────────────────┘
                                 │
┌────────────────────────────────▼────────────────────────────────┐
│                       DAO LAYER                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  ReservationDAOImpl (Interface: ReservationDAO)           │   │
│  │  ├── insertReservation()                                │   │
│  │  ├── getReservation()                                   │   │
│  │  ├── updateReservation()                                │   │
│  │  ├── deleteReservation()                                │   │
│  │  ├── checkAvailability()                                │   │
│  │  ├── getRooms(), insertRoom()                           │   │
│  │  ├── insertBill(), getBills()                           │   │
│  │  └── [SQL operations]                                   │   │
│  │                                                          │   │
│  │  UserDAOImpl (Interface: UserDAO)                         │   │
│  │  ├── findByUsername()                                   │   │
│  │  ├── insertUser()                                       │   │
│  │  ├── updatePassword()                                   │   │
│  │  └── [User SQL operations]                              │   │
│  └─────────────────────────────────────────────────────────┘   │
│  Responsibilities:                                              │
│  ├── Execute SQL queries                                       │
│  ├── Convert ResultSet to objects                              │
│  ├── Manage transactions                                       │
│  ├── Use parameterized queries                                 │
│  └── Handle database errors                                    │
└────────────────────────────────┬────────────────────────────────┘
                                 │
┌────────────────────────────────▼────────────────────────────────┐
│                       DATABASE LAYER                            │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  MySQL 8.0+ (hotel_reservation_db)                      │   │
│  │  ├── users                                              │   │
│  │  ├── rooms                                              │   │
│  │  ├── reservations                                       │   │
│  │  ├── bills                                              │   │
│  │  ├── facilities                                         │   │
│  │  ├── room_facilities                                    │   │
│  │  ├── reservation_guests                                 │   │
│  │  ├── password_reset_tokens                              │   │
│  │  └── [Other tables]                                     │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### Design Patterns Used

#### 1. **Singleton Pattern - Dependency Injection Container**

```java
// ApplicationComponents.java - Manual DI Container
public class ApplicationComponents {
    private static ApplicationComponents instance;
    private ReservationService reservationService;
    private EmailService emailService;
    private UserService userService;

    private ApplicationComponents() {
        // Initialize services
        this.reservationService = new ReservationServiceImpl();
        this.emailService = new EmailServiceImpl();
        this.userService = new UserServiceImpl();
    }

    public static synchronized ApplicationComponents getInstance() {
        if (instance == null) {
            instance = new ApplicationComponents();
        }
        return instance;
    }

    public ReservationService getReservationService() {
        return reservationService;
    }
}
```

**Usage in Controller**:

```java
public class ReservationController extends HttpServlet {
    private ReservationService reservationService = 
        ApplicationComponents.getInstance().getReservationService();
}
```

#### 2. **Data Mapper Pattern - DAO Layer**

```java
// ReservationDAOImpl.java - Maps ResultSet to Objects
public Reservations mapResultSetToReservation(ResultSet rs) throws SQLException {
    Reservations reservation = new Reservations();
    reservation.setReservationId(rs.getLong("reservation_id"));
    reservation.setRoomId(rs.getInt("room_id"));
    reservation.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
    reservation.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
    reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));
    return reservation;
}
```

#### 3. **Factory Pattern - Service Instantiation**

```java
// ApplicationComponents acts as factory
public ReservationService createReservationService() {
    return new ReservationServiceImpl();
}

public EmailService createEmailService() {
    return new EmailServiceImpl();
}
```

#### 4. **Repository Pattern - DAO Interfaces**

```java
// ReservationDAO.java - Repository interface
public interface ReservationDAO {
    Reservations getReservation(long reservationId);
    List<Reservations> getAllReservations();
    void insertReservation(Reservations reservation);
    void updateReservation(Reservations reservation);
    void deleteReservation(long reservationId);
}
```

#### 5. **DTO Pattern - Data Transfer Objects**

```java
// BillDetailsDTO.java - Transfer data without exposing entity
public class BillDetailsDTO {
    private Long reservationId;
    private Integer numberOfNights;
    private BigDecimal ratePerNight;
    private BigDecimal extraCharges;
    private BigDecimal discount;
    private BigDecimal totalCost;
    // getters/setters
}
```

#### 6. **Filter Pattern - Cross-Cutting Concerns**

```java
// AuthFilter.java - Global authentication filter
@WebFilter("/*")
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        // Authentication logic
        // Role-based access control
        chain.doFilter(request, response);
    }
}
```

## Technology Stack Details

### Backend Framework Details

#### Jakarta EE (Previously Java EE)

- **Version**: 6.0
- **Jakarta Servlet API**: 6.1.0 - HTTP protocol handling
- **Jakarta JSP API**: 3.1.0 - Server-side templating engine
- **Jakarta Mail**: 2.0.4 - Email service

```xml
<!-- From pom.xml -->
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>6.1.0</version>
    <scope>provided</scope>
</dependency>
```

#### Java Version & Compilation

```xml
<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
</properties>
```

- **Java 11 Features Used**:
  - Local variable type inference (var)
  - Lambda expressions
  - Optional API
  - Default methods in interfaces

### Database Layer

#### MySQL 8.0+

- **Connection Pooling**: HikariCP 5.1.0
- **JDBC Driver**: MySQL Connector/J 8.4.0

**HikariCP Configuration**:

```java
// DatabaseConnectionPool.java
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
config.setUsername(username);
config.setPassword(password);
config.setMaximumPoolSize(10);  // Maximum 10 connections
config.setMinimumIdle(2);       // Keep 2 idle connections
config.setConnectionTimeout(30000);  // 30 seconds
config.setIdleTimeout(600000);       // 10 minutes
config.setMaxLifetime(1800000);      // 30 minutes
```

### Security Framework

#### Password Hashing: jBCrypt 0.4

```java
// Hashing password on registration
String passwordHash = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
// Cost factor 12 = 2^12 = 4096 iterations

// Verifying password on login
boolean isPasswordCorrect = BCrypt.checkpw(plainPassword, storedHash);
```

**Performance Impact**:

- Hashing 8-char password: ~100ms
- Hashing 16-char password: ~100ms
- Cost factor 12 is balanced for security & performance

### Configuration Management

#### dotenv-java 3.0.0

```java
// Load environment variables from .env file
DotEnv dotenv = Dotenv.load();
String dbHost = dotenv.get("DB_HOST");
String mailPassword = dotenv.get("MAIL_PASSWORD");
```

**Environment Variable Priority**:

1. Java system properties
2. .env file variables
3. System environment variables
4. Hardcoded defaults (not recommended)

### Testing Framework

#### JUnit 5 (Jupiter) 5.13.2

```java
@DisplayName("ReservationService Tests")
class ReservationServiceImplTest {
  
    @Test
    @DisplayName("Should create reservation successfully")
    void testCreateReservation() {
        // Test implementation
    }
  
    @ParameterizedTest
    @ValueSource(strings = {"SINGLE", "DOUBLE", "DELUXE", "SUITE"})
    void testAllRoomTypes(String roomType) {
        // Test with multiple values
    }
}
```

#### Mockito 5.14.2

```java
@ExtendWith(MockitoExtension.class)
class ReservationDAOImplTest {
  
    @Mock
    private Connection mockConnection;
  
    @InjectMocks
    private ReservationDAOImpl reservationDAO;
  
    @Test
    void testWithMockedDatabase() {
        when(mockConnection.prepareStatement(anyString()))
            .thenReturn(mockStatement);
        // Test logic
    }
}
```

---

## Module Documentation

### 1. Authentication Module (AuthController & UserService)

**Purpose**: Handle user login, registration, and password management

**Components**:

- `AuthController.java` - Servlet handling auth requests
- `UserService.java/UserServiceImpl.java` - Business logic
- `UserDAO.java/UserDAOImpl.java` - User data access

**Key Workflows**:

##### Login Workflow

```
1. User submits username & password via /login
2. AuthController receives POST request
3. Calls UserService.authenticate(username, password)
4. UserService delegates to UserDAO.findByUsername(username)
5. If user found:
   a. UserService uses BCrypt.checkpw() to verify password
   b. Sets user in HttpSession if password matches
   c. Redirects to dashboard
6. If user not found or password incorrect:
   a. Returns error message
   b. Redirects to login page
```

##### Password Reset Workflow

```
1. User clicks "Forgot Password" on login page
2. Submits email via /forgot-password
3. UserService generates reset token:
   a. Creates 32-character random token
   b. Hashes token with SHA-256
   c. Stores token_hash in password_reset_tokens table
   d. Sets 30-minute expiration
4. EmailService sends reset link to user email
5. User clicks link with token (URL: /reset-password?token=xxx)
6. User submits new password
7. UserService validates token:
   a. Finds token in DB
   b. Checks if not expired
   c. Checks if not already used
   d. Updates user password
   e. Marks token as used
```

**Password Security**:

```java
// Password hashing with bcrypt
BCrypt.hashpw(password, BCrypt.gensalt(12))
// Cost 12 = 2^12 = 4096 rounds
// Takes ~100ms per hash attempt

// Password requirements enforced:
// - Minimum 8 characters
// - No maximum length
// - Any character allowed
```

### 2. Reservation Module (ReservationController & ReservationService)

**Purpose**: Manage all reservation-related operations

**Core Features**:

#### Double-Booking Prevention Algorithm

```sql
-- Check if room is available for date range
SELECT COUNT(*) FROM reservations 
WHERE room_id = ? 
  AND status != 'CANCELLED'
  AND NOT (check_out_date <= ? OR check_in_date >= ?)

-- If count = 0, room is available
-- Logic: Dates don't overlap if:
--   existing_checkout <= new_checkin  OR
--   existing_checkin >= new_checkout
```

**Visual Example**:

```
Existing Reservation:  |----X----|
New Check-in:              ^
New Check-out:                      ^
                      OVERLAP - NOT ALLOWED

Existing Reservation:  |----X----|
New Check-in:                         ^
New Check-out:                            ^
                      NO OVERLAP - ALLOWED
```

#### Reservation Creation Process

```java
public Reservations createReservation(Reservations reservation, 
                                     ReservationGuest guest) {
    // 1. Validate inputs
    validateDates(reservation.getCheckInDate(), 
                  reservation.getCheckOutDate());
    validateRoomType(reservation.getRoomType());
  
    // 2. Check availability (at DB level)
    boolean available = reservationDAO.checkAvailability(
        reservation.getRoomId(),
        reservation.getCheckInDate(),
        reservation.getCheckOutDate()
    );
    if (!available) {
        throw new RoomNotAvailableException("Room is booked");
    }
  
    // 3. Generate reservation ID
    long reservationId = generateUniqueReservationId();
    reservation.setReservationId(reservationId);
  
    // 4. Insert reservation
    reservationDAO.insertReservation(reservation);
  
    // 5. Insert guest details
    guest.setReservationId(reservationId);
    reservationDAO.insertReservationGuest(guest);
  
    // 6. Send confirmation email
    emailService.sendReservationConfirmation(guest.getEmail(), reservation);
  
    return reservation;
}
```

### 3. Billing Module (BillingController & BillingService)

**Purpose**: Generate and manage guest invoices

**Bill Calculation Formula**:

```
totalCost = (nightlyRate × numberOfNights) + extraCharges - discount
if (totalCost < 0) totalCost = 0  // Never negative
```

**Example Calculation**:

```
Room Type: DELUXE
Rate per Night: $150.00
Number of Nights: 3 (Jan 1-4)
Room Facility: Spa Package (+$30/night)

Calculation:
- Base Cost: $150 × 3 = $450
- Extra Charges: $30 × 3 = $90
- Subtotal: $450 + $90 = $540
- Discount Applied: $50
- Total Cost: $540 - $50 = $490
```

**Billing Workflow**:

```
1. Guest checks out
2. BillingController receives checkout request
3. Retrieves reservation and room details
4. Calls BillingService.calculateBill()
5. Queries all facilities linked to room
6. Calculates total cost
7. Inserts bill record (or updates if bill exists)
8. Generates PDF/HTML bill
9. Sends billing email to guest
10. Updates reservation status to COMPLETED
```

### 4. Room Management Module

**Purpose**: Manage hotel room inventory

**Room Types & Pricing**:

```
SINGLE   - $80/night  - Single bed
DOUBLE   - $120/night - Double bed
DELUXE   - $150/night - Two double beds
SUITE    - $250/night - Bedroom + living area
```

**Room Facilities Association**:

```
Each room can have multiple facilities:
├── FOOD
│   ├── Breakfast service (+$15/night)
│   └── Mini bar (+$10/night)
├── AMENITY
│   ├── Spa access (+$30/night)
│   └── Gym access (+$5/night)
├── SERVICE
│   ├── Room service (+$0/night)
│   └── Housekeeping (+$10/night)
└── OTHER
    └── [Custom facilities]
```

**Room Status Workflow**:

```
AVAILABLE (default)
    ↓ (Maintenance needed)
MAINTENANCE
    ↓ (Maintenance completed)
AVAILABLE
    ↓ (Reserved for dates)
AVAILABLE (but has reservation)
    ↓ (Guest checks out)
AVAILABLE (ready for next guest)
```

### 5. Email Notification Module

**Purpose**: Send automated emails for key events

**Email Service Configuration**:

```java
// EmailConfig.java
public class EmailConfig {
    public static final String MAIL_HOST = 
        System.getenv("MAIL_HOST");     // smtp.gmail.com
    public static final int MAIL_PORT = 
        Integer.parseInt(System.getenv("MAIL_PORT", "587"));
    public static final String MAIL_USERNAME = 
        System.getenv("MAIL_USERNAME");
    public static final String MAIL_PASSWORD = 
        System.getenv("MAIL_PASSWORD");
    public static final String MAIL_FROM = 
        System.getenv("MAIL_FROM");     // noreply@hotel.com
}
```

**Email Types**:

1. **Reservation Confirmation**

   - Sent when: Reservation created
   - To: Guest email
   - Contains: Reservation details, check-in/out dates, room info
2. **Password Reset**

   - Sent when: User requests password reset
   - To: User email
   - Contains: Password reset link (valid 30 minutes)
3. **Billing Notification**

   - Sent when: Guest checks out
   - To: Guest email
   - Contains: Bill details, total cost, payment info

**SMTP Implementation**:

```java
public class EmailServiceImpl implements EmailService {
    public void sendEmail(String to, String subject, String body) 
            throws Exception {
        // Configure SMTP properties
        Properties props = new Properties();
        props.put("mail.smtp.host", EmailConfig.MAIL_HOST);
        props.put("mail.smtp.port", EmailConfig.MAIL_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
      
        // Create session with authentication
        Session session = Session.getInstance(props, 
            new Authenticator() {
                @Override
                protected PasswordAuthentication 
                        getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        EmailConfig.MAIL_USERNAME,
                        EmailConfig.MAIL_PASSWORD);
                }
            });
      
        // Create and send message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EmailConfig.MAIL_FROM));
        message.setRecipients(Message.RecipientType.TO, 
            InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);
      
        Transport.send(message);
    }
}
```

### 6. Dashboard & Analytics Module

**Purpose**: Provide business intelligence

**Key Metrics**:

```
Dashboard Statistics:
├── Total Reservations (all time)
├── Active Reservations (CONFIRMED)
├── Completed Reservations (COMPLETED)
├── Cancelled Reservations (CANCELLED)
├── Total Rooms
├── Available Rooms
├── Rooms in Maintenance
├── Total Revenue (sum of bills)
├── Average Occupancy Rate
└── Average Bill Amount
```

**Query Example**:

```sql
SELECT 
    COUNT(CASE WHEN status = 'CONFIRMED' THEN 1 END) as active_reservations,
    COUNT(CASE WHEN status = 'COMPLETED' THEN 1 END) as completed,
    COUNT(CASE WHEN status = 'CANCELLED' THEN 1 END) as cancelled,
    SUM(b.total_cost) as total_revenue,
    AVG(b.total_cost) as avg_bill
FROM reservations r
LEFT JOIN bills b ON r.reservation_id = b.reservation_id
```

## Data Flow & Processes

### Complete Reservation Lifecycle

```
PHASE 1: CREATION
├── Guest contacts hotel (phone/online)
├── Receptionist opens /reservations/create
├── Fills reservation form:
│   ├── Check-in date
│   ├── Check-out date
│   ├── Room type preference
│   ├── Guest details (name, email, phone)
│   └── Special requests
├── Submits form to ReservationController
└── ReservationController validates & saves

PHASE 2: CONFIRMATION
├── ReservationService.createReservation() called
├── Validates dates (checkout > checkin)
├── Checks room availability (DB level)
├── Generates unique reservation ID
├── Inserts into reservations table
├── Inserts guest details into reservation_guests table
├── Triggers EmailService.sendConfirmation()
└── Email sent to guest

PHASE 3: ACTIVE RESERVATION
├── Reservation status = CONFIRMED
├── Guest appears at hotel on check-in date
├── Receptionist marks check-in (optional in system)
├── Guest occupies room
├── Room status remains AVAILABLE (for tracking purposes)
└── Guest can request services

PHASE 4: CHECKOUT
├── Guest checks out on checkout date
├── ReservationController receives checkout request
├── BillingController generates bill:
│   ├── Retrieves room rate
│   ├── Calculates nights stayed
│   ├── Queries facilities for extra charges
│   ├── Applies discounts if any
│   └── Calculates total cost
├── Bill inserted into bills table
├── EmailService sends billing email
├── Reservation status changed to COMPLETED
└── Room becomes available for next guest

PHASE 5: POST-CHECKOUT
├── Housekeeping cleans room
├── Room status remains AVAILABLE
├── Next reservation can use room
└── Guest can view/download bill anytime
```

### Authentication & Authorization Flow

```
REQUEST ARRIVES
    ↓
AuthFilter intercepts (@WebFilter("/*"))
    ↓
Check if request path is PUBLIC?
├─ YES (login, register, assets)
│   ├── Allow request
│   └── Continue to Controller
│
└─ NO (protected paths)
    ├── Check if user logged in?
    │   ├─ NO
    │   │   ├── Redirect to /login
    │   │   └── Return 403 Forbidden
    │   │
    │   └─ YES
    │       ├── Check user role
    │       ├── Is path ADMIN-only?
    │       │   ├─ YES
    │       │   │   ├── User role == ADMIN?
    │       │   │   │   ├─ YES → Allow request
    │       │   │   │   └─ NO → Redirect to /dashboard
    │       │   │   └──
    │       │   └─ NO → Allow request
    │       │
    │       └── Continue to Controller
    │
Controller processes request
    ↓
Response sent to client
```

## API Reference

### 1. Authentication Endpoints

#### POST /login

**Purpose**: Authenticate user with credentials

**Request**:

```html
<form method="POST" action="/login">
    <input type="text" name="username" required>
    <input type="password" name="password" required>
    <button type="submit">Login</button>
</form>
```

**Parameters**:


| Name     | Type   | Required | Description                       |
| -------- | ------ | -------- | --------------------------------- |
| username | String | Yes      | Username (unique)                 |
| password | String | Yes      | Plain text password (min 8 chars) |

**Response**:

- **Success**: Redirect to /dashboard (201)
- **Failure**: Redirect to /login with error message (400)

**Implementation**:

```java
@WebServlet("/login")
public class AuthController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, 
                         HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
      
        UserService userService = 
            ApplicationComponents.getInstance().getUserService();
      
        Optional<User> user = userService.authenticate(username, password);
      
        if (user.isPresent()) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user.get());
            response.sendRedirect("/dashboard");
        } else {
            request.setAttribute("errorMessage", 
                "Invalid username or password");
            request.getRequestDispatcher("/auth/login.jsp")
                .forward(request, response);
        }
    }
}
```

#### POST /register

**Purpose**: Create new user account

**Parameters**:


| Name            | Type   | Required | Validation                       |
| --------------- | ------ | -------- | -------------------------------- |
| firstName       | String | Yes      | Non-empty                        |
| lastName        | String | Yes      | Non-empty                        |
| username        | String | Yes      | Unique, alphanumeric, 3-50 chars |
| email           | String | Yes      | Valid email format, unique       |
| password        | String | Yes      | Minimum 8 characters             |
| confirmPassword | String | Yes      | Must match password              |

**Response**:

- **Success**: Redirect to /login (201 Created)
- **Failure**: Show registration form with errors (400 Bad Request)

### 2. Reservation Endpoints

#### GET /reservations

**Purpose**: List all reservations with filters

**Query Parameters**:


| Name     | Type    | Optional | Values                          |
| -------- | ------- | -------- | ------------------------------- |
| status   | String  | Yes      | CONFIRMED, COMPLETED, CANCELLED |
| roomType | String  | Yes      | SINGLE, DOUBLE, DELUXE, SUITE   |
| page     | Integer | Yes      | Page number (1-indexed)         |

**Response**: List of reservations (ReservationSummaryDTO)

#### POST /reservations

**Purpose**: Create new reservation

**Parameters** (JSON or form):

```json
{
  "checkInDate": "2026-05-10",
  "checkOutDate": "2026-05-13",
  "roomType": "DOUBLE",
  "guestCount": 2,
  "contactNumber": "+1234567890",
  "address": "123 Main St",
  "guest": {
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "phone": "+1234567890"
  }
}
```

**Validation**:

```
├── CheckOutDate > CheckInDate
├── CheckInDate >= Today (no past reservations)
├── Room type is valid enum
├── Guest count >= 1
├── Email format valid
└── Contact number format valid
```

**Response**:

- **Success**: Reservation object with ID (201 Created)
- **Failure**: Error message (400 Bad Request)

#### GET /reservations/ {id}

**Purpose**: Get specific reservation details

**Path Parameters**:


| Name | Type | Description    |
| ---- | ---- | -------------- |
| id   | Long | Reservation ID |

**Response**: Complete reservation with guest details

### 3. Room Endpoints

#### GET /rooms

**Purpose**: List available rooms with filtering

**Query Parameters**:


| Name          | Type    | Optional | Values                             |
| ------------- | ------- | -------- | ---------------------------------- |
| roomType      | String  | Yes      | SINGLE, DOUBLE, DELUXE, SUITE      |
| availableOnly | Boolean | Yes      | true/false                         |
| minDate       | Date    | Yes      | Check-in date (format: yyyy-MM-dd) |
| maxDate       | Date    | Yes      | Check-out date                     |

**Response**: Array of rooms with availability info

#### POST /rooms (Admin Only)

**Purpose**: Add new room to inventory

**Parameters**:

```json
{
  "roomNumber": "101",
  "roomType": "DOUBLE",
  "ratePerNight": 120.00,
  "description": "Ocean view with balcony",
  "facilities": [1, 3, 5]  // Facility IDs
}
```

**Validation**:

```
├── Room number is unique
├── Room type is valid enum
├── Rate per night > 0
├── Room number format valid (e.g., 1-3 digits)
└── Facilities exist in database
```

#### PUT /rooms/ {id} (Admin Only)

**Purpose**: Update room details

**Path Parameters**:


| Name | Type    | Description |
| ---- | ------- | ----------- |
| id   | Integer | Room ID     |

**Request Body**: Same as POST /rooms

### 4. Billing Endpoints

#### GET /billing

**Purpose**: View billing dashboard

**Response**: List of all bills with status

#### GET /billing/ {reservationId}

**Purpose**: Generate bill for specific reservation

**Path Parameters**:


| Name          | Type | Description    |
| ------------- | ---- | -------------- |
| reservationId | Long | Reservation ID |

**Response**:

```json
{
  "reservationId": 1000000001,
  "numberOfNights": 3,
  "ratePerNight": 120.00,
  "extraCharges": 45.00,
  "discount": 0.00,
  "totalCost": 405.00,
  "guestName": "John Doe",
  "guestEmail": "john@example.com",
  "checkInDate": "2026-05-10",
  "checkOutDate": "2026-05-13"
}
```

#### POST /billing/ {reservationId}/email

**Purpose**: Send billing email to guest

**Response**: Confirmation message

### 5. Dashboard Endpoints

#### GET /dashboard

**Purpose**: Display dashboard home

**Response**: Dashboard HTML with statistics

#### GET /api/dashboard/stats (JSON)

**Purpose**: Get dashboard statistics

**Response**:

```json
{
  "totalReservations": 150,
  "activeReservations": 25,
  "completedReservations": 120,
  "cancelledReservations": 5,
  "totalRooms": 50,
  "availableRooms": 20,
  "roomsInMaintenance": 5,
  "totalRevenue": 45000.00,
  "averageOccupancyRate": 0.68,
  "averageBillAmount": 300.00
}
```

## Database Documentation

### Complete Schema

#### Users Table

```sql
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(60) NOT NULL,
  last_name VARCHAR(60) NOT NULL,
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(120) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('ADMIN','RECEPTIONIST') NOT NULL DEFAULT 'RECEPTIONIST',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_username (username),
  INDEX idx_email (email)
);
```

**Fields**:

- `password_hash`: Bcrypt hash (always 60 chars for bcrypt)
- `role`: Access level (ADMIN full access, RECEPTIONIST limited)
- `timestamps`: For audit trail

#### Rooms Table

```sql
CREATE TABLE rooms (
  id INT AUTO_INCREMENT PRIMARY KEY,
  room_number VARCHAR(20) NOT NULL UNIQUE,
  room_type ENUM('SINGLE','DOUBLE','DELUXE','SUITE') NOT NULL,
  rate_per_night DECIMAL(10,2) NOT NULL,
  status ENUM('AVAILABLE','MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',
  description LONGTEXT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_room_number (room_number),
  INDEX idx_status (status)
);
```

#### Reservations Table (Complex)

```sql
CREATE TABLE reservations (
  reservation_id BIGINT PRIMARY KEY,
  guest_count INT NOT NULL DEFAULT 1,
  address VARCHAR(255) NULL,
  contact_number VARCHAR(20) NOT NULL,
  room_type ENUM('SINGLE','DOUBLE','DELUXE','SUITE') NOT NULL,
  check_in_date DATE NOT NULL,
  check_out_date DATE NOT NULL,
  room_id INT NOT NULL,
  status ENUM('CONFIRMED', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_res_room 
    FOREIGN KEY (room_id) REFERENCES rooms(id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  INDEX idx_res_room_dates (room_id, check_in_date, check_out_date),
  INDEX idx_status (status),
  INDEX idx_dates (check_in_date, check_out_date)
);
```

**Special Notes**:

- `reservation_id` is BIGINT to handle high volume
- Format: Timestamp-based (e.g., 1000000001)
- Composite index on (room_id, check_in_date, check_out_date) for availability queries
- ON DELETE RESTRICT prevents orphaned reservations

#### Bills Table

```sql
CREATE TABLE bills (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  reservation_id BIGINT NOT NULL UNIQUE,
  number_of_nights INT NOT NULL,
  rate_per_night DECIMAL(10,2) NOT NULL,
  extra_charges DECIMAL(10,2) DEFAULT 0.00,
  discount DECIMAL(10,2) DEFAULT 0.00,
  total_cost DECIMAL(10,2) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_bill_reservation
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  INDEX idx_reservation_id (reservation_id),
  INDEX idx_created_at (created_at)
);
```

#### Room Facilities Association (Many-to-Many)

```sql
CREATE TABLE room_facilities (
  room_id INT NOT NULL,
  facility_id INT NOT NULL,
  extra_price_per_night DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (room_id, facility_id),
  CONSTRAINT fk_rf_room
    FOREIGN KEY (room_id) REFERENCES rooms(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_rf_facility
    FOREIGN KEY (facility_id) REFERENCES facilities(id)
    ON DELETE CASCADE ON UPDATE CASCADE
);
```

#### Password Reset Tokens Table

```sql
CREATE TABLE password_reset_tokens (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  token_hash CHAR(64) NOT NULL UNIQUE,  -- SHA-256 hash (64 hex chars)
  expires_at DATETIME NOT NULL,          -- 30 minutes from creation
  used_at DATETIME NULL,                 -- Set when token used
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_prt_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  INDEX idx_prt_user_id (user_id),
  INDEX idx_prt_expires_at (expires_at),
  INDEX idx_token_hash (token_hash)
);
```

### ER Diagram

```
users (1) ───┬──── (Many) password_reset_tokens
             │                    │
             │                    └── tokens for password reset
             │
             └──── (Many) reservations_created_by_user
                            │
rooms (1) ─────┬────────────┤─── (1) bills
               │            │        │
               │            │        └── cost calculation
               │            │
               ├────── (Many) room_facilities ────── (Many) facilities
               │             │
               │             └── links rooms to amenities with pricing
               │
               └────── (Many) reservations ────────────┘
                            │
                            └────── (Many) reservation_guests
                                    (guest details for each reservation)
```

## Security Architecture

### 1. Authentication System

#### Session Management

```java
// On successful login
HttpSession session = request.getSession();
session.setAttribute("user", authenticatedUser);
session.setMaxInactiveInterval(1800);  // 30 minutes

// On logout
session.invalidate();
```

#### Password Security Layers

```
LAYER 1: Client-side (basic validation in HTML form)
  ├── Required field
  ├── Type="password" (masks input)
  └── Minimum length indicator

LAYER 2: Server-side (Java validation)
  ├── Null/empty check
  ├── Length validation (8+)
  └── No SQL injection (parameterized query)

LAYER 3: Database (bcrypt storage)
  ├── bcrypt with cost factor 12
  ├── Salt included in hash
  ├── One-way hashing (cannot decrypt)
  └── 100ms/hash (prevents brute force)
```

### 2. Authorization System

#### Role-Based Access Control (RBAC)

```
PUBLIC (anyone can access):
├── /login              - Authentication page
├── /register           - New user registration
├── /forgot-password    - Password recovery
├── /reset-password     - Token-based password reset
├── /assets/*           - CSS, JS, images, fonts
└── Static files (.woff, .png, .jpg)

RECEPTIONIST (logged in):
├── /reservations       - View & create reservations
├── /reservations/*     - Manage reservations
├── /billing            - View & generate bills
├── /profile            - View own profile
└── /dashboard          - View analytics

ADMIN (elevated privileges):
├── [All RECEPTIONIST routes]
├── /staff              - Manage users
├── /rooms              - Full room management
├── /rooms/edit         - Edit room details
└── System administration

GUEST (future feature):
├── View own reservation - future
├── View own bills       - future
└── Update own profile   - future
```

### 3. Data Protection

#### SQL Injection Prevention

```java
// VULNERABLE (Never do this!)
String query = "SELECT * FROM users WHERE username = '" + 
               username + "'";
Statement stmt = connection.createStatement();
ResultSet rs = stmt.executeQuery(query);

// SECURE (What we use)
String query = "SELECT * FROM users WHERE username = ?";
PreparedStatement pstmt = connection.prepareStatement(query);
pstmt.setString(1, username);  // Parameter binding
ResultSet rs = pstmt.executeQuery();
```

**All DAOs use PreparedStatement**:

- User DAO queries
- Reservation DAO queries
- Room DAO queries
- Bill DAO queries

#### Password Reset Token Security

```java
// Generate token
String randomToken = generateRandomString(32);
String tokenHash = SHA256(randomToken);  // One-way hashing

// Store hash (not the token)
passwordResetTokensDAO.insert(userId, tokenHash, expiresAt);

// Send token to user (only in reset link, never stored)
String resetLink = "http://hotel.com/reset-password?token=" + randomToken;
emailService.send(userEmail, resetLink);

// When user resets:
// 1. Receive randomToken in request
// 2. Hash it
// 3. Look for matching hash in DB
// 4. Verify expiration
// 5. Update password & mark token as used
```

### 4. HTTPS & Transport Security

```
PRODUCTION RECOMMENDATIONS:
├── Force HTTPS redirect
│   └── All HTTP requests redirect to HTTPS
├── HSTS Header
│   └── Strict-Transport-Security: max-age=31536000
├── Certificate
│   └── Valid SSL/TLS certificate
├── Cipher Suites
│   └── TLS 1.2+ with strong ciphers
└── CORS Headers
    └── Restrict to known domains
```

### 5. Session Security

```java
// Prevent Session Fixation
HttpSession oldSession = request.getSession(false);
if (oldSession != null) {
    oldSession.invalidate();
}
HttpSession newSession = request.getSession(true);

// Set Secure Flags
Cookie sessionCookie = new Cookie("JSESSIONID", newSession.getId());
sessionCookie.setHttpOnly(true);   // Not accessible to JavaScript
sessionCookie.setSecure(true);     // Only sent over HTTPS
sessionCookie.setPath("/");
response.addCookie(sessionCookie);

// Session Timeout
newSession.setMaxInactiveInterval(1800);  // 30 minutes
```

## Configuration Guide

### Environment Variables Setup

Create `.env` file in project root:

```env
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=hotel_reservation_db
DB_USER=hoteluser
DB_PASSWORD=SecurePassword123!

# Email Configuration (Gmail Example)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-specific-password
MAIL_FROM=noreply@yourhotel.com

# Email Settings
MAIL_SMTP_AUTH=true
MAIL_START_TLS=true
MAIL_SSL_ENABLE=false

# Optional: Server Configuration
SERVER_PORT=8080
LOG_LEVEL=INFO
SESSION_TIMEOUT=1800
```

### Gmail Setup for Email Sending

1. **Enable 2-Factor Authentication**:

   - Go to https://myaccount.google.com/security
   - Enable 2-Step Verification
2. **Generate App Password**:

   - Go to https://myaccount.google.com/apppasswords
   - Select "Mail" and "Windows Computer"
   - Google generates 16-character password
   - Use this in `MAIL_PASSWORD`
3. **Example Configuration**:

```env
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=yourhotel@gmail.com
MAIL_PASSWORD=xxxx xxxx xxxx xxxx
MAIL_FROM=yourhotel@gmail.com
MAIL_SMTP_AUTH=true
MAIL_START_TLS=true
MAIL_SSL_ENABLE=false
```

### MySQL Configuration

#### Create Database User

```sql
-- Login as MySQL root
mysql -u root -p

-- Create database
CREATE DATABASE hotel_reservation_db;

-- Create application user
CREATE USER 'hoteluser'@'localhost' 
  IDENTIFIED BY 'SecurePassword123!';

-- Grant permissions
GRANT ALL PRIVILEGES ON hotel_reservation_db.* 
  TO 'hoteluser'@'localhost';

-- Apply changes
FLUSH PRIVILEGES;

-- Verify
SHOW GRANTS FOR 'hoteluser'@'localhost';
```

#### Run Migration Scripts

```bash
# Execute SQL scripts
mysql -u hoteluser -p hotel_reservation_db < database/hotel_reservation_db_create.sql
mysql -u hoteluser -p hotel_reservation_db < database/hotel_reservation_db_seed.sql

# Verify tables created
mysql -u hoteluser -p hotel_reservation_db -e "SHOW TABLES;"
```

### Maven Configuration (pom.xml)

Key Maven configurations:

```xml
<!-- Java Version -->
<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>

<!-- Build Plugins -->
<build>
    <plugins>
        <!-- War Plugin for packaging -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-war-plugin</artifactId>
            <version>3.4.0</version>
        </plugin>
      
        <!-- Tomcat Plugin for running -->
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>tomcat-maven-plugin</artifactId>
            <version>1.1</version>
        </plugin>
    </plugins>
</build>
```

## Deployment Guide

### Development Deployment

```bash
# 1. Build the project
mvn clean package -DskipTests

# 2. Run with Maven Tomcat plugin
mvn tomcat7:run
# or
mvn org.codehaus.mojo:tomcat-maven-plugin:1.1:run

# 3. Access application
# http://localhost:8080/Hotel-Reservation-System
```

### Production Deployment to Tomcat

```bash
# 1. Build WAR file
mvn clean package

# 2. Stop Tomcat
$TOMCAT_HOME/bin/shutdown.sh

# 3. Remove old deployment
rm -rf $TOMCAT_HOME/webapps/Hotel-Reservation-System*

# 4. Copy WAR file
cp target/Hotel-Reservation-System-1.0-SNAPSHOT.war \
   $TOMCAT_HOME/webapps/

# 5. Start Tomcat
$TOMCAT_HOME/bin/startup.sh

# 6. Verify
tail -f $TOMCAT_HOME/logs/catalina.out
```

### Docker Deployment (Optional)

```dockerfile
FROM tomcat:10-jdk11-openjdk

# Copy WAR
COPY target/Hotel-Reservation-System-1.0-SNAPSHOT.war \
     /usr/local/tomcat/webapps/

# Copy env file
COPY .env /app/.env

# Expose port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
```

```bash
# Build image
docker build -t hotel-reservation-system:1.0 .

# Run container
docker run -p 8080:8080 \
  -e DB_HOST=mysql \
  -e MAIL_HOST=smtp.gmail.com \
  hotel-reservation-system:1.0
```

## Development Guide

### Setting Up Development Environment

#### IDE Setup (IntelliJ IDEA)

1. **Create/Open Project**:

   - File → Open
   - Select project root
   - Trust project (Maven)
2. **Configure Run Configurations**:

   - Run → Edit Configurations
   - Add new "Tomcat Server" configuration
   - Set URL: http://localhost:8080/Hotel-Reservation-System
3. **Enable Maven Plugins**:

   - View → Tool Windows → Maven
   - Enable auto-reimport

#### IDE Setup (VS Code)

```bash
# Install extensions
code --install-extension vscjava.extension-pack-for-java
code --install-extension vscjava.vscode-maven
code --install-extension ms-vscode.makefile-tools
```

### Code Style Guide

#### Java Naming Conventions

```java
// Classes: PascalCase
public class ReservationController { }
public class ReservationService { }

// Methods & Variables: camelCase
public void createReservation() { }
private String userEmail;

// Constants: UPPER_SNAKE_CASE
public static final int MAX_ROOM_CAPACITY = 4;
public static final String DATE_FORMAT = "yyyy-MM-dd";

// Booleans: is/has prefix
private boolean isAvailable;
private boolean hasError;
```

#### Code Organization

```java
// Order in class:
public class ReservationService {
    // 1. Constants
    private static final int PAGE_SIZE = 10;
  
    // 2. Instance variables
    private ReservationDAO reservationDAO;
    private EmailService emailService;
  
    // 3. Constructor
    public ReservationService() { }
  
    // 4. Public methods
    public Reservations createReservation(/* params */) { }
  
    // 5. Private methods
    private boolean validateDates(/* params */) { }
}
```

### Adding New Features

#### Adding a New Controller

1. **Create Controller Class**:

```java
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/path/to/feature")
public class FeatureController extends HttpServlet {
  
    @Override
    protected void doGet(HttpServletRequest request, 
                        HttpServletResponse response)
            throws ServletException, IOException {
        // Get service instance
        FeatureService service = 
            ApplicationComponents.getInstance().getFeatureService();
      
        // Process request
        // Forward to JSP or send response
    }
  
    @Override
    protected void doPost(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        // Similar pattern
    }
}
```

2. **Create Service Interface & Implementation**:

```java
// FeatureService.java (interface)
public interface FeatureService {
    void processFeature(Feature feature);
    Feature getFeature(long id);
}

// FeatureServiceImpl.java (implementation)
public class FeatureServiceImpl implements FeatureService {
    private FeatureDAO featureDAO = new FeatureDAOImpl();
  
    @Override
    public void processFeature(Feature feature) {
        // Business logic
        featureDAO.insert(feature);
    }
}
```

3. **Create DAO Interface & Implementation**:

```java
// FeatureDAO.java
public interface FeatureDAO {
    void insert(Feature feature);
    Feature get(long id);
}

// FeatureDAOImpl.java
public class FeatureDAOImpl implements FeatureDAO {
    @Override
    public void insert(Feature feature) {
        String sql = "INSERT INTO features (name) VALUES (?)";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
          
            pstmt.setString(1, feature.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Insert failed", e);
        }
    }
}
```

4. **Register Service in ApplicationComponents**:

```java
public class ApplicationComponents {
    private FeatureService featureService;
  
    private ApplicationComponents() {
        this.featureService = new FeatureServiceImpl();
    }
  
    public FeatureService getFeatureService() {
        return featureService;
    }
}
```

### Writing Tests

#### Unit Test Example

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService Unit Tests")
class ReservationServiceImplTest {
  
    @Mock
    private ReservationDAO reservationDAO;
  
    @Mock
    private EmailService emailService;
  
    @InjectMocks
    private ReservationServiceImpl reservationService;
  
    @Test
    @DisplayName("Should create reservation when room is available")
    void testCreateReservation_Success() {
        // Arrange
        Reservations reservation = new Reservations();
        reservation.setCheckInDate(LocalDate.now().plusDays(1));
        reservation.setCheckOutDate(LocalDate.now().plusDays(3));
      
        when(reservationDAO.checkAvailability(anyInt(), any(), any()))
            .thenReturn(true);
      
        // Act
        Reservations result = reservationService.createReservation(reservation);
      
        // Assert
        assertNotNull(result);
        verify(emailService).sendReservationConfirmation(any());
    }
}
```

#### Integration Test Example

```java
@WebAppConfiguration
@SpringBootTest
class ControllerIntegrationTest {
  
    @Autowired
    private WebApplicationContext webApplicationContext;
  
    private MockMvc mockMvc;
  
    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .build();
    }
  
    @Test
    @DisplayName("Should return 200 for GET /reservations")
    void testListReservations() throws Exception {
        mockMvc.perform(get("/reservations")
            .sessionAttr("user", testUser))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.TEXT_HTML));
    }
}
```

## Troubleshooting

### Common Issues

#### Issue 1: Cannot Connect to MySQL

```
Error: "Cannot get a connection, pool error Timeout waiting for 
idle object"
```

**Causes**:

- MySQL server not running
- Wrong database credentials
- Network connectivity issue
- Connection pool exhausted

**Solutions**:

```bash
# 1. Check MySQL is running
mysql -u root -p -e "SELECT 1"

# 2. Verify connection string
echo $DB_HOST
echo $DB_PORT

# 3. Test with mysql client
mysql -h localhost -u hoteluser -p hotel_reservation_db

# 4. Check database exists
mysql -u root -p -e "SHOW DATABASES LIKE 'hotel%';"
```

#### Issue 2: Email Sending Fails

```
Error: "javax.mail.AuthenticationFailedException"
```

**Causes**:

- Wrong SMTP credentials
- 2FA enabled on Gmail but no app password
- Port number incorrect
- Firewall blocking SMTP port

**Solutions**:

```bash
# 1. Verify credentials in .env
cat .env | grep MAIL_

# 2. Test Gmail connection
telnet smtp.gmail.com 587

# 3. Generate new app password
# https://myaccount.google.com/apppasswords

# 4. Check firewall
# Port 587: SMTP with STARTTLS
# Port 465: SMTP with SSL
```

#### Issue 3: Reservation Already Exists Error

```
Error: "Duplicate entry '1000000001' for key 'PRIMARY'"
```

**Cause**: Reservation ID generation collision

**Solution**:

```sql
-- Check current max ID
SELECT MAX(reservation_id) FROM reservations;

-- If needed, reset auto-increment
ALTER TABLE reservations AUTO_INCREMENT = 2000000000;
```

#### Issue 4: 403 Forbidden on Protected Routes

```
Error: "Access Denied" for authenticated user
```

**Cause**: Role-based access control blocking request

**Solution**:

```java
// Check user role in session
HttpSession session = request.getSession();
User user = (User) session.getAttribute("user");
System.out.println("User role: " + user.getRole());

// Verify role has required permissions in AuthFilter
```

### Performance Issues

#### Slow Reservation Listing

```
Symptoms: /reservations takes >5 seconds to load
```

**Diagnosis**:

```sql
-- Check index on reservations
SHOW INDEX FROM reservations;

-- If index missing:
CREATE INDEX idx_status ON reservations(status);
CREATE INDEX idx_dates ON reservations(check_in_date, check_out_date);
```

#### Database Connection Pool Exhaustion

```
Symptoms: Random "Cannot get connection" errors after time
```

**Solution**:

```java
// Ensure connections are closed
try (Connection conn = DatabaseConnectionPool.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql)) {
    // Use resources - auto-closed
}

// Or explicitly close
Connection conn = null;
try {
    conn = DatabaseConnectionPool.getConnection();
    // Use connection
} finally {
    if (conn != null) {
        conn.close();  // Return to pool
    }
}
```

## Performance Tuning

### Database Optimization

#### Indexes to Add

```sql
-- Reservation queries
CREATE INDEX idx_res_status ON reservations(status);
CREATE INDEX idx_res_dates ON reservations(check_in_date, check_out_date);
CREATE INDEX idx_res_room_dates ON reservations(room_id, check_in_date, check_out_date);

-- User queries
CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_user_email ON users(email);

-- Bill queries
CREATE INDEX idx_bill_reservation ON bills(reservation_id);
CREATE INDEX idx_bill_created ON bills(created_at);

-- Room queries
CREATE INDEX idx_room_status ON rooms(status);
CREATE INDEX idx_room_type ON rooms(room_type);
```

#### Query Optimization

Bad Query (Full table scan):

```sql
SELECT * FROM reservations
WHERE check_in_date BETWEEN '2026-05-01' AND '2026-05-31'
```

Better Query (Uses index):

```sql
SELECT reservation_id, room_id, check_in_date, check_out_date, status
FROM reservations
WHERE check_in_date >= '2026-05-01' 
  AND check_in_date <= '2026-05-31'
  AND status = 'CONFIRMED'
ORDER BY check_in_date
LIMIT 100
```

### Connection Pool Tuning

```java
// DatabaseConnectionPool.java
HikariConfig config = new HikariConfig();

// Adjust based on workload
config.setMaximumPoolSize(20);      // Increase for high traffic
config.setMinimumIdle(5);            // Keep more idle connections
config.setConnectionTimeout(30000);  // 30 seconds to get connection
config.setIdleTimeout(600000);       // 10 minutes idle timeout
config.setMaxLifetime(1800000);      // 30 minutes max lifetime

// Enable connection testing
config.setConnectionTestQuery("SELECT 1");
config.setLeakDetectionThreshold(60000);  // 60 seconds
```

### Caching Strategies

#### Cache Room Data (Rarely Changes)

```java
private static final Map<Integer, Rooms> ROOM_CACHE = new ConcurrentHashMap<>();
private static long LAST_CACHE_UPDATE = 0;
private static final long CACHE_TTL = 300000;  // 5 minutes

public List<Rooms> getRooms() {
    if (System.currentTimeMillis() - LAST_CACHE_UPDATE > CACHE_TTL) {
        refreshCache();
    }
    return new ArrayList<>(ROOM_CACHE.values());
}

private void refreshCache() {
    ROOM_CACHE.clear();
    List<Rooms> rooms = reservationDAO.getAllRooms();
    rooms.forEach(r -> ROOM_CACHE.put(r.getId(), r));
    LAST_CACHE_UPDATE = System.currentTimeMillis();
}
```

## Maintenance & Operations

### Database Maintenance

#### Regular Backups

```bash
# Daily backup
mysqldump -u hoteluser -p hotel_reservation_db > backup_$(date +%Y%m%d_%H%M%S).sql

# Automated backup (cron job)
0 2 * * * /usr/bin/mysqldump -u hoteluser -pPASSWORD hotel_reservation_db > /backups/daily_$(date +\%Y\%m\%d).sql
```

#### Data Cleanup

```sql
-- Remove old password reset tokens (>1 month old)
DELETE FROM password_reset_tokens 
WHERE created_at < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- Remove cancelled reservations older than 1 year
DELETE FROM reservations 
WHERE status = 'CANCELLED' 
  AND created_at < DATE_SUB(NOW(), INTERVAL 1 YEAR);
```

#### Database Health Checks

```sql
-- Check table sizes
SELECT table_name, ROUND(((data_length + index_length) / 1024 / 1024), 2) AS size_mb
FROM information_schema.tables
WHERE table_schema = 'hotel_reservation_db'
ORDER BY size_mb DESC;

-- Check for unused indexes
SELECT object_name, count
FROM performance_schema.table_io_waits_summary_by_index_usage
WHERE object_schema = 'hotel_reservation_db' AND count = 0;

-- Run optimize table
OPTIMIZE TABLE reservations, rooms, users, bills;
```

### Monitoring

#### Log Monitoring

```bash
# Tail Tomcat logs
tail -f /opt/tomcat/logs/catalina.out

# Search for errors
grep "ERROR" /opt/tomcat/logs/catalina.out | tail -20

# Real-time monitoring
watch -n 5 'tail /opt/tomcat/logs/catalina.out | tail -10'
```

#### Application Metrics to Track

```
├── Request Count: Total requests per minute
├── Error Rate: 5xx errors percentage
├── Response Time: Average response time (target <200ms)
├── Database Queries: Average query time
├── Connection Pool: Active connections
├── Email Queue: Pending emails
└── Session Count: Active user sessions
```

## Appendix

### Default Test Data

After running `hotel_reservation_db_seed.sql`:

**Admin User**:

```
Username: admin
Password: admin123
Email: admin@hotel.com
```

**Receptionist User**:

```
Username: receptionist
Email: receptionist@hotel.com
Password: pass123
```

**Sample Rooms**:

```
101 - SINGLE   - $80/night
102 - DOUBLE   - $120/night
103 - DELUXE   - $150/night
104 - SUITE    - $250/night
```

### File Locations Reference

```
Hotel-Reservation-System/
├── Controllers           → src/main/java/com/example/hotelreservationsystem/controller/
├── Services             → src/main/java/com/example/hotelreservationsystem/service/
├── DAOs                 → src/main/java/com/example/hotelreservationsystem/dao/
├── Models               → src/main/java/com/example/hotelreservationsystem/model/
├── JSP Templates        → src/main/webapp/WEB-INF/views/
├── CSS/JS Assets        → src/main/webapp/assets/
├── Database Scripts     → database/
├── Tests                → src/test/java/com/example/hotelreservationsystem/
└── Configuration        → pom.xml, .env
```

**Document Version**: 1.0
**Last Updated**: May 2026
**Author**: Dilaksha Dissanayake
**Status**: Approved for Production
