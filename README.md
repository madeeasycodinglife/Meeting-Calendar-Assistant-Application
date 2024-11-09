# Employee and Meeting Management API Documentation

This repository contains the implementation of an API for managing employees and scheduling meetings. It provides endpoints for creating employees, fetching employee details, booking meetings, finding free slots, and detecting scheduling conflicts.

## API Endpoints

### 1. **Employee API**

#### **Create Employee**

- **URL**: `/api/employees/create`
- **Method**: `POST`
- **Description**: Creates a new employee in the system.
- **Request Body**:

```json
{
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

- **Response**:

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

#### **Get Employee by ID**

- **URL**: `/api/employees/{id}`
- **Method**: `GET`
- **Description**: Fetches the details of an employee by their ID.
- **Response**:

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

#### **Get All Employees**

- **URL**: `/api/employees`
- **Method**: `GET`
- **Description**: Fetches a list of all employees in the system.
- **Response**:

```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com"
  },
  {
    "id": 2,
    "name": "Alice Johnson",
    "email": "alice.johnson@example.com"
  },
  {
    "id": 3,
    "name": "Bob Brown",
    "email": "bob.brown@example.com"
  }
]

```

---

### 2. **Meeting API**

#### **Book a Meeting**

- **URL**: `/api/meetings/book`
- **Method**: `POST`
- **Description**: Books a new meeting with specified participants.
- **Request Body**:

```json
{
  "adminId": 1,
  "topic": "Team Sync",
  "startTime": "2024-11-05T10:00:00",
  "endTime": "2024-11-05T10:30:00",
  "participantIds": [2, 3]
}
```

- **Response**:

```json
{
  "id": 1,
  "topic": "Team Sync",
  "startTime": "2024-11-05T10:00:00",
  "endTime": "2024-11-05T10:30:00",
  "participants": [
    {
      "id": 2,
      "name": "Alice Johnson",
      "email": "alice.johnson@example.com",
      "calendarSlots": [
        {
          "id": 1,
          "startTime": "2024-11-05T10:00:00",
          "endTime": "2024-11-05T10:30:00",
          "available": false
        }
      ]
    },
    {
      "id": 3,
      "name": "Bob Brown",
      "email": "bob.brown@example.com",
      "calendarSlots": [
        {
          "id": 2,
          "startTime": "2024-11-05T10:00:00",
          "endTime": "2024-11-05T10:30:00",
          "available": false
        }
      ]
    }
  ]
}
```

#### **Get Free Slots for Employees**

- **URL**: `/api/meetings/free-slots`
- **Method**: `GET`
- **Description**: Retrieves the available meeting slots for the given list of employees.
- **Query Parameters**:
    - `employeeIds`: List of employee IDs (e.g., `employeeIds=1&employeeIds=2&employeeIds=3`)
    - `requestedStartTime`: The start time for the slot search (e.g., `2024-11-05T09:00:00`)
    - `durationMinutes`: The duration of the meeting in minutes (e.g., `30`)

- **Response**:

```json
[
  {
    "id": 3,
    "employee": {
      "id": 1,
      "name": "John Doe",
      "email": "john.doe@example.com"
    },
    "startTime": "2024-11-05T10:00:00",
    "endTime": "2024-11-05T10:30:00",
    "available": true
  },
  {
    "id": 1,
    "employee": {
      "id": 2,
      "name": "Alice Johnson",
      "email": "alice.johnson@example.com"
    },
    "startTime": "2024-11-05T10:00:00",
    "endTime": "2024-11-05T10:30:00",
    "available": true
  },
  {
    "id": 2,
    "employee": {
      "id": 3,
      "name": "Bob Brown",
      "email": "bob.brown@example.com"
    },
    "startTime": "2024-11-05T10:00:00",
    "endTime": "2024-11-05T10:30:00",
    "available": true
  }
]
```

#### **Get Conflicted Participants for a Meeting**

- **URL**: `/api/meetings/conflicts`
- **Method**: `POST`
- **Description**: Finds the employees who have conflicting schedules for the given time slot and duration.
- **Request Body**:

```json
{
  "requestedStartTime": "2024-11-05T10:00:00",
  "durationMinutes": 30
}
```

- **Response**:

```json
{
  "conflictedEmployees": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john.doe@example.com",
      "calendarSlots": [
        {
          "id": 3,
          "startTime": "2024-11-05T10:00:00",
          "endTime": "2024-11-05T10:30:00",
          "available": false
        }
      ]
    },
    {
      "id": 2,
      "name": "Alice Johnson",
      "email": "alice.johnson@example.com",
      "calendarSlots": [
        {
          "id": 1,
          "startTime": "2024-11-05T10:00:00",
          "endTime": "2024-11-05T10:30:00",
          "available": false
        }
      ]
    },
    {
      "id": 3,
      "name": "Bob Brown",
      "email": "bob.brown@example.com",
      "calendarSlots": [
        {
          "id": 2,
          "startTime": "2024-11-05T10:00:00",
          "endTime": "2024-11-05T10:30:00",
          "available": false
        }
      ]
    }
  ]
}

```

---

## Technologies Used

- **Spring Boot** for backend development
- **Spring Data JPA** for database interaction
- **H2 Database** for in-memory database
- **Lombok** for code simplification
- **Java 21** as the runtime environment
- **Maven** for project management

## Running the Application

### Prerequisites

- Java 21 or later
- Maven

### Steps to run

1. Clone the repository:
   ```bash
   git clone <repository_url>
   ```

2. Navigate to the project folder:
   ```bash
   cd <project_folder>
   ```

3. Run the application using Maven:
   ```bash
   mvn spring-boot:run
   ```

4. The application will be available at `http://localhost:8080`.

---

## Conclusion

This API provides a simple yet powerful system to manage employees and their meetings. You can easily integrate it with other applications or extend its functionality as needed.

Thank you for using the Employee and Meeting Management API!