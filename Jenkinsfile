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
				sh 'mkdir outTavern/ || true'
				sh 'cp /home/ubuntu/smoketestconfig/common.yaml smoketests/'
				sh 'cd smoketests && docker build -t zuehlke/bibweb-smoketests .'
				sh 'docker rm -f bibweb-smoketests || true'
				sh 'docker run --network host --name bibweb-smoketests -e HOST_URL=https://jenkins:8443 zuehlke/bibweb-smoketests'
				sh 'docker cp bibweb-smoketests:/tests/out/results.xml outTavern/'
			}
		}
    }
	
	post {
		always {
			junit 'build/test-results/**/*.xml'
			junit 'outTavern/results.xml'
		}
	}
}
