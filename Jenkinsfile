pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build'
            }
        }
        stage('JaCoCo') {
                     steps {
                         sh './gradlew jacocoTestReport'
                     }
                 }
        stage('Docker image') {
            steps {
                sh './gradlew docker'
            }
        }
        stage('Deploy Docker local') {
            steps {
                sh 'docker service update --env-add "JENKINS_META=$JOB_NAME[$BUILD_NUMBER]" bibweb-backend'
            }
        }
    }
	
	post {
		always {
			junit 'build/test-results/**/*.xml'
		}
	}
}
