# Beacon Südtirol API

## Configure application.properties file
Make a copy of the application.properties.dist file and name it "application.properties".
Fill in the required values
* spring.datasource.url
* spring.datasource.username
* spring.datasource.password
* spring.jpa.properties.hibernate.dialect
* security.jqt.token.secret
* it.bz.beacon.allowedOrigins
* kontakt.io.apiKey

### Run a local server for development
Run
```
./gradlew clean run
```
to let the application run locally.

### Create a production release
Run
```
./gradlew clean build
```
to create a release ready for production. You'll find the output .jar file in the ./build/libs/ folder

### Test the application
Run
```
./gradlew clean test
```
to run the unit tests.
