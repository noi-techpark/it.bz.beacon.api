pipeline {
    agent {
        dockerfile {
            filename 'infrastructure/docker/java.dockerfile'
            additionalBuildArgs '--build-arg JENKINS_USER_ID=$(id -u jenkins) --build-arg JENKINS_GROUP_ID=$(id -g jenkins)'
        }
    }

    environment {
        DB_URL = "jdbc:postgresql://postgres-prod.co90ybcr8iim.eu-west-1.rds.amazonaws.com:5432/beacon"
        DB_USERNAME = credentials('beacon-api-db-username')
        DB_PASSWORD = credentials('beacon-api-db-password')
        JWT_SECRET = credentials('beacon-api-jwt-secret')
        JWT_EXPIRE_LENGTH = "3600000"
        UPLOAD_DIR = "/data/beacon-api"
        CORS_ORIGINS = "*"

        INFO_HOST = "api.beacon.bz.it"
        INFO_TITLE = "Beacon Suedtirol API"
        INFO_DESCRIPTION = "The API for the Beacon Suedtirol project for configuring beacons and accessing beacon data."
        INFO_VERSION = "0.1"
        INFO_TERMS_OF_SERVICE_URL = "https://opendatahub.readthedocs.io/en/latest/licenses.html"
        INFO_CONTACT_NAME = "NOI S.p.A."
        INFO_CONTACT_URL = "https://beacon.bz.it/"
        INFO_CONTACT_EMAIL = "info@bacon.bz.it"
        INFO_LICENSE = "AGPL-3.0-or-later"
        INFO_LICENSE_URL = "https://github.com/noi-techpark/beacon-suedtirol-api/blob/master/LICENSE"

        ISSUE_EMAIL_FROM = "info@beacon.bz.it"
        ISSUE_EMAIL_TO = "info@beacon.bz.it"

        MAIL_HOST = "email-smtp.eu-west-1.amazonaws.com"
        MAIL_PORT = "587"
        MAIL_USERNAME = credentials('beacon-api-mail-username')
        MAIL_PASSWORD = credentials('beacon-api-mail-password')
        MAIL_SMTP_AUTH = "true"
        MAIL_SMTP_STARTTLS = "true"

        BEACON_TRUSTED_USERNAME = credentials('beacon-api-trusted-username')
        BEACON_TRUSTED_PASSWORD = credentials('beacon-api-trusted-password')

        BEACON_PASSWORD_RESET_URL = "https://admin.beacon.bz.it/"
        BEACON_PASSWORD_RESET_MAIL = "info@beacon.bz.it"
    }

    stages { 
        stage('Configure') {
            steps {
                sh '''
                    cp src/main/resources/application.dist.properties src/main/resources/application.properties
                    sed -i -e "s%\\(spring.datasource.url\\s*=\\).*\\$%\\1${DB_URL}%" src/main/resources/application.properties
                    sed -i -e "s%\\(spring.datasource.username\\s*=\\).*\\$%\\1${DB_USERNAME}%" src/main/resources/application.properties
                    sed -i -e "s%\\(spring.datasource.password\\s*=\\).*\\$%\\1${DB_PASSWORD}%" src/main/resources/application.properties
                    sed -i -e "s%\\(security.jwt.token.secret\\s*=\\).*\\$%\\1${JWT_SECRET}%" src/main/resources/application.properties
                    sed -i -e "s%\\(security.jwt.token.expire-length\\s*=\\).*\\$%\\1${JWT_EXPIRE_LENGTH}%" src/main/resources/application.properties
                    sed -i -e "s%\\(file.upload-dir\\s*=\\).*\\$%\\1${UPLOAD_DIR}%" src/main/resources/application.properties
                    sed -i -e "s%\\(it.bz.beacon.allowedOrigins\\s*=\\).*\\$%\\1${CORS_ORIGINS}%" src/main/resources/application.properties

                    sed -i -e "s%\\(api.info.host\\s*=\\).*\\$%\\1${INFO_HOST}%" src/main/resources/application.properties
                    sed -i -e "s%\\(api.info.title\\s*=\\).*\\$%\\1${INFO_TITLE}%" src/main/resources/application.properties
                    sed -i -e "s%\\(api.info.description\\s*=\\).*\\$%\\1${INFO_DESCRIPTION}%" src/main/resources/application.properties
                    sed -i -e "s%\\(api.info.version\\s*=\\).*\\$%\\1${INFO_VERSION}%" src/main/resources/application.properties
                    sed -i -e "s%\\(api.info.termsOfServiceUrl\\s*=\\).*\\$%\\1${INFO_TERMS_OF_SERVICE_URL}%" src/main/resources/application.properties
                    sed -i -e "s%\\(api.info.contactName\\s*=\\).*\\$%\\1${INFO_CONTACT_NAME}%" src/main/resources/application.properties
                    sed -i -e "s%\\(api.info.contactUrl\\s*=\\).*\\$%\\1${INFO_CONTACT_URL}%" src/main/resources/application.properties
                    sed -i -e "s%\\(api.info.contactEmail\\s*=\\).*\\$%\\1${INFO_CONTACT_EMAIL}%" src/main/resources/application.properties
                    sed -i -e "s%\\(api.info.license\\s*=\\).*\\$%\\1${INFO_LICENSE}%" src/main/resources/application.properties
                    sed -i -e "s%\\(api.info.licenseUrl\\s*=\\).*\\$%\\1${INFO_LICENSE_URL}%" src/main/resources/application.properties

                    sed -i -e "s%\\(it.bz.beacon.issueEmailFrom\\s*=\\).*\\$%\\1${ISSUE_EMAIL_FROM}%" src/main/resources/application.properties
                    sed -i -e "s%\\(it.bz.beacon.issueEmailTo\\s*=\\).*\\$%\\1${ISSUE_EMAIL_TO}%" src/main/resources/application.properties

                    sed -i -e "s%\\(spring.mail.host\\s*=\\).*\\$%\\1${MAIL_HOST}%" src/main/resources/application.properties
                    sed -i -e "s%\\(spring.mail.port\\s*=\\).*\\$%\\1${MAIL_PORT}%" src/main/resources/application.properties
                    sed -i -e "s%\\(spring.mail.username\\s*=\\).*\\$%\\1${MAIL_USERNAME}%" src/main/resources/application.properties
                    sed -i -e "s%\\(spring.mail.password\\s*=\\).*\\$%\\1${MAIL_PASSWORD}%" src/main/resources/application.properties
                    sed -i -e "s%\\(spring.mail.properties.mail.smtp.auth\\s*=\\).*\\$%\\1${MAIL_SMTP_AUTH}%" src/main/resources/application.properties
                    sed -i -e "s%\\(spring.mail.properties.mail.smtp.starttls.enable\\s*=\\).*\\$%\\1${MAIL_SMTP_STARTTLS}%" src/main/resources/application.properties

                    sed -i -e "s%\\(it.bz.beacon.trusted.user\\s*=\\).*\\$%\\1${BEACON_TRUSTED_USERNAME}%" src/main/resources/application.properties
                    sed -i -e "s%\\(it.bz.beacon.trusted.password\\s*=\\).*\\$%\\1${BEACON_TRUSTED_PASSWORD}%" src/main/resources/application.properties

                    sed -i -e "s%\\(it.bz.beacon.passwordResetURL\\s*=\\).*\\$%\\1${BEACON_PASSWORD_RESET_URL}%" src/main/resources/application.properties
                    sed -i -e "s%\\(it.bz.beacon.passwordResetEmailFrom\\s*=\\).*\\$%\\1${BEACON_PASSWORD_RESET_MAIL}%" src/main/resources/application.properties
                '''
            }
        }
        stage('Test') {
            steps {
                sh 'mvn -B -U clean test verify'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn -B -U -Dmaven.test.skip=true clean package'
            }
        }
        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/beacon-api.war', onlyIfSuccessful: true
            }
        }
    }
}
