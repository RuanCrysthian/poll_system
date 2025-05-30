# Poll System

A robust polling system built with Spring Boot that allows users to create polls, vote, and receive email notifications when votes are processed.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Configuration](#configuration)
- [Infrastructure](#infrastructure)
- [Testing](#testing)
- [Contributing](#contributing)

## 🎯 Overview

The Poll System is an application that enables users to participate in polls through a RESTful API. The system implements Clean Architecture principles with domain-driven design, ensuring scalability and maintainability.

## ✨ Features

### User Management
- **User Registration**: Create new users with profile image upload
- **Role-based Access**: Support for Admin and Voter roles
- **Profile Management**: Upload and manage user profile images via MinIO object storage
- **Input Validation**: CPF and email uniqueness validation

### Voting System
- **Asynchronous Vote Processing**: Votes are queued using RabbitMQ for reliable processing
- **Vote Validation**: Ensures users and poll options exist before processing
- **Vote Status Tracking**: Tracks vote processing status (UNPROCESSED → PROCESSED)

### Email Notifications
- **Automated Email Alerts**: Users receive email confirmations when their votes are processed
- **SMTP Integration**: Configured with MailHog for development environment
- **Template-based Messages**: Standardized email templates for vote confirmations

### Event-Driven Architecture
- **Domain Events**: [`VoteCreatedEvent`](src/main/java/com/example/poll_system/domain/entities/events/VoteCreatedEvent.java) and [`VoteProcessedEvent`](src/main/java/com/example/poll_system/domain/entities/events/VoteProcessedEvent.java)
- **Message Queue Integration**: RabbitMQ for reliable event processing
- **Decoupled Components**: Event-driven communication between services

## 🏗️ Architecture

The application follows Clean Architecture principles with the following layers:

```
src/main/java/com/example/poll_system/
├── application/
│   └── usecases/           # Business use cases
├── domain/
│   ├── entities/           # Core business entities
│   ├── events/             # Domain events
│   ├── factories/          # Entity factories
│   ├── gateways/           # Repository interfaces
│   └── value_objects/      # Value objects (CPF, Email)
└── infrastructure/
    ├── config/             # Spring configurations
    ├── controllers/        # REST controllers
    ├── persistence/        # Repository implementations
    └── services/           # External service implementations
```

### Key Components

#### Use Cases
- [`CreateUser`](src/main/java/com/example/poll_system/application/usecases/user/CreateUser.java) - User registration with image upload
- [`CreateVote`](src/main/java/com/example/poll_system/application/usecases/vote/CreateVote.java) - Vote creation (queued processing)
- [`ProcessVote`](src/main/java/com/example/poll_system/application/usecases/vote/ProcessVote.java) - Asynchronous vote processing
- [`SendEmailVoteProcessed`](src/main/java/com/example/poll_system/application/usecases/vote/impl/SendEmailVoteProcessed.java) - Email notification service

#### Controllers
- [`UserController`](src/main/java/com/example/poll_system/infrastructure/controllers/UserController.java) - User management endpoints
- [`VoteController`](src/main/java/com/example/poll_system/infrastructure/controllers/VoteController.java) - Voting endpoints

#### Event Listeners
- [`CreateVoteListener`](src/main/java/com/example/poll_system/infrastructure/services/listeners/CreateVoteListener.java) - Processes vote creation events
- [`SendEmailListener`](src/main/java/com/example/poll_system/infrastructure/services/listeners/SendEmailListener.java) - Handles email notification events

## 🛠️ Technologies

- **Framework**: Spring Boot 3.4.5
- **Language**: Java 21
- **Message Queue**: RabbitMQ
- **Object Storage**: MinIO
- **Email**: Spring Mail with MailHog
- **Security**: Spring Security with BCrypt
- **Testing**: JUnit 5, Mockito
- **Build Tool**: Maven

## 🚀 Getting Started

### Prerequisites

- Java 21
- Docker and Docker Compose
- Maven 3.6+

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd pool_system
   ```

2. **Start infrastructure services**
   ```bash
   docker-compose up -d
   ```

3. **Build the application**
   ```bash
   ./mvnw clean install
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

The application will be available at `http://localhost:8090`

### Infrastructure Services

After running `docker-compose up -d`, the following services will be available:

- **RabbitMQ Management**: http://localhost:15672 (guest/guest)
- **MinIO Console**: http://localhost:9001 (ROOTNAME/CHANGEME123)
- **MailHog Web UI**: http://localhost:8025

## 📡 API Endpoints

### Users
- `POST /api/v1/users` - Create a new user with profile image
- `GET /api/v1/users` - List all users

### Votes
- `POST /api/v1/votes` - Submit a vote (asynchronous processing)
- `GET /api/v1/votes` - List all votes

## ⚙️ Configuration

Key configuration properties in [`application.properties`](src/main/resources/application.properties):

```properties
# Server
server.port=8090

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
app.rabbitmq.exchange=vote
app.rabbitmq.vote-queue=vote-queue
app.rabbitmq.email-queue=email-queue

# Email (MailHog)
spring.mail.host=localhost
spring.mail.port=1025

# Retry Configuration
spring.rabbitmq.listener.simple.retry.enabled=true
spring.rabbitmq.listener.simple.retry.max-attempts=5
```

## 🐳 Infrastructure

The [`docker-compose.yml`](docker-compose.yml) includes:

- **MinIO**: Object storage for user profile images
- **RabbitMQ**: Message queue for asynchronous processing
- **MailHog**: SMTP server for email testing

## 🧪 Testing

The project includes comprehensive unit tests:

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=CreateUserImplTest
```

### Test Coverage

- **Use Cases**: [`CreateUserImplTest`](src/test/java/com/example/poll_system/application/usecases/user/impl/CreateUserImplTest.java), [`SendEmailVoteProcessedTest`](src/test/java/com/example/poll_system/application/usecases/vote/impl/SendEmailVoteProcessedTest.java)
- **Domain Events**: [`VoteCreatedEventTest`](src/test/java/com/example/poll_system/domain/entities/events/VoteCreatedEventTest.java), [`VoteProcessedEventTest`](src/test/java/com/example/poll_system/domain/entities/events/VoteProcessedEventTest.java)
- **Integration Tests**: Queue processing and email sending

## 🔄 Workflow

1. **User Registration**: Users register with profile image upload to MinIO
2. **Vote Submission**: Votes are validated and queued in RabbitMQ
3. **Asynchronous Processing**: [`CreateVoteListener`](src/main/java/com/example/poll_system/infrastructure/services/listeners/CreateVoteListener.java) processes votes from queue
4. **Email Notification**: [`SendEmailListener`](src/main/java/com/example/poll_system/infrastructure/services/listeners/SendEmailListener.java) sends confirmation emails
5. **Event Tracking**: All actions are tracked via domain events

## 📝 Contributing

1. Fork the repository
2. Create a feature branch
3. Write tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.