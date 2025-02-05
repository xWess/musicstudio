# Music Studio Project - Team Split Guide

This document outlines how the Music Studio project is split among 5 contributors for parallel development.

## Module Assignments

### 1. User Management & Authentication Module
**Responsible for:**
- Login/Registration functionality
- User profile management
- Role-based access control
- Password management

**Key files:**
- src/main/java/asm/org/MusicStudio/controllers/LoginController.java
- src/main/java/asm/org/MusicStudio/util/PasswordUtil.java
- src/main/java/asm/org/MusicStudio/services/AuthService.java
- src/main/java/asm/org/MusicStudio/services/UserService.java

### 2. Schedule & Course Management Module
**Responsible for:**
- Course creation and management
- Schedule planning
- Room allocation
- Time slot management

**Key files:**
- src/main/java/asm/org/MusicStudio/controllers/ScheduleController.java
- src/main/java/asm/org/MusicStudio/services/ScheduleService.java
- src/main/java/asm/org/MusicStudio/services/CourseService.java
- src/main/java/asm/org/MusicStudio/entity/Schedule.java

### 3. Payment & Enrollment Module
**Responsible for:**
- Payment processing
- Course enrollment
- Fee management
- Financial reporting

**Key files:**
- src/main/java/asm/org/MusicStudio/controllers/EnrollmentDialogController.java
- src/main/java/asm/org/MusicStudio/services/PaymentService.java
- src/main/java/asm/org/MusicStudio/entity/Payment.java
- src/main/java/asm/org/MusicStudio/entity/Enrollment.java

### 4. Admin Dashboard & Reporting Module
**Responsible for:**
- Admin interface
- User management dashboard
- System monitoring
- Reporting features

**Key files:**
- src/main/java/asm/org/MusicStudio/controllers/AdminController.java
- src/main/java/asm/org/MusicStudio/services/ReportService.java
- src/main/java/asm/org/MusicStudio/util/ReportGenerator.java

### 5. UI/UX & Frontend Module
**Responsible for:**
- JavaFX UI components
- CSS styling
- Layout management
- User interface improvements

**Key files:**
- src/main/resources/styles/main.css
- src/main/resources/fxml/*.fxml
- src/main/java/asm/org/MusicStudio/util/WindowManager.java
- src/main/java/asm/org/MusicStudio/util/IconHelper.java

## Collaboration Guidelines

### Database Schema Management
- Create a separate `database` branch for schema changes
- Use migration scripts for database updates
- Coordinate changes through the team lead

### Shared Components
- Keep shared utilities in a common package
- Document API changes in a shared document
- Use interface-based development

### Version Control Strategy
- Create feature branches from `develop`
- Branch naming convention: `feature/module-name/feature-description`
- Require code reviews before merging

### Communication Channels
- Set up a dedicated channel for each module
- Regular sync-ups between dependent modules
- Document API changes and dependencies

### Testing Strategy
- Each module should maintain its own test suite
- Integration tests for module interactions
- End-to-end tests for critical flows

## Module Dependencies

The modules have the following dependencies:
- User Management is required by Admin Dashboard
- Schedule Management feeds into Admin Dashboard
- Payment & Enrollment depends on User Management and Schedule Management
- UI/UX module interacts with all other modules

## Getting Started

1. Clone the repository
2. Create your module-specific branch
3. Review the files in your assigned module
4. Set up your development environment
5. Coordinate with team members on shared components

## Code Review Process

1. Create a Pull Request (PR) when feature is ready
2. Assign reviewers from dependent modules
3. Address review comments
4. Get approval from at least two team members
5. Merge only when CI/CD pipeline passes

## Contact

For questions about module assignments or collaboration, contact the team lead. 