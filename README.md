# Training Tracking Platform for Firefighters

A Java backend to manage firefighters (agents), their skills and driving licenses, the trainings they can register for, and their rank progression.

The project follows a **clean architecture**: business rules live in the domain and use case layers, and never depend on the database. Persistence is handled by an infrastructure layer backed by SQLite.

## Features

- **Agents**: create an agent, list all agents in a dashboard view, archive an agent.
- **Trainings**: create a training with its required skills and the skill it delivers.
- **Registrations**: register an agent to a training, cancel a registration, mark it as done or failed.
- **Rank progression**: when a training is completed, the agent's rank is recalculated from the skills they hold and their seniority in their current rank.

## Tech stack

| Tool | Version |
|---|---|
| Java | 17 |
| Maven | 3.x |
| SQLite (via `sqlite-jdbc`) | 3.53 |
| JUnit | 5 (Jupiter, with parameterized tests) |

## Project structure

```
backend/
└── src/
    ├── pom.xml
    └── src/
        ├── main/java/
        │   ├── domaine/
        │   │   ├── entity/       # Business entities and enums (Agent, Training, Skill, Rank...)
        │   │   └── repository/   # Repository interfaces (contracts used by the use cases)
        │   ├── usecase/          # One class per use case
        │   │   ├── DTO/          # Read models returned to the UI (e.g. DashboardAgentProfil)
        │   │   ├── exceptions/   # Business exceptions
        │   │   └── registration/ # Registration lifecycle use cases
        │   ├── infrastructure/   # SQL implementations of the repositories
        │   └── Main.java
        └── test/java/
            ├── data/             # Test data builders (agents, trainings, skills, registrations)
            ├── domaine/          # Domain entity tests
            ├── usecase/          # Use case tests, with in-memory fake repositories (usecase/mock)
            └── infrastructure/   # Repository tests
```

### Dependency rule

```
infrastructure  ──►  domaine  ◄──  usecase
```

- `domaine` depends on nothing. It defines the entities and the repository **interfaces**.
- `usecase` depends only on `domaine`. It never contains SQL.
- `infrastructure` implements the repository interfaces with JDBC and SQLite.

This is what allows every use case to be tested with in-memory fakes instead of a real database.

## Domain model

| Entity | Description |
|---|---|
| `Agent` | A firefighter: name, unique matricule, rank, rank start date, licenses, skills, active and differentiated flags. |
| `Training` | A training session with start and end dates, the skills required to register, and the skill it delivers. |
| `Skill` | A competency (e.g. `EQ INC`, `CE INC`). |
| `License` | Driving license: `PERMIS_B`, `PERMIS_C`. |
| `Rank` | Firefighter ranks, from `SAPEUR` to `COLONEL`. |
| `RegistrationAgentInTraining` | Links an agent to a training, with registration dates and a status. |
| `RankCondition` / `RankCalculator` | Conditions to reach a rank (skills, seniority) and the logic that computes an agent's new rank. |

## Business rules

### Agents

- The matricule must be unique and have a valid length.
- An archived agent becomes inactive.

### Registering to a training

An agent can register only if:

- they hold **all the skills** required by the training;
- the training **has not started yet**.

### Registration lifecycle

```
PENDING ──► IN_PROGRESS ──► DONE
   │             │
   ▼             ▼
 CANCEL        FAILED
```

| Action | Allowed from | Refused from |
|---|---|---|
| Cancel | `PENDING` | `IN_PROGRESS`, `DONE`, `FAILED`, `CANCEL` |
| Mark as done | `IN_PROGRESS` | `PENDING`, `DONE`, `FAILED`, `CANCEL` |
| Mark as failed | `IN_PROGRESS` | `PENDING` |

Every refused transition throws an `InvalidStatusException` with an explicit message.

### Rank progression

When a registration is marked as done, the agent's rank is recalculated with `RankCalculator` from the rank conditions stored in the database. If the agent is promoted, their rank start date is reset to the current date.

## Getting started

### Prerequisites

- JDK 17 or later
- Maven 3

### Build

From the folder containing `pom.xml`:

```bash
mvn clean compile
```

### Run

Run `Main.java` from your IDE (in VS Code, with the Extension Pack for Java, use the **Run** link above the `main` method). The application uses the SQLite database `database.db`.

## Tests

```bash
mvn test
```

To run a single test class:

```bash
mvn test -Dtest=DoneTrainingTest
```

### Testing approach

- **Use case tests** run against **in-memory fake repositories** (`usecase/mock`), which store data in a `HashMap`. They test business logic only, with no database.
- **Infrastructure tests** check that the SQL implementations actually read and write the database correctly.
- **Test data** is built by the classes in `data/`. Each method returns **brand new objects** on every call, so that a test can never alter the data used by another one.
- Status rules are covered with **parameterized tests** (`@ParameterizedTest` + `@EnumSource`), so every status is checked with the same scenario.