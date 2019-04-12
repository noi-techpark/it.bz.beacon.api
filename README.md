# Beacon Südtirol API

The API for the Beacon Südtirol project for configuring beacons and accessing beacon data.

## Table of contents

- [Getting started](#getting-started)
- [Running tests](#running-tests)
- [Deployment](#deployment)
- [Information](#information)

## Getting started

These instructions will get you a copy of the project up and running
on your local machine for development and testing purposes.

### Prerequisites

To build the project, the following prerequisites must be met:

- Java JDK 1.8 or higher (e.g. [OpenJDK](https://openjdk.java.net/))
- [Maven](https://maven.apache.org/) 3.x
- Database (e.g. [PostgreSQL](https://www.postgresql.org))
- Filesystem

### Source code

Get a copy of the repository:

```bash
git clone https://github.com/idm-suedtirol/beacon-suedtirol-api.git
```

Change directory:

```bash
cd beacon-suedtirol-api/
```

### Configuration

Make a copy of the src/resources/application.properties.dist file and name it "application.properties". Fill in the required values:

* spring.datasource.url  
*Database connection url*


* spring.datasource.username  
*Database connection username*


* spring.datasource.password  
*Database connection password*


* spring.jpa.properties.hibernate.dialect  
*Database dialect*
  

* security.jwt.token.secret  
*JWT token secret hash*


* it.bz.beacon.allowedOrigins  
*Comma separated list of allowed origins for CORS requests*


* kontakt.io.apiKey  
*API key for the manufacturer Kontakt.IO*


* file.upload-dir  
*The directory where file uploads shall be saved to*


* api.info.*  
*API information values*


* it.bz.beacon.issueEmailTo  
*The address where notifications about new beacon issues should be send to*


* it.bz.beacon.issueEmailFrom  
*The address where notifications about new beacon issues should be send from*


* spring.mail.*  
*Mail host configuration, so that notification mails can be send by the server*


* it.bz.beacon.uuid  
*iBeacon UUID*


* it.bz.beacon.namespace  
*Eddystone namespace*


* it.bz.beacon.task.infoimport.enabled  
*Defines whether the info replication through Google spread sheet should be enabled or not*


* it.bz.beacon.task.infoimport.spreadSheetId  
*Google spread sheet id to import*


* it.bz.beacon.task.infoimport.delay
*Replication delay in milliseconds after last run*  


* it.bz.beacon.trusted.user  
*The username for the basic authorization for the trusted api*


* it.bz.beacon.trusted.password  
*The bcrypted password for the basic authorization for the trusted api*



If you enable info import, you have to create a Google service account which is able to use the Google Sheet API and move the resulting client-secret.json file to /src/main/resources/google-api-service-account.json.
For more information on generating this json file, have a look at the Google documentation for [Using OAuth 2.0 for Server to Server Applications](https://developers.google.com/identity/protocols/OAuth2ServiceAccount) 

You may also change other values in the application.properties file on your own risk.
Make sure your webserver is configured to handle file uploads for at least 10MB of size.

### Database

The schema of the database will be automatically generated when starting the application based on the SQL files located in `src/main/resources/db/migration`.

Also an admin user will be inserted with username "admin" and password "password". The admin will be the only user be able to create and delete users and reset their passwords.

### Build

Build the project:

```bash
mvn clean spring-boot:run
```

## Running tests

The unit tests can be executed with the following command:

```bash
mvn clean test
```

## Deployment

To build the application for production, execute the following command:

```bash
mvn clean package
```

## Docker environment

For the project a Docker environment is already prepared and ready to use with all necessary prerequisites.

These Docker containers are the same as used by the continuous integration servers.

### Installation

Install [Docker](https://docs.docker.com/install/) (with Docker Compose) locally on your machine.

### Start and stop the containers

Before start working you have to start the Docker containers:

```
docker-compose up --build --detach
```

After finished working you can stop the Docker containers:

```
docker-compose stop
```

### Running commands inside the container

When the containers are running, you can execute any command inside the environment. Just replace the dots `...` in the following example with the command you wish to execute:

```bash
docker-compose exec java /bin/bash -c "..."
```

Some examples are:

```bash
docker-compose exec java /bin/bash -c "mvn clean test"

# or

docker-compose exec java /bin/bash -c "mvn clean spring-boot:run"
```

While running the last command, you can access the website at http://localhost:8080.

## Information

### Using the API

The authentication layer of the API is divided in the following 3 parts:

1. No auth - /v1/info/**, /v1/signin, /v1/checkToken
2. JWT token - /v1/admin/**
3. Basic auth - /v1/trusted/**

As a matter of fact, using the API, you have to choose which type of authentication has to be attached to the request (one of the 3 options above).

#### Open endpoint calls
If you desire to access to an open API, no authentication has to be passed with the request.

#### JWT token protected endpoint calls
1. Make a request to /v1/signin using your credentials of the web application or android app
2. If your credentials were correct, a JWT token will be present in the response
3. In SwaggerUI, click on the "Authorize" button on the top of the page and insert "Bearer [token]" in the JWT token field by replacing [token] with the acutal token received in the response
4. Click on "Authorize" in the JWT token section
5. The padlocks on the right side of the JWT token protected API endpoints will turn black and closed
6. You are now able to call JWT token protected APIs

#### Basic auth protected endpoint calls
1. In SwaggerUI, click on the "Authorize" button on the top of the page and insert the credentials of the page in the username and password fields of the Basic auth section. These credentials were configured in your application.properties file.
2. Click on "Authorize" in the Basic auth section
3. The padlocks on the right side of the Basic auth protected API endpoints will turn black and closed
4. You are now able to call Basic auth protected APIs

CAUTION!  
In case you set a wrong authorization header either for the JWT token or the Basic auth, some API endpoints may not work properly.

### Support

For support, please contact [info@beacon.bz.it](mailto:info@beacon.bz.it).

### Contributing

If you'd like to contribute, please follow the following instructions:

- Fork the repository.

- Checkout a topic branch from the `development` branch.

- Make sure the tests are passing.

- Create a pull request against the `development` branch.

### Documentation

More documentation can be found at [https://opendatahub.readthedocs.io/en/latest/index.html](https://opendatahub.readthedocs.io/en/latest/index.html).

### License

The code in this project is licensed under the Mozilla Public License 2.0 license.
See the LICENSE.md file for more information.

