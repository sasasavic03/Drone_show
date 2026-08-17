# Drone Show - Microservices Application

A complete microservices architecture for a Drone Show booking platform built with Spring Boot, Java 21, Maven, and PostgreSQL.

## 📋 Project Structure

```
Drone_show/
├── auth_service/          # Authentication & Authorization Service
├── user_service/          # User Management Service
├── booking_service/       # Booking Management Service (coming)
├── package_service/       # Package Configuration Service (coming)
├── docker-compose.yml     # Docker Compose configuration
├── init.sql              # Database initialization script
└── README.md             # This file
```

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 21 (optional, for local development)
- Maven 3.8+ (optional, for local development)

### Running with Docker Compose

1. **Clone the repository**
   ```bash
   cd /home/savic/IdeaProjects/Drone_show
   ```

2. **Build and run all services**
   ```bash
   docker-compose up --build
   ```

   This will:
   - Start PostgreSQL database on port 5432
   - Build and start Auth Service on port 3001
   - Build and start User Service on port 3002

3. **Verify services are running**
   ```bash
   # Check Auth Service
   curl http://localhost:3001/auth/me

   # Check User Service
   curl http://localhost:3002/users/me
   ```

4. **Stop services**
   ```bash
   docker-compose down
   ```

## 🔐 AUTH SERVICE (Port 3001)

### Endpoints

#### Register
```bash
POST /auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+381623456789"
}

Response: 201 Created
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",
    "user": {
      "id": 1,
      "email": "user@example.com",
      "firstName": "John",
      "role": "USER"
    }
  }
}
```

#### Login
```bash
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response: 200 OK
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",
    "user": {
      "id": 1,
      "email": "user@example.com",
      "firstName": "John",
      "role": "USER"
    }
  }
}
```

#### Refresh Token
```bash
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGc..."
}

Response: 200 OK
{
  "success": true,
  "message": "Token refreshed",
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",
    "user": {...}
  }
}
```

#### Logout
```bash
POST /auth/logout
Content-Type: application/json
Authorization: Bearer {accessToken}

{
  "refreshToken": "eyJhbGc..."
}

Response: 200 OK
{
  "success": true,
  "message": "Logout successful"
}
```

#### Get Current User
```bash
GET /auth/me
Authorization: Bearer {accessToken}

Response: 200 OK
{
  "success": true,
  "message": "User details retrieved",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+381623456789",
    "city": null,
    "role": "USER"
  }
}
```

## 👤 USER SERVICE (Port 3002)

### Endpoints

#### Get Current User Profile
```bash
GET /users/me
Authorization: Bearer {accessToken}

Response: 200 OK
{
  "success": true,
  "message": "User profile retrieved",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+381623456789",
    "city": "Beograd",
    "role": "USER"
  }
}
```

#### Update Current User Profile
```bash
PUT /users/me
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+381623456789",
  "city": "Beograd"
}

Response: 200 OK
{
  "success": true,
  "message": "User profile updated",
  "data": {...}
}
```

#### Get All Users (ADMIN ONLY)
```bash
GET /users?page=0&size=20&role=USER
Authorization: Bearer {adminAccessToken}

Response: 200 OK
{
  "success": true,
  "message": "Users retrieved",
  "data": {
    "content": [...],
    "page": 0,
    "size": 20,
    "total": 50,
    "pages": 3,
    "first": true,
    "last": false
  }
}
```

#### Get User by ID (ADMIN ONLY)
```bash
GET /users/{id}
Authorization: Bearer {adminAccessToken}

Response: 200 OK
{
  "success": true,
  "message": "User retrieved",
  "data": {...}
}
```

#### Delete User (ADMIN ONLY)
```bash
DELETE /users/{id}
Authorization: Bearer {adminAccessToken}

Response: 200 OK
{
  "success": true,
  "message": "User deleted"
}
```

#### Make User Admin (ADMIN ONLY)
```bash
POST /users/{id}/make-admin
Authorization: Bearer {adminAccessToken}

Response: 200 OK
{
  "success": true,
  "message": "User is now admin"
}
```

## 🗄️ Database

### Tables

#### users
- id: SERIAL PRIMARY KEY
- email: VARCHAR(255) UNIQUE NOT NULL
- password_hash: VARCHAR(255) NOT NULL
- first_name: VARCHAR(100) NOT NULL
- last_name: VARCHAR(100) NOT NULL
- phone: VARCHAR(20)
- city: VARCHAR(100)
- role: VARCHAR(20) DEFAULT 'USER' (USER, ADMIN)
- is_deleted: BOOLEAN DEFAULT FALSE
- created_at: TIMESTAMP
- updated_at: TIMESTAMP

#### refresh_tokens
- id: SERIAL PRIMARY KEY
- user_id: INT FK users(id)
- token: VARCHAR(500) UNIQUE NOT NULL
- expires_at: TIMESTAMP NOT NULL
- created_at: TIMESTAMP

## 🔧 Configuration

### JWT Configuration (in application.properties)
```properties
jwt.secret=your-secret-key-change-this-in-production
jwt.refresh.secret=your-refresh-secret-key-change-this
jwt.expiration=900000              # 15 minutes
jwt.refresh.expiration=604800000   # 7 days
```

### Database Configuration
```properties
spring.datasource.url=jdbc:postgresql://postgres:5432/drone_show_db
spring.datasource.username=postgres
spring.datasource.password=drone_show_password
```

## 📦 Technologies Used

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 21
- **Build**: Maven 3.8.1
- **Database**: PostgreSQL 16
- **Container**: Docker & Docker Compose
- **Authentication**: JWT (JSON Web Tokens)
- **Password Encoding**: BCrypt
- **API Documentation**: REST API

## 🔄 Service Communication Flow

1. **Registration/Login** → Auth Service → Issue JWT tokens
2. **Token Validation** → User Service receives JWT → Validates with JwtUtil
3. **User Operations** → User Service → Database operations
4. **Token Refresh** → Auth Service → Issue new access token

## 📝 Testing with Postman

1. **Register a user** - POST /auth/register
2. **Login** - POST /auth/login (get tokens)
3. **Get user profile** - GET /users/me (use access token)
4. **Update profile** - PUT /users/me (use access token)
5. **Refresh token** - POST /auth/refresh

## 🚀 Development

### Local Development (without Docker)

1. **Install PostgreSQL locally**
   ```bash
   # Run init.sql to create tables
   psql -U postgres drone_show_db < init.sql
   ```

2. **Run Auth Service**
   ```bash
   cd auth_service
   mvn spring-boot:run
   ```

3. **Run User Service** (in another terminal)
   ```bash
   cd user_service
   mvn spring-boot:run
   ```

## 📋 Completed Services

### ✅ Auth Service (Port 3001)
- User registration and login
- JWT token management
- Role-based access control

### ✅ User Service (Port 3002)
- User profile management
- User listing (admin)
- Admin operations

### ✅ Package Service (Port 3003)
- Package management
- Options configuration
- Price calculation

### ✅ Booking Service (Port 3004)
- Booking creation and management
- Status tracking
- Availability checking
- Booking cancellation

### ✅ Media Service (Port 3005)
- File upload to MinIO
- Media listing and filtering
- Media deletion
- Support for photos and videos

## 🎯 Next Steps (Coming Soon)

- [ ] API Gateway
- [ ] Frontend (React.js)
- [ ] Live Streaming Service
- [ ] Analytics Service

## 📧 Support

For issues or questions, please refer to the service documentation or contact the development team.

## 📄 License

This project is private and confidential.
