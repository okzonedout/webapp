# CSYE6225 Assignment 1: Health check API(local setup)

## Overview

This repository contains a Spring Boot application that utilizes Hibernate as the Object-Relational Mapping (ORM) tool to connect to a PostgreSQL database. The application features a single API endpoint, "/healthz," with specific behaviors for different scenarios.
 
## API Endpoint

### `/healthz`

- **Method:** `GET`
    - **Description** 
      - Accepts only when there is no payload or query params (`200 OK`)
      - Rejects when payload is present (`400 Bad Request`)
- **Other HTTP Methods**
    - **Description**
      - All other HTTP methods are rejected (`405 Method Not Allowed`)
- **Endpoint Unavailability**
  - If the endpoint is not available, a `404 Not Found` HTTP status code is returned.

### Database Management

- **Local Database Instance**
    - The database is currently local to the system.
    - To stop the local instance of the database (Windows), manual intervention is required by accessing Windows services.

### Handling Database Status

- **Database Availability**
    - If the database is down, the "/healthz" endpoint returns HTTP status code `503` SERVICE UNAVAILABLE.

## Testing

To test the API, [Postman](https://www.postman.com/) can be used for making requests to the defined endpoints.
The file "postman.json" in the project can be imported in postman

## Getting Started

1. Import the project in IntelliJ IDEA
2. Run the project
3. Access the API at `http://localhost:8080/healthz`.
