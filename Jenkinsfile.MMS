pipeline {
    agent any

    tools {
        maven 'maven'
        jdk 'JDK8'
    }

    options {
        timestamps()
    }

    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }

        stage ('Build') {
            steps {
                withMaven(maven: 'maven') {
                    sh 'mvn -Penable-jacoco -Dmaven.test.failure.ignore=true checkstyle:checkstyle install -e' +
                            ' sonar:sonar -Dsonar.host.url=https://sonarqube.com -Dsonar.login=2d490959a4bfab63bd2b7f41d43347955ebfe939'
                }
            }
            post {
                always {
                    step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: 'raphael.pionke@t-systems.com', sendToIndividuals: false])
                }
                success {
                    step([$class: 'JacocoPublisher'])
                }
            }
        }
    }
}
