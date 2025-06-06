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
- **JWT Authentication**: Secure authentication system with JSON Web Tokens
- **Role-based Authorization**: Endpoints protected based on user roles

### Poll Management
- **Poll Creation**: Create polls with multiple options (2-10 options supported)
- **Poll Scheduling**: Schedule polls with start and end dates
- **Poll Status Management**: Support for SCHEDULED, OPEN, CLOSED, PAUSED, and CANCELED statuses
- **Automatic Poll Activation**: Scheduled polls are automatically activated via `PollScheduler`
- **Automatic Poll Closing**: Scheduled polls are automatically closed via `PollScheduler`
- **Poll Statistics**: Get comprehensive voting statistics including total votes and vote counts per option
- **Poll Validation**: Comprehensive validation rules for dates, options, and ownership

### Voting System
- **Asynchronous Vote Processing**: Votes are queued using RabbitMQ for reliable processing
- **Comprehensive Vote Validation**: Pre-submission validation ensuring users exist, poll options exist, and polls are open for voting
- **Vote Status Tracking**: Tracks vote processing status (UNPROCESSED → PROCESSED)
- **Real-time Poll Status Check**: Validates poll availability before accepting votes across all poll statuses (OPEN, CLOSED, SCHEDULED, PAUSED, CANCELED)
- **Vote Management**: List votes with pagination and retrieve individual vote details by ID
- **Vote Repository**: Both in-memory and JPA implementations for development and production environments

### Email Notifications
- **Automated Email Alerts**: Users receive email confirmations when their votes are processed
- **SMTP Integration**: Configured with MailHog for development environment
- **Template-based Messages**: Standardized email templates for vote confirmations

### Event-Driven Architecture
- **Domain Events**: [`VoteCreatedEvent`](src/main/java/com/example/poll_system/domain/entities/events/VoteCreatedEvent.java), [`VoteProcessedEvent`](src/main/java/com/example/poll_system/domain/entities/events/VoteProcessedEvent.java) and [`PollClosedEvent`](src/main/java/com/example/poll_system/domain/entities/events/PollClosedEvent.java)
- **Message Queue Integration**: RabbitMQ for reliable event processing
- **Decoupled Components**: Event-driven communication between services

### Automated Scheduling
- **Poll Scheduler**: Automatic activation of scheduled polls when start time is reached
- **Background Processing**: Scheduled task runs every minute to check for polls to activate
- **Status Transitions**: Seamless transition from SCHEDULED to OPEN status

### Comprehensive Validation System
- **Pre-vote Validation**: Multi-layer validation before vote submission
  - User existence validation with detailed logging
  - Poll option existence validation with specific error messages
  - Poll status validation across all states (OPEN, CLOSED, SCHEDULED, PAUSED, CANCELED)
- **Business Rules Enforcement**: Strict adherence to business logic through `BusinessRulesException`
- **Input Sanitization**: Comprehensive input validation for all user-provided data
- **Error Handling**: Detailed error messages and proper exception propagation

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
- [`ListUserPageable`](src/main/java/com/example/poll_system/application/usecases/user/impl/ListUserPageable.java) - Paginated user listing with role-based information
- [`Login`](src/main/java/com/example/poll_system/application/usecases/auth/Login.java) - User authentication with JWT token generation
- [`CreatePoll`](src/main/java/com/example/poll_system/application/usecases/poll/CreatePoll.java) - Poll creation with validation and scheduling
- [`ListPollPageable`](src/main/java/com/example/poll_system/application/usecases/poll/impl/ListPollPageable.java) - Paginated poll listing with status information
- [`ActivePoll`](src/main/java/com/example/poll_system/application/usecases/poll/ActivePoll.java) - Manual poll activation from SCHEDULED to OPEN status
- [`ClosePoll`](src/main/java/com/example/poll_system/application/usecases/poll/ClosePoll.java) - Manual poll closing from OPEN to CLOSED status
- [`PollStatistics`](src/main/java/com/example/poll_system/application/usecases/poll/PollStatistics.java) - Get comprehensive poll voting statistics and analytics
- [`SendVoteToQueue`](src/main/java/com/example/poll_system/application/usecases/vote/impl/SendVoteToQueue.java) - Vote creation with comprehensive validation (queued processing)
- [`ProcessVote`](src/main/java/com/example/poll_system/application/usecases/vote/ProcessVote.java) - Asynchronous vote processing with status management
- [`ListVotePageable`](src/main/java/com/example/poll_system/application/usecases/vote/impl/ListVotePageable.java) - Paginated vote listing with status information
- [`FindVoteById`](src/main/java/com/example/poll_system/application/usecases/vote/impl/FindVoteById.java) - Individual vote retrieval by ID
- [`SendEmailVoteProcessed`](src/main/java/com/example/poll_system/application/usecases/vote/impl/SendEmailVoteProcessed.java) - Email notification service

