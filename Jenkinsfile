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
                         jacoco()
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
		stage('Smoke test') {
			steps {
				sh 'cp /home/ubuntu/smoketestconfig/common.yaml smoketests/'
				sh 'cd smoketests && docker build -t zuehlke/bibweb-smoketests .'
				sh 'docker rm bibweb-smoketests || true'
				sh 'docker run -t --name bibweb-smoketests -e HOST_URL=https://`/usr/bin/curl -s http://169.254.169.254/latest/meta-data/public-hostname`:8443 zuehlke/bibweb-smoketests'
			}
		}
    }
	
	post {
		always {
			junit 'build/test-results/**/*.xml'
		}
	}
}
