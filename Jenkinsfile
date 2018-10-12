pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build'
            }
        }
        stage('Docker image') {
            steps {
                sh './gradlew docker'
            }
        }
        stage('Deploy Docker local') {
            steps {
                sh 'docker stop bibweb-backend || true && docker rm -f bibweb-backend || true'
                withCredentials([usernamePassword(credentialsId: 'bibweb_mysql', usernameVariable: 'BIBUSER', passwordVariable: 'BIBPWD')]) {
                    sh '''
                        docker run --name=bibweb-backend --restart unless-stopped -d -p 8090:8080 \
                        -e DATABASE_URL=mysql://localhost:3306/bibweb \
                        -e DATABASE_USER=$BIBUSER \
                        -e DATABASE_PASSWORD=$BIBPWD \
                        -e DATABASE_DRIVER=com.mysql.jdbc.Driver \
                        --net="host" \
                        zuehlke/bibweb-backend
                    '''
                }
            }
        }
    }
}
