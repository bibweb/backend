pipeline {
    agent any

	options {
		timeout(time: 10, unit: 'MINUTES')
	}
	
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
                 jacoco exclusionPattern: '**/*Test*.class,**/*IT*.class', sourceExclusionPattern: '**/test/**'
             }
        }

        stage('Sonarqube') {
            steps {
                withSonarQubeEnv('SonarQube Scanner') {
                    sh './gradlew sonarqube -Dsonar.host.url=http://localhost:9000/sonar -Dsonar.projectVersion=$BUILD_NUMBER'
                }
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
				sh 'wget --retry-connrefused --no-check-certificate --tries=120 --waitretry=1 -q https://172.17.0.1:8443 -O /dev/null || true'
				sh 'mkdir outTavern/ || true'
				sh 'cp /home/ubuntu/smoketestconfig/common.yaml smoketests/'
				sh 'cd smoketests && docker build -t zuehlke/bibweb-smoketests .'
				sh 'docker rm -f bibweb-smoketests || true'					
				sh 'docker run --name bibweb-smoketests -e HOST_URL=https://172.17.0.1:8443 zuehlke/bibweb-smoketests'
			}
		}
    }
	
	post {
		always {
			sh 'docker cp bibweb-smoketests:/tests/out/results.xml outTavern/ || true'
			junit 'build/test-results/**/*.xml'
			junit 'outTavern/results.xml'
		}
	}
}
