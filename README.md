<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

# it.bz.beacon.api: Beacon Südtirol API
The API for the Beacon Südtirol project for configuring beacons and accessing beacon data.

[![REUSE Compliance](https://github.com/noi-techpark/it.bz.beacon.api/actions/workflows/reuse.yml/badge.svg)](https://github.com/noi-techpark/odh-docs/wiki/REUSE#badges)
[![CI](https://github.com/noi-techpark/it.bz.beacon.api/actions/workflows/ci.yml/badge.svg)](https://github.com/noi-techpark/it.bz.beacon.api/actions/workflows/ci.yml)

## Table of contents

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Getting started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Source code](#source-code)
  - [Configuration](#configuration)
  - [Database](#database)
  - [Build](#build)
- [Running tests](#running-tests)
- [Deployment](#deployment)
- [Docker environment](#docker-environment)
  - [Installation](#installation)
  - [Start and stop the containers](#start-and-stop-the-containers)
  - [Running commands inside the container](#running-commands-inside-the-container)
- [Information](#information)
  - [User management](#user-management)
  - [Using the API](#using-the-api)
    - [Open endpoint calls](#open-endpoint-calls)
    - [JWT token protected endpoint calls](#jwt-token-protected-endpoint-calls)
    - [Basic auth protected endpoint calls](#basic-auth-protected-endpoint-calls)
    - [Google spread sheet import of POI data](#google-spread-sheet-import-of-poi-data)
  - [Support](#support)
  - [Contributing](#contributing)
  - [Documentation](#documentation)
  - [License](#license)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Getting started

These instructions will get you a copy of the project up and running
on your local machine for development and testing purposes.

### Prerequisites

To build the project, the following prerequisites must be met:

- Java JDK 1.8 or higher (e.g. [OpenJDK](https://openjdk.java.net/))
- [Maven](https://maven.apache.org/) 3.x
- Database (ideally, [PostgreSQL](https://www.postgresql.org))
- Filesystem

### Source code

Get a copy of the repository:

```bash
git clone https://github.com/idm-suedtirol/it.bz.beacon.api.git
```

Change directory:

```bash
cd it.bz.beacon.api/
```

### Configuration

1) Go to `src/resources/` and make a copy of `application.dist.properties`.
   Name it `application.properties`. Fill in the required values. See comments
   inside that file for further information. 
2) Make sure your webserver is configured to handle file uploads for at least
   10MB of size.

### Database

The schema of the database will be automatically generated when starting the
application based on the SQL files located in `src/main/resources/db/migration`.

Also an admin user will be inserted with username "admin" and password
"password". The admin will be the only user be able to create and delete users
and reset their passwords initially, but then you can create arbitrary users and
groups. Multiple administrators are possible.

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

### User management
We have only a single user, namely `admin`, that can manage new users. Just login with that user and go to the user list to create, update or delete user information.

### Using the API

The authentication layer of the API is divided in the following 3 parts:

1. No auth - /v1/info/**, /v1/signin, /v1/checkToken
2. JWT token - /v1/admin/**
3. Basic auth - /v1/trusted/**

As a matter of fact, using the API, you have to choose which type of authentication has to be attached to the request (one of the 3 options above).

#### Open endpoint calls
If you desire to access to an open API, no authentication has to be passed with the request.

#### JWT token protected endpoint calls
1. Make a request to /v1/signin using your credentials of the web application or
   android app
2. If your credentials were correct, a JWT token will be present in the response
3. In SwaggerUI, click on the "Authorize" button on the top of the page and
   insert "Bearer [token]" in the JWT token field by replacing [token] with the
   acutal token received in the response
4. Click on "Authorize" in the JWT token section
5. The padlocks on the right side of the JWT token protected API endpoints will
   turn black and closed
6. You are now able to call JWT token protected APIs

#### Basic auth protected endpoint calls
1. In SwaggerUI, click on the "Authorize" button on the top of the page and
   insert the credentials of the page in the username and password fields of the
   Basic auth section. These credentials were configured in your
   application.properties file.
2. Click on "Authorize" in the Basic auth section
3. The padlocks on the right side of the Basic auth protected API endpoints will
   turn black and closed
4. You are now able to call Basic auth protected APIs

**CAUTION!** In case you set a wrong authorization header either for the JWT token
or the Basic auth, some API endpoints may not work properly.

#### Google spread sheet import of POI data

**The Google spread sheet import is deprecated as of 2021:** <strike>If you
enable info import, you have to create a Google service account which is able to
use the Google Sheet API and move the resulting client-secret.json file to
/src/main/resources/google-api-service-account.json. For more information on
generating this json file, have a look at the Google documentation for [Using
OAuth 2.0 for Server to Server
Applications](https://developers.google.com/identity/protocols/OAuth2ServiceAccount)</strike>


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

The code in this project is licensed under the GNU AFFERO GENERAL PUBLIC LICENSE 3.0 or later license.
See the LICENSE.md file for more information.

### REUSE

This project is [REUSE](https://reuse.software) compliant, more information about the usage of REUSE in NOI Techpark repositories can be found [here](https://github.com/noi-techpark/odh-docs/wiki/Guidelines-for-developers-and-licenses#guidelines-for-contributors-and-new-developers).

Since the CI for this project checks for REUSE compliance you might find it useful to use a pre-commit hook checking for REUSE compliance locally. The [pre-commit-config](.pre-commit-config.yaml) file in the repository root is already configured to check for REUSE compliance with help of the [pre-commit](https://pre-commit.com) tool.

Install the tool by running:
```bash
pip install pre-commit
```
Then install the pre-commit hook via the config file by running:
```bash
pre-commit install
```

