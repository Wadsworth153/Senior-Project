# VA Resource & Action Planner

**CST-452 -- Senior Project II**\
**Instructor:** Charles Thompson

The VA Resource & Action Planner is an educational web application
prototype designed to bring veteran profile information, a simplified
eligibility illustration, simulated claim tracking, personal goals, and
action-planning tools into one authenticated workspace.

> **Important:** This application is a student project prototype. It is
> not an official Department of Veterans Affairs (VA) product, does not
> provide official benefit or eligibility determinations, does not
> submit real claims, and does not retrieve live claim information. Its
> eligibility and claim information is simulated. For actual benefits
> and eligibility information, consult official VA resources.

## Project objectives

The project explores how a centralized application can help users
organize benefit-related information and plan follow-up activities. Its
main objectives are to:

-   Provide account registration and sign-in.
-   Maintain a veteran profile.
-   Demonstrate a simplified, rule-based eligibility result.
-   Display simulated claim records and claim-update history.
-   Allow users to organize goals and action tasks.
-   Provide preparation checklists for follow-up activities.

## Features

-   **Authentication and access control:** Registration, login, password
    encoding, and protected application routes using Spring Security.
-   **Veteran profile:** Stores profile information associated with the
    user's account.
-   **Eligibility illustration:** Applies the simplified demonstration
    rule described below.
-   **Simulated claim tracking:** Displays claim records and related
    progress updates.
-   **Goals and action planning:** Supports personal goals and action
    tasks.
-   **Preparation checklists:** Helps organize steps associated with
    follow-up activities.

## Technology stack

  Area                      Technology
  ------------------------- -------------------------------------------
  Language                  Java 17
  Application framework     Spring Boot 3.3.2
  Web framework             Spring MVC
  View layer                Thymeleaf, HTML, CSS, JavaScript
  Security                  Spring Security
  Password encoding         BCrypt
  Persistence               Spring Data JPA
  Database                  MySQL
  Build tool                Maven
  Development environment   IDE of choice with Java and Maven support

The primary user interface is a server-rendered Spring MVC and Thymeleaf
application. A limited REST controller is included for veteran data, but
a separate React or Angular frontend is not the primary interface.

## Application architecture

The application is organized into layers and packages:

-   **Configuration (`config`):** Application configuration and security
    setup.
-   **Controllers (`controller`):** Handle incoming web requests and
    select views.
-   **DTOs (`dto`):** Carry form and request data.
-   **Entities (`entity`):** Represent persisted application records.
-   **Repositories (`repository`):** Provide database access through
    Spring Data JPA.
-   **Services (`service`, `service.impl`):** Contain application
    operations and business logic.
-   **Thymeleaf templates:** Render the server-side user interface.

### Main data relationships

-   A `User` is associated with a `Veteran` profile in a one-to-one
    relationship.
-   A `Veteran` owns related `Claim`, `Goal`, and `ActionTask` records.
-   A `Claim` can have multiple `ClaimUpdate` records.
-   An `ActionTask` can optionally be associated with a `Goal`.

Personal benefit progress is represented through the `Claim` and
`ClaimUpdate` records; the application does not use a separate
`BenefitTracker` entity.

## Simplified eligibility demonstration

The prototype uses a deliberately simplified rule to demonstrate
business logic:

1.  The service period is evaluated.
2.  The discharge status is checked.
3.  The demonstration displays **Eligible** when the service duration is
    at least 24 months and the discharge status is **Honorable**.
4.  Otherwise, it displays **Not Eligible**.

This rule is not a complete representation of VA eligibility
requirements. Actual eligibility depends on the specific benefit program
and the individual's circumstances. The result shown by this application
must not be used to make real benefits decisions.

## Local setup

### Prerequisites

Install and configure:

-   Java Development Kit (JDK) 17.
-   MySQL.
-   Maven.
-   An IDE or code editor capable of working with Java and Maven.

### 1. Obtain the source code

Clone the repository:

``` bash
git clone https://github.com/Wadsworth153/Senior-Project.git
cd Senior-Project
```

The repository is private, so access requires permission from the
repository owner.

### 2. Create the database

Create a MySQL database named:

``` sql
CREATE DATABASE vabenefits;
```

### 3. Configure the database connection

Open the application's `src/main/resources/application.properties` file
and configure the MySQL connection for your local environment.

Use your own local database username and password. Do not publish
database credentials, tokens, or other secrets in the repository. If
credentials have ever been committed to source control, rotate them and
remove them from the tracked configuration and repository history as
appropriate.

The application is configured to use port **8080** unless the local
configuration has been changed.

### 4. Run the application

From the project directory, run:

``` bash
mvn spring-boot:run
```

Alternatively, import the project into an IDE as a Maven project and run
the Spring Boot application entry point.

### 5. Open the application

After the application starts, open:

``` text
http://localhost:8080
```

Use the registration page to create an account, then sign in to access
the protected application features.

> Setup details may need to be adjusted to match the local MySQL
> installation and the configuration in `application.properties`.

## Security and access control

The application uses Spring Security to protect authenticated pages and
restrict selected routes by role. Passwords are encoded using BCrypt.
Public routes include registration, login, and static resources;
protected application pages require authentication, and selected pages
require the appropriate role.

For a production deployment, additional work would be needed. In
particular, database credentials should be supplied through secure
configuration rather than committed in source files. Production use
would also require a security review, stronger operational controls, and
additional testing.

## Testing and verification

Manual application testing was completed successfully during the
project.

A historical automated test run documented 49 tests with zero failures,
errors, or skipped tests for the version tested at that time. Those
results should not be interpreted as a new test run against every later
source revision.

## Known limitations

-   The application is an educational prototype and is not an official
    VA system.
-   Eligibility logic is simplified and does not cover all VA rules or
    benefit programs.
-   Claim records and claim updates are simulated.
-   The application does not submit claims or retrieve live VA claim
    status.
-   There is no live VA.gov or VA API integration.
-   Further security hardening, accessibility evaluation, usability
    testing, and expanded automated test coverage would be appropriate
    future work.

## Future improvements

Potential areas for continued development include:

-   Moving credentials and other secrets into secure environment-based
    configuration.
-   Expanding automated unit, integration, and regression testing.
-   Conducting structured accessibility and usability evaluations.
-   Improving performance monitoring and operational reliability.
-   Investigating authorized, privacy-conscious integration with
    authoritative VA services, if appropriate.

## Project repository

**GitHub:**
[Wadsworth153/Senior-Project](https://github.com/Wadsworth153/Senior-Project)

The repository is private and intended for authorized project review.

------------------------------------------------------------------------

**CST-452 -- Senior Project II**\
**VA Resource & Action Planner**
