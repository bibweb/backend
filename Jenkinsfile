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
                withCredentials([usernamePassword(credentialsId: 'mysql_pwd', usernameVariable: 'BIBUSER', passwordVariable: 'BIBPWD')]) {
                    sh '''
                        docker run --name=bibweb-backend --restart unless-stopped -d -p 8090:8080 \
                        -e DATABASE_URL=mysql://bibweb-mysql:3306/bibweb \
                        -e DATABASE_USER=$BIBUSER \
                        -e DATABASE_PASSWORD=$BIBPWD \
                        -e DATABASE_DRIVER=com.mysql.jdbc.Driver \
                        --link bibweb-mysql:mysql \
                        zuehlke/bibweb-backend
                    '''
                }
            }
        }
    }
}
