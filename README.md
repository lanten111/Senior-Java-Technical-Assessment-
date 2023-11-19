# Customer Service application

## Overview

This is customer service application, it consists of two modules namely customer and mode

The customer take care of customer operation which allow you to create customer, get customers, delete customer and update customer
the User module handle user registration and login

## Security

The API endpoint are secured with token based security, after getting the application to run you can open
swagger and use /signup endpoint to register, simply enter any username and password. after successful registration
you can use the same details to login on /login endpoint , you will get token back and you can use it to authenticate with bearer
on the rest of /customer endpoints

The steps for security are

Signup with username and password
sign with those details
token gets generated for the user
and when ever you do any request on secured endpoints which are anything on /customer
you can use the token to access this resource

The token life is 5 minutes, after that you will need to login again to generate new token

# Tech stack

The app uses the following stacks

Springboard 3 with spring boot reactive(webflux)
Liquibase for crating schema and initial data
Postgres for main database and for storing data
Redis for caching a request that require caching
mapstruct for mappers
Lombok for reducing boiler(issues getting this work)
Swagger for api documentation(issues getting this to work)

# Deploying the app
in order do deploy the app follow the below steps

## Build Docker Image
The application make use of docker to deploy 

Ensure you have Docker and Docker Compose installed on your machine.

First thing when deploying the app will be to build the app using the command below

```bash
docker build -t customer-service-api-image:latest .
````

Intellij might allow you to run it directly from here, if you see double play button on the left just press it,
else you can copy the command to application root directory

Docker build will take a little while..

The build will build the image, pull all dependency and run unit tests, when all is successful, 
you can now run docker compose with below command

this will start up the database, redis and then the application itself

when it's done then you can access the api via swagger or use postman collection below


```bash
docker-compose -f .docker/docker-compose.yml up -d
```

To build the Docker image for this project, run the following command in your terminal:


