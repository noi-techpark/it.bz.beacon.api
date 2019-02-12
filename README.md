# Beacon Südtirol API

The API for the Beacon Südtirol project for configuring beacons and accessing beacon data.

## Table of contents

- [Getting started](#getting-started)
- [Running tests](#running-tests)
- [Deployment](#deployment)
- [Docker environment](#docker-environment)
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

## Configure application.properties file
Make a copy of the application.properties.dist file and name it "application.properties".
Fill in the required values
* spring.datasource.url
* spring.datasource.username
* spring.datasource.password
* spring.jpa.properties.hibernate.dialect
* security.jwt.token.secret
* it.bz.beacon.allowedOrigins
* kontakt.io.apiKey
* file.upload-dir

You may also change other values in the application.properties file on your own risk.
Make sure your webserver is configured to handle file uploads for at least 10MB of size.


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

ToDo: A detailed description about how the application must be deployed.

### Build production war file
```bash
mvn clean package
```

## Information

### Support

ToDo: For support, please contact [EMAIL](mailto:EMAIL).

### Contributing

If you'd like to contribute, please follow the following instructions:

- Fork the repository.

- Checkout a topic branch from the `development` branch.

- Make sure the tests are passing.

- Create a pull request against the `development` branch.

### Documentation

ToDo: More documentation can be found at [URL](URL).

### License

ToDo: The code in this project is licensed under the [LICENSE].
See the LICENSE.md file for more information.