#### Controllers
- [`AuthController`](src/main/java/com/example/poll_system/infrastructure/controllers/AuthController.java) - Authentication endpoints
- [`UserController`](src/main/java/com/example/poll_system/infrastructure/controllers/UserController.java) - User management endpoints
- [`PollController`](src/main/java/com/example/poll_system/infrastructure/controllers/PollController.java) - Poll management endpoints
- [`VoteController`](src/main/java/com/example/poll_system/infrastructure/controllers/VoteController.java) - Voting endpoints

#### Schedulers
- [`PollScheduler`](src/main/java/com/example/poll_system/infrastructure/services/schedulers/PollScheduler.java) - Automatic poll activation and deactivation service

#### Event Listeners
- [`CreateVoteListener`](src/main/java/com/example/poll_system/infrastructure/services/listeners/CreateVoteListener.java) - Processes vote creation events
- [`SendEmailVoteProcessedListener`](src/main/java/com/example/poll_system/infrastructure/services/listeners/SendEmailVoteProcessedListener.java) - Handles email notification events
- [`SendEmailPollClosedListener`](src/main/java/com/example/poll_system/infrastructure/services/listeners/SendEmailPollClosedListener.java) - Handles email notification poll close events

#### Data Persistence
- **Repository Pattern**: Clean separation between domain logic and data persistence
- **Multiple Implementations**: Both in-memory and JPA-based repositories for flexibility
- **Vote Repository**: [`VoteRepositoryInMemory`](src/main/java/com/example/poll_system/infrastructure/persistence/VoteRepositoryInMemory.java), [`VoteRepositoryJpa`](src/main/java/com/example/poll_system/infrastructure/persistence/jpa/repositories/VoteRepositoryJpa.java)
- **Custom Queries**: Specialized queries for vote counting and statistics aggregation
- **Profile-based Configuration**: Automatic selection between in-memory and JPA implementations

## 🛠️ Technologies

- **Framework**: Spring Boot 3.4.5 with `@EnableScheduling` for automated tasks
- **Language**: Java 21
- **Message Queue**: RabbitMQ for asynchronous processing
- **Object Storage**: MinIO for profile image storage
- **Email**: Spring Mail with MailHog for development
- **Security**: Spring Security with BCrypt password hashing and JWT authentication
- **Authentication**: JSON Web Tokens (JWT) for stateless authentication
- **Authorization**: Role-based access control (RBAC)
- **Testing**: JUnit 5, Mockito for comprehensive unit testing
- **Build Tool**: Maven with Surefire for test execution
- **Scheduling**: Spring `@Scheduled` for poll automation

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

### Authentication
- `POST /api/v1/auth/login` - Authenticate user and get JWT token

### Users
- `POST /api/v1/users` - Create a new user with profile image
- `GET /api/v1/users` - List all users with pagination (requires authentication)
- `GET /api/v1/users/{userId}` - Get user details by ID (requires authentication)

### Polls
- `POST /api/v1/polls` - Create a new poll with options and scheduling (requires authentication)
- `GET /api/v1/polls` - List all polls with pagination (requires authentication)
- `GET /api/v1/polls/{pollId}` - Get poll details by ID (requires authentication)
- `GET /api/v1/polls/{pollId}/statistics` - Get comprehensive poll statistics including vote counts and totals (requires authentication)

