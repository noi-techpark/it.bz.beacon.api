# Beacon Südtirol API

## Prerequisits
* Maven

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

### Run a local server for development
Run
```
mvn clean spring-boot:run
```
to let the application run locally.

### Create a production release
Run
```
mvn clean package
```
to create a release ready for production. You'll find the output .war file in the ./target/ folder

### Test the application
Run
```
mvn clean test
```
to run the unit tests.
