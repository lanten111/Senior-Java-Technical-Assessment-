# Customer Service application

## Overview

This is customer service application, it consists of two modules namely customer and auth

The customer take care of customer operation which allow you to create customer, get customers, delete customer and update customer
the auth module handle user registration and login

## Security

The API endpoint are secured with token based security, after getting the application to run you can open
swagger and use /signup endpoint to register, simply enter any username and password. after successful registration
you can use the same details to login on /login endpoint , you will get token back 

All '/customer' endpoints are secured and the token retrieved from /login is required in order to perform any operation on /customer endpoints ts

The steps for security are

Signup with username and password
sign with those details , token gets generated for the user , and when ever you do any request on secured endpoints which are anything on /customer 
, you can use the token to access this resource

The token life is 10 minutes, after that you will need to login again to generate new token

# Tech stack

The app uses the following stacks

Springboard 3 with spring boot reactive(webflux)
Liquibase for crating schema and initial test data
Postgres for main database and for storing data
Redis for caching(cache evict not working properly)
mapstruct for mappers
Lombok for reducing boiler(issues getting this work)
Swagger for api documentation and endpoints test

# Deploying the app
in order do deploy the app follow the below steps

## Properrties files

there are two docker compose files
docker-compose and docker-compose-docker
first one is for spinning up database and redis for local use,
one for deploying the whole app to docker

there are two environment, local and docker.
docker is used for docker compose and default for local


## Build Docker Image
The application make use of docker and docker compose to deploy 

Ensure you have Docker and Docker Compose installed on your machine.

First thing when deploying the app will be to build the app image using the command below
the image tag will be "customer-service-api-image:latest"

```bash
docker build -t customer-service-api-image:latest .
````

Intellij might allow you to run it directly from here, if you see double play button on the left just press it,
else you can copy the command to terminal on application root directory

Docker will build the image. this might take a little while

The build will build the image, pull all dependency and run all unit tests.

## Spinning up containers
when the build is done you can now run docker compose with below command

```bash
docker-compose -f .docker/docker-compose-docker.yml up -d
```


this will start up the database, redis and then the application itself

The app will start up on port 8071, redis and the database will use internal port only


## Accessing the service

### swagger
Once all service are up and running you can access swagger on the following endpoint

[swagger ui url](http://localhost:8071/customer-service/v1/api/swagger-ui/index.html)

First thing would do is to register using any username or password or keep the pre-populated details

once's successfully registered, then you can login using the login endpoint and same username and password used in registration

Once's that is successful, then you can copy the token from the response and press on "Authorize" in top right, then past token in the value filed
This will add the token on all endpoints that require auth

once that done then you will be able to access all customer service endpoints. until the token expire

### Postman
Alternatively the api can tests via postman, just import the below postman collection and environment into your own postman

[Collection](.postman/Assesment.postman_collection.json)

[Environment](.postman/Assesment.postman_environment.json)


