# Step-by-Step Configuration Guide

## Step 1: Open Terminal in Project Directory

```bash
cd "d:/MY DOCUMENTS/PROJECTS/FYP/national-blood-management-system"
```

## Step 2: Create .env File

```bash
cp .env.example .env
```

## Step 3: Edit .env File

Open `.env` in a text editor (Notepad, VS Code, etc.) and configure each section:

---

## 📌 PART 1: Database Credentials (6 Databases)

### Database 1: Donor Service

```
POSTGRES_DONOR_DB=donor_service
POSTGRES_DONOR_USER=donor_user
POSTGRES_DONOR_PASS=donor_pass      ← Change this password!
POSTGRES_DONOR_PORT=5434
```

**Action:** Change `donor_pass` to any secure password (e.g., `Donor123!`)

---

### Database 2: Identity Service

```
POSTGRES_IDENTITY_DB=identity_service
POSTGRES_IDENTITY_USER=identity_user
POSTGRES_IDENTITY_PASS=identity_pass   ← Change this password!
POSTGRES_IDENTITY_PORT=5435
```

**Action:** Change `identity_pass` to any secure password (e.g., `Identity123!`)

---

### Database 3: Zone Service

```
POSTGRES_ZONE_DB=zone_service
POSTGRES_ZONE_USER=zone_user
POSTGRES_ZONE_PASS=zone_pass           ← Change this password!
POSTGRES_ZONE_PORT=5436
```

**Action:** Change `zone_pass` to any secure password (e.g., `Zone123!`)

---

### Database 4: Meeting Service

```
POSTGRES_MEETING_DB=meeting_service
POSTGRES_MEETING_USER=meeting_user
POSTGRES_MEETING_PASS=meeting_pass     ← Change this password!
POSTGRES_MEETING_PORT=5439
```

**Action:** Change `meeting_pass` to any secure password (e.g., `Meeting123!`)

---

### Database 5: Notification Service

```
POSTGRES_NOTIFICATION_DB=notification_service
POSTGRES_NOTIFICATION_USER=notification_user
POSTGRES_NOTIFICATION_PASS=notification_pass   ← Change this password!
POSTGRES_NOTIFICATION_PORT=5440
```

**Action:** Change `notification_pass` to any secure password (e.g., `Notification123!`)

---

### Database 6: Laboratory Service

```
POSTGRES_LABORATORY_DB=laboratory_service
POSTGRES_LABORATORY_USER=laboratory_user
POSTGRES_LABORATORY_PASS=laboratory_pass       ← Change this password!
POSTGRES_LABORATORY_PORT=5441
```

**Action:** Change `laboratory_pass` to any secure password (e.g., `Laboratory123!`)

---

## 📌 PART 2: Eureka Server Credentials

```
EUREKA_USERNAME=eureka
EUREKA_PASSWORD=your_eureka_password_here   ← CHANGE THIS!
EUREKA_HOST=discovery-server
EUREKA_PORT=8761
```

**Action:** Change `your_eureka_password_here` to a secure password (e.g., `EurekaPass123!`)

---

## 📌 PART 3: SMS Credentials (Beem Africa)

```
SMS_USERNAME=        ← Get from Beem Africa account
SMS_PASSWORD=        ← Get from Beem Africa account
SMS_SOURCE=          ← Your approved sender name from Beem
SMS_URL=https://apisms.beem.africa/v1/send
```

**Actions:**

1. Go to https://beem.africa/ and create account
2. Apply for Sender Name approval
3. Get API credentials from your Beem dashboard
4. Fill in:
   - `SMS_USERNAME` = Your Beem API username
   - `SMS_PASSWORD` = Your Beem API password
   - `SMS_SOURCE` = Your approved sender name (e.g., "NBTS")

**⚠️ NOTE:** If you don't have Beem Africa account yet, you can leave these blank for now. SMS notifications won't work but everything else will run fine.

---

## 📌 PART 4: Kafka & Zookeeper (NO CHANGES NEEDED)

```
KAFKA_HOST=kafka-broker-1
KAFKA_PORT_INTERNAL=19092
KAFKA_PORT_EXTERNAL=9092
ZOOKEEPER_PORT=2181
```

**✅ Leave these as they are!**

---

## 📌 PART 5: Grafana Dashboard Credentials

```
GF_SECURITY_ADMIN_USER=admin
GF_SECURITY_ADMIN_PASSWORD=password   ← Change this!
```

**Action:** Change `password` to a secure password (e.g., `GrafanaAdmin123!`)

---

## 📌 PART 6: Docker Hub Credentials

```
DOCKER_USERNAME=seranise
DOCKER_PASSWORD=access-token          ← Change this!
```

**Actions:**

1. Go to https://hub.docker.com/ and create account
2. Create an Access Token from Docker Hub settings
3. Fill in:
   - `DOCKER_USERNAME` = Your Docker Hub username
   - `DOCKER_PASSWORD` = Your Docker Access Token

**⚠️ NOTE:** If you're not pushing images to Docker Hub, you can skip this. The services will still run locally.

---

## 📝 Complete Example of Filled .env File

```env
# Postgres Donor Service
POSTGRES_DONOR_DB=donor_service
POSTGRES_DONOR_USER=donor_user
POSTGRES_DONOR_PASS=DonorPass123!
POSTGRES_DONOR_PORT=5434

# Postgres Identity Service
POSTGRES_IDENTITY_DB=identity_service
POSTGRES_IDENTITY_USER=identity_user
POSTGRES_IDENTITY_PASS=IdentityPass123!
POSTGRES_IDENTITY_PORT=5435

# Postgres Zone Service
POSTGRES_ZONE_DB=zone_service
POSTGRES_ZONE_USER=zone_user
POSTGRES_ZONE_PASS=ZonePass123!
POSTGRES_ZONE_PORT=5436

# Postgres Meeting Service
POSTGRES_MEETING_DB=meeting_service
POSTGRES_MEETING_USER=meeting_user
POSTGRES_MEETING_PASS=MeetingPass123!
POSTGRES_MEETING_PORT=5439

# Postgres Notification Service
POSTGRES_NOTIFICATION_DB=notification_service
POSTGRES_NOTIFICATION_USER=notification_user
POSTGRES_NOTIFICATION_PASS=NotificationPass123!
POSTGRES_NOTIFICATION_PORT=5440

# Postgres Laboratory Service
POSTGRES_LABORATORY_DB=laboratory_service
POSTGRES_LABORATORY_USER=laboratory_user
POSTGRES_LABORATORY_PASS=LaboratoryPass123!
POSTGRES_LABORATORY_PORT=5441

# Eureka Server
EUREKA_USERNAME=eureka
EUREKA_PASSWORD=EurekaPass123!
EUREKA_HOST=discovery-server
EUREKA_PORT=8761

# SMS Credentials
SMS_USERNAME=my_beem_user
SMS_PASSWORD=my_beem_password
SMS_SOURCE=NBTS
SMS_URL=https://apisms.beem.africa/v1/send

# Kafka ports
KAFKA_HOST=kafka-broker-1
KAFKA_PORT_INTERNAL=19092
KAFKA_PORT_EXTERNAL=9092

# Zookeeper port
ZOOKEEPER_PORT=2181

# Grafana Server
GF_SECURITY_ADMIN_USER=admin
GF_SECURITY_ADMIN_PASSWORD=GrafanaAdmin123!

# Docker Information
DOCKER_USERNAME=my_dockerhub_user
DOCKER_PASSWORD=my_dockerhub_token
```

---

## 🚀 Step 4: Start Docker Containers

After configuring `.env`:

```bash
docker compose up -d
```

---

## ✅ Quick Checklist

| Item                                  | Status |
| ------------------------------------- | ------ |
| [ ] Database passwords changed        | ⬜     |
| [ ] Eureka password changed           | ⬜     |
| [ ] Grafana password changed          | ⬜     |
| [ ] SMS credentials (optional)        | ⬜     |
| [ ] Docker Hub credentials (optional) | ⬜     |
| [ ] `.env` file saved                 | ⬜     |
| [ ] `docker compose up -d` executed   | ⬜     |

---

## ⚠️ Important Notes

1. **Save the .env file** after making changes
2. **Use strong passwords** - at least 8 characters with numbers and symbols
3. **Remember your passwords** - you'll need them to connect to databases externally
4. **If SMS fails to send** - check your Beem Africa credentials
5. **If Docker fails to start** - make sure Docker Desktop is running and has 8GB+ RAM allocated
