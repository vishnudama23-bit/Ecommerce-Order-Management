README.md


E-Commerce Order Management System
A backend REST API developed using Java and Spring Boot for managing an e-commerce application.

Technologies Used
Java 21

Spring Boot 4.1.1

Spring Data JPA

Hibernate

MySQL

Spring Security

JWT Authentication

Spring Validation

MapStruct

Lombok

Spring AOP

Spring Mail

Spring Boot Actuator

Swagger / OpenAPI

Maven

Eclipse / Spring Tool Suite

Postman

Architecture
Client
   ↓
Controller
   ↓
Request DTO
   ↓
Service
   ↓
Repository
   ↓
Entity / JPA
   ↓
MySQL
Project Structure
src/main/java/com/example/ecommerce
├── controller
├── service
│   └── impl
├── repository
├── entity
├── dto
│   ├── request
│   └── response
├── mapper
├── exception
├── config
├── security
├── scheduler
└── enums
Features
Authentication and Authorization
User registration and login

BCrypt password encryption

JWT authentication

USER and ADMIN roles

Role-based authorization

Product Management
Product CRUD

Product image URL update

Product image upload

CSV product upload

Stock management

Product search

Pagination

Sorting

Advanced product filtering

Product Search
GET /products/search?name=Samsung&page=0&size=10
Supports partial and case-insensitive name search, pagination, and sorting.

Advanced Product Filtering
Supports:

Product name

Category

Minimum price

Maximum price

Stock availability

Pagination

Sorting

Multiple filters together

Example:

GET /products/filter?name=Samsung&categoryId=1&minPrice=50000&maxPrice=80000&inStock=true&page=0&size=10
Dynamic filtering is implemented using Spring Data JPA Specifications.

Category Management
Create, update, view, and delete categories

Prevent deletion when products are associated with a category

Customer Management
Customer profile management

Customer ownership validation

Duplicate email validation

Duplicate phone validation

Cart Management
Add product to cart

Add multiple products

Add products from CSV

Update quantity

Remove items

Clear cart

Stock validation

Customer ownership validation

Order Management
Create and view orders

Customer order history

Search orders by status

Update order status

Cancel orders

Order total calculation

Stock management

Order statuses:

PLACED
PAID
CONFIRMED
SHIPPED
DELIVERED
CANCELLED
Payments
Create and view payments

Payment validation

Payment status handling

Automatically update order status to PAID

Payment confirmation email

Wishlist
Add products to wishlist

View wishlist

Remove products

Clear wishlist

Prevent duplicate wishlist items

Customer ownership validation

Reviews
Add product reviews

View reviews by product

View reviews by customer

Delete reviews

Review summary

Average rating

Star distribution

Email Notifications
Spring Mail is used for:

Order confirmation

Successful payment

Low-stock notifications

Auditing and Versioning
Spring Data JPA auditing tracks:

createdDate
updatedDate
JPA @Version is used for optimistic locking.

Exception Handling
Global exception handling is implemented using @ControllerAdvice.

The application handles product, category, customer, order, order item, payment, validation, stock, and duplicate-data exceptions.

Logging
Spring AOP provides controller-level logging for:

HTTP method

Request URI

Controller method

Execution time

Swagger / OpenAPI
Swagger UI:

http://localhost:8080/swagger-ui.html
OpenAPI:

http://localhost:8080/v3/api-docs
JWT Bearer authentication is configured in Swagger.

Actuator
Health:

http://localhost:8080/actuator/health
Configured endpoints:

health
info
metrics
Security Flow
Register
   ↓
Login
   ↓
Username + Password
   ↓
Authentication
   ↓
JWT Token
   ↓
Authorization Header
   ↓
JWT Authentication Filter
   ↓
Protected API
Example:

Authorization: Bearer <JWT_TOKEN>
Database
MySQL database:

ecommerce_db
Example configuration:

spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.jpa.hibernate.ddl-auto=update
Do not commit real passwords, tokens, API keys, or other secrets to GitHub.

Running the Project
Prerequisites
Java 21 or compatible Java version

MySQL

Maven

Eclipse / Spring Tool Suite

Clone
git clone https://github.com/vishnudama23-bit/Ecommerce-Order-Management.git
Create Database
CREATE DATABASE ecommerce_db;
Configure
Update:

src/main/resources/application.properties
with your local database and email configuration.

Run
mvn spring-boot:run
Or run EcommerceOrderManagementApplication.java from Eclipse / STS.

Application:

http://localhost:8080
API Modules
/auth
/products
/categories
/customers
/carts
/orders
/order-items
/payments
/reviews
/wishlists
/admin
Testing
APIs were tested using:

Swagger UI

Postman

MySQL database verification

Testing covers CRUD operations, validation, authentication, authorization, pagination, sorting, search, filtering, stock management, orders, payments, emails, wishlist, reviews, and exception handling.

Future Enhancements
Coupon and discount management

Address management

Order tracking history

Return and refund management

Invoice generation

Sales reports

Recently viewed products

Notification management

Author
Vishnu Dama

Java Developer | Spring Boot | REST API | MySQL

License
This project is developed for learning, portfolio, and demonstration purposes.