### Votes
- `POST /api/v1/votes` - Submit a vote with comprehensive validation (asynchronous processing, requires authentication)
- `GET /api/v1/votes` - List all votes with pagination (requires authentication)
- `GET /api/v1/votes/{voteId}` - Get vote details by ID (requires authentication)

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

# JWT Authentication
app.jwt.secret=mySecretKey123456789012345678901234567890abcdefghijklmnopqrstuvwxyz
app.jwt.expiration=86400
```

## ⏰ Automated Scheduling

The system includes an automated polling scheduler that manages poll lifecycle:

### PollScheduler Features
- **Automatic Activation**: Scheduled polls are automatically opened when their start time is reached
- **Fixed Rate Execution**: Runs every 60 seconds to check for polls ready to be activated
- **Efficient Querying**: Only retrieves polls with `SCHEDULED` status for processing
- **Logging**: Provides console output for monitoring poll activation events

### Configuration
The scheduler is enabled via the `@EnableScheduling` annotation in the main application class and runs with a fixed rate of 60,000 milliseconds (1 minute).

### Use Cases Integration
- **Manual Activation**: Use [`ActivePoll`](src/main/java/com/example/poll_system/application/usecases/poll/ActivePoll.java) use case for immediate poll activation
- **Scheduled Activation**: [`PollScheduler`](src/main/java/com/example/poll_system/infrastructure/services/schedulers/PollScheduler.java) handles automatic activation based on start dates

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

- **Use Cases**: [`CreateUserImplTest`](src/test/java/com/example/poll_system/application/usecases/user/impl/CreateUserImplTest.java), [`ListUserPageableTest`](src/test/java/com/example/poll_system/application/usecases/user/impl/ListUserPageableTest.java), [`CreatePollImplTest`](src/test/java/com/example/poll_system/application/usecases/poll/impl/CreatePollImplTest.java), [`ListPollPageableTest`](src/test/java/com/example/poll_system/application/usecases/poll/impl/ListPollPageableTest.java), [`ActivePollImplTest`](src/test/java/com/example/poll_system/application/usecases/poll/impl/ActivePollImplTest.java), [`ClosePollImplTest`](src/test/java/com/example/poll_system/application/usecases/poll/impl/ClosePollImplTest.java), [`PollStatisticsImplTest`](src/test/java/com/example/poll_system/application/usecases/poll/impl/PollStatisticsImplTest.java), [`ProcessVoteImplTest`](src/test/java/com/example/poll_system/application/usecases/vote/impl/ProcessVoteImplTest.java), [`SendVoteToQueueTest`](src/test/java/com/example/poll_system/application/usecases/vote/impl/SendVoteToQueueTest.java), [`ListVotePageableTest`](src/test/java/com/example/poll_system/application/usecases/vote/impl/ListVotePageableTest.java), [`FindVoteByIdTest`](src/test/java/com/example/poll_system/application/usecases/vote/impl/FindVoteByIdTest.java), [`SendEmailVoteProcessedTest`](src/test/java/com/example/poll_system/application/usecases/vote/impl/SendEmailVoteProcessedTest.java)
- **Domain Entities**: [`PollTest`](src/test/java/com/example/poll_system/domain/entities/PollTest.java), [`VoteTest`](src/test/java/com/example/poll_system/domain/entities/VoteTest.java), [`UserTest`](src/test/java/com/example/poll_system/domain/entities/UserTest.java), [`PollOptionTest`](src/test/java/com/example/poll_system/domain/entities/PollOptionTest.java)
- **Domain Events**: [`VoteCreatedEventTest`](src/test/java/com/example/poll_system/domain/entities/events/VoteCreatedEventTest.java), [`VoteProcessedEventTest`](src/test/java/com/example/poll_system/domain/entities/events/VoteProcessedEventTest.java)
- **Factories**: [`PollFactoryTest`](src/test/java/com/example/poll_system/domain/factories/PollFactoryTest.java), [`UserFactoryTest`](src/test/java/com/example/poll_system/domain/factories/UserFactoryTest.java), [`VoteFactoryTest`](src/test/java/com/example/poll_system/domain/factories/VoteFactoryTest.java)
- **Integration Tests**: Queue processing, email sending, and vote validation workflows

## 🔄 Workflow

1. **User Registration**: Users register with profile image upload to MinIO
2. **User Management**: Administrators can list all users with pagination and role information
3. **Poll Creation**: Admins create polls with scheduling options (immediate or future start)
4. **Poll Management**: List polls with pagination, view individual poll details, and get comprehensive statistics
5. **Automatic Poll Activation**: [`PollScheduler`](src/main/java/com/example/poll_system/infrastructure/services/schedulers/PollScheduler.java) automatically activates scheduled polls when start time is reached
6. **Vote Submission**: Votes undergo comprehensive validation (user exists, poll option exists, poll is open) and are queued in RabbitMQ via [`SendVoteToQueue`](src/main/java/com/example/poll_system/application/usecases/vote/impl/SendVoteToQueue.java)
7. **Asynchronous Processing**: [`CreateVoteListener`](src/main/java/com/example/poll_system/infrastructure/services/listeners/CreateVoteListener.java) processes votes from queue using [`ProcessVoteImpl`](src/main/java/com/example/poll_system/application/usecases/vote/impl/ProcessVoteImpl.java)
8. **Vote Management**: Administrators can list votes with pagination and retrieve individual vote details by ID
9. **Poll Closing**: [`PollScheduler`](src/main/java/com/example/poll_system/infrastructure/services/schedulers/PollScheduler.java) automatically closing scheduled polls when ended time is reached
10. **Email Notification**: [`SendEmailListener`](src/main/java/com/example/poll_system/infrastructure/services/listeners/SendEmailListener.java) sends confirmation emails
11. **Event Tracking**: All actions are tracked via domain events (`VoteCreatedEvent`, `VoteProcessedEvent`)

### Poll Lifecycle

1. **SCHEDULED** → Poll is created with future start date
2. **OPEN** → Poll is activated and accepting votes (manual via [`ActivePoll`](src/main/java/com/example/poll_system/application/usecases/poll/ActivePoll.java) or automatic via [`PollScheduler`](src/main/java/com/example/poll_system/infrastructure/services/schedulers/PollScheduler.java))
3. **CLOSED** → Poll is closed and no longer accepting votes automatic when end date is reached)

## 📊 Analytics and Data Management

### Pagination Support
The system provides comprehensive pagination support across all major entities:

- **User Pagination**: [`ListUserPageable`](src/main/java/com/example/poll_system/application/usecases/user/impl/ListUserPageable.java) - Efficient user listing with role-based filtering
- **Poll Pagination**: [`ListPollPageable`](src/main/java/com/example/poll_system/application/usecases/poll/impl/ListPollPageable.java) - Poll browsing with status-based filtering
- **Vote Pagination**: [`ListVotePageable`](src/main/java/com/example/poll_system/application/usecases/vote/impl/ListVotePageable.java) - Vote tracking with processing status information

### Statistics and Analytics
- **Poll Statistics**: Real-time vote counting and distribution analysis via [`PollStatistics`](src/main/java/com/example/poll_system/application/usecases/poll/PollStatistics.java)
- **Vote Tracking**: Individual vote retrieval and status monitoring through [`FindVoteById`](src/main/java/com/example/poll_system/application/usecases/vote/impl/FindVoteById.java)
- **User Analytics**: Role-based user management with comprehensive profile information

### Data Consistency
- **Event-Driven Updates**: Real-time data consistency through domain events
- **Asynchronous Processing**: Vote processing maintains data integrity while providing responsive user experience
- **Status Validation**: Multi-layer validation ensures data consistency across all operations

## 📝 Contributing

1. Fork the repository
2. Create a feature branch
3. Write tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.