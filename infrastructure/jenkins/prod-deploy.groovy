pipeline {
    agent any

    environment {
        AWS_ACCESS_KEY_ID = credentials('aws_secret_key_id')
        AWS_SECRET_ACCESS_KEY = credentials('aws_secret_access_key')
    }

    stages {
        stage('Clean') {
            steps {
                sh '''
                    rm -rf build
                    cp -R prod-tomcat-beacon build
                '''
            }
        }
        stage('Load') {
            steps {
                copyArtifacts projectName: 'it.bz.beacon/api.prod-archive', 
                    filter: 'target/beacon-api.war', 
                    target: 'build', 
                    flatten: true, 
                    selector: lastSuccessful(), 
                    fingerprintArtifacts: true
                sh '''
                    cd build
                    unzip beacon-api.war
                    rm -rf build/beacon-api.war
                '''
            }
        }
        stage('Deploy') {
            steps {
                sh 'cd build && ~/.local/bin/eb deploy'
            }
        }
    }
}
