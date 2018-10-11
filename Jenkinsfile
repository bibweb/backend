pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/bibweb/backend'
            }
        }
        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build -x test'
            }
        }
        stage('Docker image') {
            steps {
                sh 'docker build --build-arg JAR_FILE=./build/libs/bibweb-backend-0.1.0.jar -t zuehlke/bibweb-backend .'
            }
        }
        stage('Deploy Docker local') {
            steps {
                sh 'docker stop bibweb-backend || true && docker rm -f bibweb-backend || true'
                sh 'docker run --name=bibweb-backend --restart unless-stopped -d -p 8090:8080 zuehlke/bibweb-backend'
            }
        }
    }
}