pipeline {
    agent {
        dockerfile {
            filename 'docker/dockerfile-java'
            additionalBuildArgs '--build-arg JENKINS_USER_ID=`id -u jenkins` --build-arg JENKINS_GROUP_ID=`id -g jenkins`'
        }
    }

    environment {
        TESTSERVER_TOMCAT_ENDPOINT = "http://alpinebits-server.tomcat02.testingmachine.eu:8080/manager/text"
        TESTSERVER_TOMCAT_CREDENTIALS = credentials('testserver-tomcat8-credentials')

        GOOGLE_SERVICE_ACCOUNT = credentials('beacon-api-google-service-account')

        DB_URL = "jdbc:postgresql://test-pg-bdp.co90ybcr8iim.eu-west-1.rds.amazonaws.com:5432/beacon"
        DB_USERNAME = credentials('beacon-api-test-db-username')
        DB_PASSWORD = credentials('beacon-api-test-db-password')
        DB_DIALECT = "org.hibernate.dialect.PostgreSQLDialect"
        JWT_SECRET = credentials('beacon-api-test-jwt-secret')
        JWT_EXPIRE_LENGTH = "3600000"
        UPLOAD_DIR = "/var/data/beacon-api"
        CORS_ORIGINS = "*"
        KONTAKT_IO_API_KEY = credentials('beacon-api-test-kontakt-io-api-key')

        INFO_HOST = "api.beacon.testingmachine.eu"
        INFO_TITLE = "Beacon Suedtirol API"
        INFO_DESCRIPTION = "The API for the Beacon Suedtirol project for configuring beacons and accessing beacon data."
        INFO_VERSION = "0.1"
        INFO_TERMS_OF_SERVICE_URL = "https://opendatahub.readthedocs.io/en/latest/licenses.html"
        INFO_CONTACT_NAME = ""
        INFO_CONTACT_URL = ""
        INFO_CONTACT_EMAIL = ""
        INFO_LICENSE = ""
        INFO_LICENSE_URL = ""

        ISSUE_EMAIL_FROM = "info@beacon.bz.it"
        ISSUE_EMAIL_TO = "info@beacon.bz.it"

        MAIL_HOST = "email-smtp.eu-west-1.amazonaws.com"
        MAIL_PORT = "587"
        MAIL_USERNAME = credentials('beacon-api-test-mail-username')
        MAIL_PASSWORD = credentials('beacon-api-test-mail-password')
        MAIL_SMTP_AUTH = "true"
        MAIL_SMTP_STARTTLS = "true"

        BEACON_UUID = "6a84c716-0f2a-1ce9-f210-6a63bd873dd9"
        BEACON_NAMESPACE = "6a84c7166a63bd873dd9"
        BEACON_TASK_IMPORT_ENABLED = "true"
        BEACON_TASK_IMPORT_SPREADSHEET = "13ddjs6puWyAPTqaVVo2QKQ0-E6-EQBwfIkYyv1jxN2Q"
        BEACON_TASK_IMPORT_DELAY = "21600000"
        BEACON_TRUSTED_USERNAME = credentials('beacon-api-test-trusted-username')
        BEACON_TRUSTED_PASSWORD = credentials('beacon-api-test-trusted-password')
    }

    stages { 
        stage('Configure') {
            steps {
                sh 'sed -i -e "s/<\\/settings>$//g\" ~/.m2/settings.xml'
                sh 'echo "    <servers>" >> ~/.m2/settings.xml'
                sh 'echo "        ${TESTSERVER_TOMCAT_CREDENTIALS}" >> ~/.m2/settings.xml'
                sh 'echo "    </servers>" >> ~/.m2/settings.xml'
                sh 'echo "</settings>" >> ~/.m2/settings.xml'

                sh 'cp src/main/resources/application.properties.dist src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(spring.datasource.url\\s*=\\).*\\$%\\1${DB_URL}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(spring.datasource.username\\s*=\\).*\\$%\\1${DB_USERNAME}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(spring.datasource.password\\s*=\\).*\\$%\\1${DB_PASSWORD}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(spring.jpa.properties.hibernate.dialect\\s*=\\).*\\$%\\1${DB_DIALECT}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(security.jwt.token.secret\\s*=\\).*\\$%\\1${JWT_SECRET}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(security.jwt.token.expire-length\\s*=\\).*\\$%\\1${JWT_EXPIRE_LENGTH}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(file.upload-dir\\s*=\\).*\\$%\\1${UPLOAD_DIR}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(it.bz.beacon.allowedOrigins\\s*=\\).*\\$%\\1${CORS_ORIGINS}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(kontakt.io.apiKey\\s*=\\).*\\$%\\1${KONTAKT_IO_API_KEY}%" src/main/resources/application.properties'

                sh 'sed -i -e "s%\\(api.info.host\\s*=\\).*\\$%\\1${INFO_HOST}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(api.info.title\\s*=\\).*\\$%\\1${INFO_TITLE}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(api.info.description\\s*=\\).*\\$%\\1${INFO_DESCRIPTION}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(api.info.version\\s*=\\).*\\$%\\1${INFO_VERSION}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(api.info.termsOfServiceUrl\\s*=\\).*\\$%\\1${INFO_TERMS_OF_SERVICE_URL}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(api.info.contactName\\s*=\\).*\\$%\\1${INFO_CONTACT_NAME}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(api.info.contactUrl\\s*=\\).*\\$%\\1${INFO_CONTACT_URL}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(api.info.contactEmail\\s*=\\).*\\$%\\1${INFO_CONTACT_EMAIL}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(api.info.license\\s*=\\).*\\$%\\1${INFO_LICENSE}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(api.info.licenseUrl\\s*=\\).*\\$%\\1${INFO_LICENSE_URL}%" src/main/resources/application.properties'

                sh 'sed -i -e "s%\\(it.bz.beacon.issueEmailFrom\\s*=\\).*\\$%\\1${ISSUE_EMAIL_FROM}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(it.bz.beacon.issueEmailTo\\s*=\\).*\\$%\\1${ISSUE_EMAIL_TO}%" src/main/resources/application.properties'

                sh 'sed -i -e "s%\\(spring.mail.host\\s*=\\).*\\$%\\1${MAIL_HOST}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(spring.mail.port\\s*=\\).*\\$%\\1${MAIL_PORT}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(spring.mail.username\\s*=\\).*\\$%\\1${MAIL_USERNAME}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(spring.mail.password\\s*=\\).*\\$%\\1${MAIL_PASSWORD}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(spring.mail.properties.mail.smtp.auth\\s*=\\).*\\$%\\1${MAIL_SMTP_AUTH}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(spring.mail.properties.mail.smtp.starttls.enable\\s*=\\).*\\$%\\1${MAIL_SMTP_STARTTLS}%" src/main/resources/application.properties'

                sh 'sed -i -e "s%\\(it.bz.beacon.uuid\\s*=\\).*\\$%\\1${BEACON_UUID}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(it.bz.beacon.namespace\\s*=\\).*\\$%\\1${BEACON_NAMESPACE}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(it.bz.beacon.task.infoimport.enabled\\s*=\\).*\\$%\\1${BEACON_TASK_IMPORT_ENABLED}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(it.bz.beacon.task.infoimport.spreadSheetId\\s*=\\).*\\$%\\1${BEACON_TASK_IMPORT_SPREADSHEET}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(it.bz.beacon.task.infoimport.delay\\s*=\\).*\\$%\\1${BEACON_TASK_IMPORT_DELAY}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(it.bz.beacon.trusted.user\\s*=\\).*\\$%\\1${BEACON_TRUSTED_USERNAME}%" src/main/resources/application.properties'
                sh 'sed -i -e "s%\\(it.bz.beacon.trusted.password\\s*=\\).*\\$%\\1${BEACON_TRUSTED_PASSWORD}%" src/main/resources/application.properties'

                sh 'cat "${GOOGLE_SERVICE_ACCOUNT}" > src/main/resources/google-api-service-account.json'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn -B -U clean test verify'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn -B -U clean package'
            }
        }
        stage('Deploy') {
            steps{
                sh 'mvn -B -U tomcat:redeploy -Dmaven.tomcat.url=${TESTSERVER_TOMCAT_ENDPOINT} -Dmaven.tomcat.server=testServer'
            }
        }
    }
}
