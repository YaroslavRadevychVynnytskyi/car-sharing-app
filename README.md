# CAR SHARING API
![](https://raw.githubusercontent.com/YaroslavRadevychVynnytskyi/car-sharing-app/master/readme-media/car-sharing-photo.avif)
___
## Introduction
Welcome to the Car Sharing API. This project is a Java-based RESTful
API designed to streamline car sharing services. This API provides
a solid foundation for creating a comprehensive car sharing solution. 
___
## Challenges
There were several challenges during the creation of the application.
### Integration Telegram API for notifications & Stripe API for payments
Since it was my first time interacting with Telegram API as well as with Stripe API 
it was a bit complicated at first to understand how to work with these tools correctly.
Despite this, successfully integrating the Stripe API for payment services 
and the Telegram API for notifications has enhanced the functionality and
usability of the Car Sharing API, providing users with a seamless and
secure experience.
___
## API Architecture
The Car Sharing API is build upon a robust and scalable MVC architecture
designed to provide a seamless and efficient experience for users interacting
with a platform. The application is divided into a number of layers, each with
specific responsibility:
* **Controller layer** is responsible for handling incoming ***HTTP*** requests
and returning responses to client.
* **Service layer** is in charge of implementation of the business logic of the API. 
* **Repository layer** is used for interaction with data sources.
* **Domain model layer** needed for defining domain entities used by the API.
___
## API Features Overview
1. **Authentication management endpoints:**
    * Available for everybody:\
      ```POST: /auth/register``` - registers a new user.\
      ```POST: /auth/login``` - sign in for existing user.
2. **Car management endpoints:**
    * Administrator available:\
      ```POST: /cars``` - adds a new car.\
      ```PATCH: /cars/{id} ``` - updates a car.\
      ```DELETE: /cars/{id} ``` - removes a car.
   * Available for everyone:\
      ```GET: /cars```      - retrieves all cars.\
      ```GET: /cars/{id}``` - retrieves specific car detailed info.
3. **User management endpoints:**
    * Administrator available:\
      ```PUT: /users/{id}/role``` - updates user's role.
    * User available:\
      ```GET: /users/me``` - retrieves user's profile info.\
      ```PATCH: /users/me``` - updates user's profile info.
4. **Rental management endpoints:**
    * Administrator available:\
      ```GET: /rentals/?user_id=...&is_active=...``` - retrieves specific user rentals by user ID and whether the rental is still active or not.\
      ```GET: /rentals/{id}``` - retrieves specific rental.
    * User available:\
      ```POST: /rentals``` - places a new rental.\
      ```GET: /rentals/``` - retrieves user's specific rental.\
      ```POST: /rentals/return``` - sets actual return date for specific rental.
5. **Payment management endpoints:**
   * Administrator available:\
     ```GET: /payments?userId=...``` - provides list of specific user payments.
   * Available for everyone:\
     ```POST: /payments/create``` - creates a new payment session for specific rental.\
     ```GET: /payments``` - provides list of user's payments.\
     ```GET: /payments/success``` - endpoint for Stripe redirection when payment is successful.\
     ```GET: /payments/cancel``` - endpoint for Stripe redirection when payment is cancelled.

[Here](https://www.postman.com/yaroslavradevych/workspace/car-sharing-app) you can 
find public Postman collection with full URLs which you can use for
testing.
___
## Setting Up the Application
This chapter contains information on how to set up the ***Car Sharing API***.

There are two ways of running the app:
1. Natively (JRE).
2. In Docker container.

### First way: 
#### Requirements:
* Java Development Kit (JDK) version 17.0.10 or higher
* Apache Maven version 3.8.7 or higher
* MySQL Server 8 or MariaDB 11.2.3 if you are a Linux user
#### Instruction:
1. Clone this repository to your computer ```git clone git@github.com:YaroslavRadevychVynnytskyi/car-sharing-app.git```
2. Move to ***car-sharing-app*** directory
3. Configure database connection in application.properties 
4. Configure your Telegram and Stripe properties
5. Build the project using ```mvn clean package```
6. Run the project by ```java -jar car-sharing-app-0.0.1-SNAPSHOT.jar``` command

### Second way:
#### Requirements:
* Docker Desktop
#### Instruction:
1. Clone this repository to your computer ```git clone git@github.com:YaroslavRadevychVynnytskyi/car-sharing-app.git```
2. Move to ***car-sharing-app*** directory
3. Create and configure your .env file. See .env.template.  
4. Run ```docker-compose build``` command to build required images
5. Run ```docker-compose up``` to start an application.
6. To finish execution of program you may press Ctrl + C or type in ```docker-compose down```
in a new terminal tab.
___
# API work display
In this section you will see some basic use cases of the application.
### 1. Registration use case:
   ![](https://raw.githubusercontent.com/YaroslavRadevychVynnytskyi/car-sharing-app/master/readme-media/register.gif)
### 2. Login use case:
   ![](https://raw.githubusercontent.com/YaroslavRadevychVynnytskyi/car-sharing-app/master/readme-media/login.gif)
### 3. Adding a new rental use case:
   ![](https://raw.githubusercontent.com/YaroslavRadevychVynnytskyi/car-sharing-app/master/readme-media/add-new-rental.gif)
### 4. Setting an actual return date use case:
   ![](https://raw.githubusercontent.com/YaroslavRadevychVynnytskyi/car-sharing-app/master/readme-media/set-actual-return-date.gif)
### 5. Creating of a new payment session use case:
   ![](https://raw.githubusercontent.com/YaroslavRadevychVynnytskyi/car-sharing-app/master/readme-media/create-new-payment-session.gif)
### 6. Making payment:
   ![](https://raw.githubusercontent.com/YaroslavRadevychVynnytskyi/car-sharing-app/master/readme-media/stripe.gif)
### 7. Telegram notifications overview:
   ![](https://raw.githubusercontent.com/YaroslavRadevychVynnytskyi/car-sharing-app/master/readme-media/telegram-notifications.jpg)
___
## Technologies and libraries used in the project
1. ### Java
   * **Java** is hugely popular programming language that is used widely for building enterprise-level
     applications. It gained recognition for its robustness, scalability and security.
2. ### Spring Boot
   * **Spring Boot** is a wide-spread framework for building Java applications. It provides a set of tools
     and libraries that simplify the development process and make it easier to build reliable and
     flexible software. Spring Boot is used to build **Online Book Store API** supplying features
     such as Dependency Injection, security, data access and web development.
3. ### MySQL
   * **MySQL** is a solid open-source relational database management system that is widely used for
     web applications. This application uses it for storing data about users, books, and other related
     information.
4. ### Maven
   * **Maven** is a build automation tool used for Java projects. It serves as a standard way to manage
     project dependencies, project builder, and test runner.
5. ### Jackson
   * **Jackson** is a library for processing JSON in Java. It provides features for parsing JSON,
     generating JSON, and mapping JSON to Java objects.
6. ### Mapstruct
   * **Mapstruct** is a code generator library that automates the mapping between Java objects. It
     provides a way to define the mappings between objects using annotations and generates
     the mapping code automatically during the build phase of project. In this project Mapstruct
     is used for mapping between domain models and data transfer objects.
7. ### Liquibase
   * **Liquibase** is a database migration tool that allows to manage database schema changes.
     Same as **Mapstruct** it does its work during the building stage of the application.
8. ### Swagger
   * **Swagger** is an open-source tool for creating API documentation.
9. ### Telegram API
   * **Telegram API** is a set of tools and protocols provided by Telegram Messenger LLP 
      that allows developers to integrate Telegram's messaging services into their own 
      applications or services.
10. ### Stripe API
    * **The Stripe API** is a set of tools, protocols, and endpoints provided 
          by Stripe, Inc. that enable developers to integrate payment processing 
          functionalities into their applications or websites. 
