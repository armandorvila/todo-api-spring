# TODO API Spring Framework
Small project that implements a REST TODO API on top of Java 8, Spring Framework 5 and reactive programming. 

## Feature list

1. As a user I should list my todo items.
2. As a user I should be able to add a new item.
3. As a user I should be able to remove a new item.
4. As a user I should be able to modify a existent item.
5. As a user I should be able to set a priority on a item.
6. As a user I should be able to set a due date on a item.
7. As a user I should be able to sort my todo list by due date.
8. As a user I should be able to sort my todo list by priority.
9. As a user I should be able to set an item as completed (and list completed items).
10. As a user I should be able to assign a task to another user id.
11. As a user I should be able to list tasks assigned to a user.

## Using the API

### Running the API

The repository contains a docker compose file that runs a mongodb container, a temporary container that 
sets up some testing data, and a container running the Spring Boot application which implements the API.

```bash
$ mvn clean install
$ docker-compose up
```

### Endpoints

| API Endpoint | Method | Example |
| ------------ | -------------- | ------- |
| `/authenticate` | POST | Gets a token given an email and a password. |
| `/users/singup` | POST | Sings up a new user in the system. |
| `/users/me` | POST | Gets the details of the logged user. |
| `/tasks` | POST | Creates a new task related to the current user. |
| `/tasks` | GET | Lists all tasks that are currently managed. |
| `/tasks/{itemId}` | GET  the details of a specific item. |

The following sections will walk you through a simple example on how to use the API via cURL. If you have run the API with the provided docker compose, there will be a user already provisioned: ```some.user@gmail.com/secret```

### Registering a user

```bash
$ curl http://localhost:8080/users/singup -X POST -H "Content-Type: application/json" -H -d '{"email":"some@email.com", "password":"somepass", "firstName":"Some", "lastName": "User"}'
```

### Authenticating

```bash
$ curl http://localhost:8080/authenticate -X POST -H "Content-Type: application/json" -H -d '{"email":"some.user@gmail.com", "password":"secret"}'
```

### Listing tasks

```bash
$ curl http://localhost:8080/tasks -X GET -H "Authorization: Bearer someToken"
```

### Creating a new task

```bash
$ curl http://localhost:8080/tasks -X POST -H "Content-Type: application/json" -H "Authorization: Bearer someToken" -d '{"summary":"Some task"}'
```

## Build
[![Build Status](https://secure.travis-ci.org/armandorvila/todo-api-spring.png)](http://travis-ci.org/armandorvila/todo-api-spring)  [![codecov.io](https://codecov.io/github/armandorvila/todo-api-spring/coverage.svg)](https://codecov.io/github/armandorvila/todo-api-spring) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/62c434b415f444e48bbed29f83b57a1f)](https://www.codacy.com/app/armandorvila/todo-api-spring?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=armandorvila/todo-api-spring&amp;utm_campaign=Badge_Grade)
