pipeline {
    agent any

    stages {
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
                sh 'docker run --name=bibweb-backend --restart unless-stopped -d -p 8090:8080  -e DATABASE_URL=h2:file:~/test -e DATABASE_USER=sa -e DATABASE_PASSWORD= -e DATABASE_DRIVER=org.h2.Driver zuehlke/bibweb-backend'
            }
        }
    }
}
