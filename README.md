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
* spring.datasource.username
* spring.datasource.password
* spring.jpa.properties.hibernate.dialect
* security.jwt.token.secret
* it.bz.beacon.allowedOrigins
* kontakt.io.apiKey
* file.upload-dir
* api.info.*
* it.bz.beacon.issueEmailTo
* it.bz.beacon.issueEmailFrom
* spring.mail.*
* it.bz.beacon.uuid
* it.bz.beacon.namespace
* it.bz.beacon.task.infoimport.enabled
* it.bz.beacon.task.infoimport.spreadSheetId

If you enable infor import, you have to create a Google service account which is able to use the Google Sheet API and move the resulting client-secret.json file to /src/main/resources/google-api-service-account.json.
For more information on generating this json file, have a look at the Google documentation for [Using OAuth 2.0 for Server to Server Applications](https://developers.google.com/identity/protocols/OAuth2ServiceAccount) 

You may also change other values in the application.properties file on your own risk.
Make sure your webserver is configured to handle file uploads for at least 10MB of size.

### Database

The schema of the database will be automatically generated when starting the application based on the SQL files located in `src/main/resources/db/migration`.

Also an admin user will be inserted with username "admin" and password "password".

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

