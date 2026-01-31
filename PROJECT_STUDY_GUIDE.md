# National Blood Transfusion Service Management System (NBT-SMS)

## Complete Project Study Guide

---

## 📋 Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Microservices](#microservices)
4. [Data Models](#data-models)
5. [Security & Authentication](#security--authentication)
6. [Event-Driven Communication](#event-driven-communication)
7. [Infrastructure & DevOps](#infrastructure--devops)
8. [How to Run](#how-to-run)

---

## 1. Project Overview

**National Blood Transfusion Service Management System (NBT-SMS)** is a centralized, scalable, and secure microservice-based platform for managing blood transfusion services across all zones in Tanzania.

### Key Features:

- **Donor Management** - Register and manage blood donors
- **Appointment Scheduling** - Schedule blood donation appointments
- **Zone/Center Management** - Manage geographic zones and blood collection centers
- **Meeting/Donation Sessions** - Track blood donation events and assessments
- **Laboratory Services** - Blood testing and blood unit management
- **Notifications** - SMS notifications via Beem Africa
- **User Authentication & Authorization** - Role-based access control

### Tech Stack:

| Component         | Technology                  |
| ----------------- | --------------------------- |
| Backend           | Spring Boot 3.4.4 (Java 17) |
| Databases         | PostgreSQL                  |
| API Gateway       | Spring Cloud Gateway        |
| Service Discovery | Netflix Eureka              |
| Event Streaming   | Apache Kafka                |
| Monitoring        | Prometheus & Grafana        |
| Tracing           | Zipkin                      |
| Containerization  | Docker & Docker Compose     |
| Orchestration     | Kubernetes                  |

---

## 2. Architecture

### System Architecture Diagram

```
                                    ┌─────────────────────────────────────────┐
                                    │         API Gateway (8080)              │
                                    │    Spring Cloud Gateway + JWT           │
                                    └───────────────┬─────────────────────────┘
                                                    │
                    ┌───────────────────────────────┼───────────────────────────────┐
                    │                               │                               │
                    ▼                               ▼                               ▼
        ┌─────────────────────┐     ┌─────────────────────┐     ┌─────────────────────┐
        │  Identity Service   │     │   Donor Service     │     │   Zone Service      │
        │       (8081)        │     │      (8083)         │     │      (8082)         │
        └─────────────────────┘     └─────────────────────┘     └─────────────────────┘
                    │                               │                               │
                    └───────────────────────────────┼───────────────────────────────┘
                                                    │
                    ┌───────────────────────────────┼───────────────────────────────┐
                    │                               │                               │
                    ▼                               ▼                               ▼
        ┌─────────────────────┐     ┌─────────────────────┐     ┌─────────────────────┐
        │  Meeting Service    │     │ Notification Service│     │ Laboratory Service  │
        │       (8084)        │     │      (8085)         │     │      (8086)         │
        └─────────────────────┘     └─────────────────────┘     └─────────────────────┘

        ┌──────────────────────────────────────────────────────────────────────────────┐
        │                           Kafka Event Bus                                    │
        │                  (Event-driven communication)                                │
        └──────────────────────────────────────────────────────────────────────────────┘

        ┌──────────────────────────────────────────────────────────────────────────────┐
        │                              PostgreSQL                                       │
        │   (Separate database per service)                                            │
        └──────────────────────────────────────────────────────────────────────────────┘

        ┌──────────────────────────────────────────────────────────────────────────────┐
        │                        Monitoring Stack                                      │
        │        Prometheus (9090) + Grafana (3000) + Zipkin (9411)                   │
        └──────────────────────────────────────────────────────────────────────────────┘
```

### Key Components:

1. **API Gateway** - Single entry point for all client requests
2. **Eureka Server** - Service discovery and registration
3. **Microservices** - Independent, loosely coupled services
4. **Kafka** - Asynchronous event communication
5. **PostgreSQL** - Per-service databases for data isolation

---

## 3. Microservices

### 3.1 Identity Service (`identity-service`)

**Port:** 8081  
**Purpose:** User authentication and authorization

**Key Entities:**

- `User` - Staff users with roles (ADMIN, COUNSELOR, LAB_TECHNICIAN, ORGANIZER, SUPER_USER)

**Key Features:**

- User registration and authentication
- JWT token generation and validation
- Role-based access control
- Staff assignment to zones and centers

**Key Files:**

- [`User.java`](identity-service/src/main/java/com/nbtsms/identity_service/entity/User.java) - User entity with Spring Security integration
- [`AuthenticationController.java`](identity-service/src/main/java/com/nbtsms/identity_service/controller/AuthenticationController.java) - Auth endpoints
- [`Role.java`](identity-service/src/main/java/com/nbtsms/identity_service/enums/Role.java) - Role enum

### 3.2 Donor Service (`donor-service`)

**Port:** 8083  
**Purpose:** Donor management and appointment scheduling

**Key Entities:**

- `Donor` - Blood donor information (personal details, donation history)
- `Appointment` - Donation appointments with status tracking

**Key Features:**

- Donor registration
- Appointment scheduling (minimum 3 months between donations)
- Donor notification scheduling
- Donor history tracking

**Key Files:**

- [`Donor.java`](donor-service/src/main/java/com/nbts/management/donor_service/entity/Donor.java) - Donor entity
- [`Appointment.java`](donor-service/src/main/java/com/nbts/management/donor_service/entity/Appointment.java) - Appointment entity
- [`DonorController.java`](donor-service/src/main/java/com/nbts/management/donor_service/controller/DonorController.java) - Donor endpoints
- [`AppointmentController.java`](donor-service/src/main/java/com/nbts/management/donor_service/controller/AppointmentController.java) - Appointment endpoints

### 3.3 Zone Service (`zone-service`)

**Port:** 8082  
**Purpose:** Geographic zone and blood center management

**Key Entities:**

- `Zone` - Geographic zones (e.g., North Zone, South Zone)
- `Region` - Regions within zones
- `Center` - Blood collection centers with GPS coordinates
- `Staff` - Staff members assigned to centers
- `Admin` - Zone administrators

**Key Features:**

- Zone CRUD operations
- Region management
- Center management with location tracking
- Staff assignment/unassignment to centers

**Key Files:**

- [`Zone.java`](zone-service/src/main/java/com/nbtsms/zone_service/entity/Zone.java) - Zone entity
- [`Region.java`](zone-service/src/main/java/com/nbtsms/zone_service/entity/Region.java) - Region entity
- [`Center.java`](zone-service/src/main/java/com/nbtsms/zone_service/entity/Center.java) - Center entity
- [`ZoneController.java`](zone-service/src/main/java/com/nbtsms/zone_service/controller/ZoneController.java) - Zone endpoints
- [`CenterController.java`](zone-service/src/main/java/com/nbtsms/zone_service/controller/CenterController.java) - Center endpoints

### 3.4 Meeting Service (`meeting-service`)

**Port:** 8084  
**Purpose:** Blood donation sessions and donor assessments

**Key Entities:**

- `Meeting` - Blood donation events/sessions
- `Questionnaire` - Donor health questionnaires
- `PreliminaryQuestionnaire` - Initial donor screening
- `PhysicalExamination` - Physical check results
- `BloodPressureAndPulse` - Vital signs
- `HaematologicalTest` - Blood test results
- `BloodCollectionData` - Blood collection metrics
- `FinalDonorEvaluation` - Final donation assessment
- `AdverseEvent` - Adverse reactions tracking
- `AssignedStaff` - Staff assigned to meetings
- `Donor` - Donor information (separate from donor-service)

**Key Features:**

- Meeting/event scheduling
- Staff assignment to meetings
- Donor health questionnaires
- Physical examinations
- Blood collection tracking
- Adverse event reporting

**Key Files:**

- [`Meeting.java`](meeting-service/src/main/java/com/management/nationalblood/meeting/entity/Meeting.java) - Meeting entity
- [`MeetingController.java`](meeting-service/src/main/java/com/management/nationalblood/meeting/controller/MeetingController.java) - Meeting endpoints
- [`AssessmentController.java`](meeting-service/src/main/java/com/management/nationalblood/meeting/controller/AssessmentController.java) - Assessment endpoints

### 3.5 Notification Service (`notification-service`)

**Port:** 8085  
**Purpose:** SMS notifications via Beem Africa

**Key Features:**

- Appointment reminders
- Donation confirmations
- Event notifications
- SMS via Beem Africa API

**Key Files:**

- [`NotificationServiceImpl.java`](notification-service/src/main/java/com/management/nationalblood/notificationservice/service/impl/NotificationServiceImpl.java) - SMS sending logic
- [`SmsProperties.java`](notification-service/src/main/java/com/management/nationalblood/notificationservice/config/SmsProperties.java) - SMS config

### 3.6 Laboratory Service (`laboratory-service`)

**Port:** 8086  
**Purpose:** Blood testing and blood unit management

**Key Features:**

- Blood unit registration
- Test result recording
- Blood inventory management
- Quality control

**Status:** Service skeleton exists, implementation in progress

### 3.7 API Gateway (`api-gateway`)

**Port:** 8080  
**Purpose:** Single entry point for all microservices

**Key Features:**

- JWT token validation
- Route configuration to microservices
- CORS configuration
- Request rate limiting

**Key Files:**

- [`SecurityConfig.java`](api-gateway/src/main/java/com/management/nationalblood/apigateway/config/SecurityConfig.java) - Security configuration
- [`JwtDecoderConfig.java`](api-gateway/src/main/java/com/management/nationalblood/apigateway/config/JwtDecoderConfig.java) - JWT decoder configuration

### 3.8 Discovery Server (`discovery-server`)

**Port:** 8761  
**Purpose:** Eureka service discovery

**Key Features:**

- Service registration
- Service discovery
- Health monitoring

**Key Files:**

- [`DiscoveryServerApplication.java`](discovery-server/src/main/java/com/management/nationalblood/discoveryserver/DiscoveryServerApplication.java) - Eureka server

---

## 4. Data Models

### User Roles (Identity Service)

```
Role Enum:
- USER - Basic user
- SUPER_USER - Super administrator
- ADMIN - Zone administrator
- COUNSELOR - Donor counselor
- LAB_TECHNICIAN - Laboratory staff
- ORGANIZER - Event organizer
```

### Donor Status (Meeting Service)

```
DonorStatus Enum:
- PENDING - Initial state
- APPROVED - Approved for donation
- DEFERRED - Temporarily deferred
- REJECTED - Permanently rejected
- DONATED - Successfully donated
```

### Meeting Status (Meeting Service)

```
MeetingStatus Enum:
- PLANNED - Scheduled but not started
- IN_PROGRESS - Currently active
- COMPLETED - Finished
- CANCELLED - Cancelled
```

### Appointment Status (Donor Service)

```
AppointmentStatus Enum:
- SCHEDULED - Booked
- CONFIRMED - Confirmed by donor
- COMPLETED - Attended and donated
- CANCELLED - Cancelled
- NO_SHOW - Did not attend
```

---

## 5. Security & Authentication

### JWT Authentication Flow

```
1. Client → POST /api/v1/identity/users/auth/signin
2. Server → Validate credentials → Generate JWT token
3. Client → Include JWT in Authorization header
4. API Gateway → Validate JWT signature using RSA public key
5. API Gateway → Forward request to microservice
6. Microservice → Extract user info from JWT → Authorize
```

### Security Configuration

- **Algorithm:** RS512 (RSA with SHA-512)
- **Key:** RSA Public/Private key pair
- **Token Expiration:** Configurable (typically 1 hour)
- **Refresh Token:** Supported

### Role-Based Access Control

```java
// Example: Checking roles in User entity
public boolean isAdmin() {
    return this.roles.contains(Role.ADMIN);
}

public boolean isZoneAdmin() {
    return this.zoneId != null && this.roles.contains(Role.ADMIN);
}

public boolean isLabTechnician() {
    return this.roles.contains(Role.LAB_TECHNICIAN);
}
```

### Public Endpoints (No Authentication Required)

- `/api/v1/identity/users/auth/**` - Authentication endpoints
- `/api/v1/donors/appointments/create` - Create appointment
- `/v3/api-docs/**` - Swagger documentation
- `/swagger-ui/**` - Swagger UI
- `/eureka/**` - Eureka dashboard
- `/actuator/**` - Spring Boot Actuator

---

## 6. Event-Driven Communication

### Kafka Topics

The system uses Kafka for asynchronous communication between services:

| Topic                     | Publisher        | Consumer             | Purpose                |
| ------------------------- | ---------------- | -------------------- | ---------------------- |
| `donor-auth-created`      | Identity Service | Donor Service        | Create donor auth      |
| `donor-notification`      | Donor Service    | Notification Service | Send SMS               |
| `staff-assignment`        | Identity Service | Meeting Service      | Assign staff           |
| `admin-zone-assignment`   | Identity Service | Zone Service         | Assign admin to zone   |
| `admin-zone-unassignment` | Identity Service | Zone Service         | Remove admin from zone |

### Event Classes

```java
// Example event structure
public class DonorNotificationEvent {
    private UUID donorId;
    private String phoneNumber;
    private String message;
    private LocalDateTime scheduledTime;
}
```

### Kafka Configuration

- **Broker:** Kafka broker 1 (confluentinc/cp-kafka:7.0.1)
- **Zookeeper:** Zookeeper for cluster management
- **Replication Factor:** 1 (development)
- **Port External:** 9092
- **Port Internal:** 19092

---

## 7. Infrastructure & DevOps

### Docker Compose Services

| Service                       | Container Name                | Port        | Database        |
| ----------------------------- | ----------------------------- | ----------- | --------------- |
| postgres-donor-service        | postgres-donor-service        | 5432        | donor_db        |
| postgres-identity-service     | postgres-identity-service     | 5432        | identity_db     |
| postgres-zone-service         | postgres-zone-service         | 5432        | zone_db         |
| postgres-meeting-service      | postgres-meeting-service      | 5432        | meeting_db      |
| postgres-notification-service | postgres-notification-service | 5432        | notification_db |
| postgres-laboratory-service   | postgres-laboratory-service   | 5432        | laboratory_db   |
| zookeeper                     | zookeeper                     | 2181        | -               |
| kafka-broker-1                | kafka-broker                  | 9092, 19092 | -               |
| zipkin                        | zipkin                        | 9411        | -               |
| discovery-server              | discovery-server              | 8761        | -               |
| api-gateway                   | api-gateway                   | 8080        | -               |
| donor-service                 | donor-service                 | 8083        | -               |
| identity-service              | identity-service              | 8081        | -               |
| zone-service                  | zone-service                  | 8082        | -               |
| meeting-service               | meeting-service               | 8084        | -               |
| notification-service          | notification-service          | 8085        | -               |
| laboratory-service            | laboratory-service            | 8086        | -               |
| prometheus                    | prometheus                    | 9090        | -               |
| grafana                       | grafana                       | 3000        | -               |

### Monitoring & Observability

- **Prometheus:** http://localhost:9090
  - Collects metrics from all microservices
  - Pre-configured dashboards
- **Grafana:** http://localhost:3000
  - Default login: admin / password
  - Visualizes Prometheus metrics
  - Pre-loaded dashboard in `Grafana_Dashboard.json`

- **Zipkin:** http://localhost:9411
  - Distributed tracing
  - Request flow visualization
  - Dependency graph

### Docker Resource Requirements

- **Minimum RAM:** 8 GB
- **Recommended RAM:** 16 GB
- **Docker Desktop:** Settings > Resources > Advanced > Memory: 8 GB+

### Kubernetes Deployment

The project includes Kubernetes manifests documentation in [`KUBERNETES.md`](KUBERNETES.md):

- Deployment manifests for PostgreSQL
- Service configuration
- Secret management from .env file
- Port forwarding for external access

---

## 8. How to Run

### Prerequisites

1. **Docker Desktop** (Windows/Mac) or **Docker Engine** (Linux)
2. **Docker Compose** v2.0+
3. **8 GB+ RAM** allocated to Docker
4. **Git** for cloning

### Steps to Run

#### Step 1: Clone the Repository

```bash
git clone https://github.com/CEO-LAYSON/national-blood-management-system.git
cd national-blood-management-system
```

#### Step 2: Configure Environment Variables

```bash
# Copy example env file
cp .env.example .env

# Edit .env with your configuration
nano .env
```

Required environment variables in `.env`:

```env
# Database Configuration
POSTGRES_DONOR_DB=donor_db
POSTGRES_DONOR_USER=donor_user
POSTGRES_DONOR_PASS=donor_password
POSTGRES_DONOR_PORT=5432

POSTGRES_IDENTITY_DB=identity_db
POSTGRES_IDENTITY_USER=identity_user
POSTGRES_IDENTITY_PASS=identity_password
POSTGRES_IDENTITY_PORT=5432

POSTGRES_ZONE_DB=zone_db
POSTGRES_ZONE_USER=zone_user
POSTGRES_ZONE_PASS=zone_password
POSTGRES_ZONE_PORT=5432

POSTGRES_MEETING_DB=meeting_db
POSTGRES_MEETING_USER=meeting_user
POSTGRES_MEETING_PASS=meeting_password
POSTGRES_MEETING_PORT=5432

POSTGRES_NOTIFICATION_DB=notification_db
POSTGRES_NOTIFICATION_USER=notification_user
POSTGRES_NOTIFICATION_PASS=notification_password
POSTGRES_NOTIFICATION_PORT=5432

POSTGRES_LABORATORY_DB=laboratory_db
POSTGRES_LABORATORY_USER=laboratory_user
POSTGRES_LABORATORY_PASS=laboratory_password
POSTGRES_LABORATORY_PORT=5432

# Kafka Configuration
ZOOKEEPER_PORT=2181
KAFKA_HOST=kafka-broker-1
KAFKA_PORT_EXTERNAL=9092
KAFKA_PORT_INTERNAL=19092

# Eureka Configuration
EUREKA_USERNAME=eureka_user
EUREKA_PASSWORD=eureka_password
EUREKA_HOST=discovery-server
EUREKA_PORT=8761

# SMS Configuration (Beem Africa)
SMS_USERNAME=your_beem_username
SMS_PASSWORD=your_beem_password
SMS_SOURCE=NBTS
SMS_URL=https://apisms.beem.africa/v1/send

# Grafana Configuration
GF_SECURITY_ADMIN_USER=admin
GF_SECURITY_ADMIN_PASSWORD=password

# Docker Hub
DOCKER_USERNAME=your_dockerhub_username
```

#### Step 3: Start Docker Compose

```bash
# Start all services in detached mode
docker compose up -d

# View logs
docker compose logs -f

# Check running containers
docker ps
```

#### Step 4: Access Services

| Service          | URL                   | Credentials                   |
| ---------------- | --------------------- | ----------------------------- |
| API Gateway      | http://localhost:8080 | -                             |
| Eureka Dashboard | http://localhost:8761 | eureka_user / eureka_password |
| Prometheus       | http://localhost:9090 | -                             |
| Grafana          | http://localhost:3000 | admin / password              |
| Zipkin           | http://localhost:9411 | -                             |

#### Step 5: Build and Run Locally (Without Docker)

```bash
# Build all microservices
mvn clean package -DskipTests

# Run each service individually
java -jar donor-service/target/donor-service.jar
java -jar identity-service/target/identity-service.jar
java -jar zone-service/target/zone-service.jar
java -jar meeting-service/target/meeting-service.jar
java -jar notification-service/target/notification-service.jar
java -jar laboratory-service/target/laboratory-service.jar
java -jar api-gateway/target/api-gateway.jar
java -jar discovery-server/target/discovery-server.jar
```

#### Step 6: Run Tests

```bash
# Run all tests
mvn test

# Run specific service tests
mvn test -pl donor-service
mvn test -pl identity-service
```

---

## 📁 Project Directory Structure

```
national-blood-management-system/
├── .env.example                    # Environment template
├── .gitignore
├── docker-compose.yml             # Docker Compose configuration
├── pom.xml                        # Parent Maven POM
├── README.md                      # Project documentation
├── KUBERNETES.md                  # Kubernetes deployment guide
├── Grafana_Dashboard.json         # Grafana dashboard config
├── wait-for-it.sh                 # Wait for services script
│
├── api-gateway/                   # API Gateway service
│   ├── pom.xml
│   └── src/main/java/com/management/nationalblood/apigateway/
│       ├── ApiGatewayApplication.java
│       └── config/
│           ├── SecurityConfig.java
│           └── JwtDecoderConfig.java
│
├── discovery-server/              # Eureka Server
│   ├── pom.xml
│   └── src/main/java/com/management/nationalblood/discoveryserver/
│       └── DiscoveryServerApplication.java
│
├── donor-service/                 # Donor Management
│   ├── pom.xml
│   └── src/main/java/com/nbts/management/donor_service/
│       ├── entity/
│       │   ├── Donor.java
│       │   └── Appointment.java
│       ├── controller/
│       │   ├── DonorController.java
│       │   └── AppointmentController.java
│       └── service/impl/
│           ├── DonorServiceImpl.java
│           └── AppointmentServiceImpl.java
│
├── identity-service/              # Authentication & User Management
│   ├── pom.xml
│   └── src/main/java/com/nbtsms/identity_service/
│       ├── entity/
│       │   └── User.java
│       ├── enums/
│       │   └── Role.java
│       ├── controller/
│       │   └── AuthenticationController.java
│       └── service/impl/
│           └── AuthenticationServiceImpl.java
│
├── zone-service/                  # Zone & Center Management
│   ├── pom.xml
│   └── src/main/java/com/nbtsms/zone_service/
│       ├── entity/
│       │   ├── Zone.java
│       │   ├── Region.java
│       │   ├── Center.java
│       │   └── Staff.java
│       └── controller/
│           ├── ZoneController.java
│           ├── RegionController.java
│           └── CenterController.java
│
├── meeting-service/               # Donation Sessions & Assessments
│   ├── pom.xml
│   └── src/main/java/com/management/nationalblood/meeting/
│       ├── entity/
│       │   ├── Meeting.java
│       │   ├── Questionnaire.java
│       │   ├── PhysicalExamination.java
│       │   └── BloodCollectionData.java
│       ├── controller/
│       │   ├── MeetingController.java
│       │   └── AssessmentController.java
│       └── enums/
│           ├── MeetingStatus.java
│           └── DonorStatus.java
│
├── notification-service/          # SMS Notifications
│   ├── pom.xml
│   └── src/main/java/com/management/nationalblood/notificationservice/
│       ├── service/impl/
│       │   └── NotificationServiceImpl.java
│       └── config/
│           └── SmsProperties.java
│
└── laboratory-service/            # Blood Testing & Inventory
    ├── pom.xml
    └── src/main/java/com/nbts/management/laboratory_service/
        └── (Service implementation in progress)
```

---

## 🔑 Key Learning Points

### 1. Microservices Architecture

- Each service is independent and can be deployed separately
- Database per service ensures loose coupling
- Service discovery via Eureka allows dynamic scaling

### 2. Event-Driven Design

- Kafka enables asynchronous communication
- Services publish events when actions occur
- Other services consume events and react accordingly

### 3. Security Best Practices

- JWT for stateless authentication
- RSA keys for signature verification
- Role-based authorization at multiple levels
- API Gateway as central security point

### 4. Monitoring & Observability

- Prometheus collects metrics
- Grafana visualizes data
- Zipkin traces requests across services

### 5. Containerization Benefits

- Consistent environments
- Easy scaling
- Isolation between services
- Simple deployment

---

## 📚 Additional Resources

- **Spring Boot Documentation:** https://spring.io/projects/spring-boot
- **Spring Cloud:** https://spring.io/projects/spring-cloud
- **Docker Docs:** https://docs.docker.com/
- **Kafka:** https://kafka.apache.org/documentation/
- **PostgreSQL:** https://www.postgresql.org/docs/
- **Beem Africa SMS:** https://docs.beem.africa/
- **Prometheus:** https://prometheus.io/docs/
- **Grafana:** https://grafana.com/docs/

---

## ⚠️ Notes

1. **Laboratory Service** is still under development
2. **Meeting Service** has the most complex entity relationships
3. All services follow similar patterns for consistency:
   - Entity → Repository → Service → Controller
   - DTOs for API communication
   - Exception handling via GlobalExceptionHandler
   - JWT security on all endpoints

4. **Frontend:** This appears to be a backend-only project. A separate frontend (likely React/Vue) would be needed for full functionality.

---

_Document Generated: January 2026_  
_Project: National Blood Transfusion Service Management System_
