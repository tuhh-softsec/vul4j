pipeline {
    agent any

    tools {
        maven 'autoMaven'
        jdk 'JDK8'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr:'15'))
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
                sh 'mvn -Dmaven.test.failure.ignore=true checkstyle:checkstyle install -e' +
                        ' sonar:sonar -Dsonar.host.url=https://sonarqube.com -Dsonar.login=2d490959a4bfab63bd2b7f41d43347955ebfe939'
            }
            post {
                success {
                    junit '**/surefire-reports/TEST-*.xml'
                    step([$class: 'CheckStylePublisher', canComputeNew: false, defaultEncoding: '', healthy: '', pattern: '', unHealthy: ''])
                    step([$class: 'FindBugsPublisher', canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: '', unHealthy: ''])
                    step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: 'rapi@mms-dresden.de', sendToIndividuals: false])
                    step([$class: 'JacocoPublisher'])
                    archiveArtifacts '**/target/*.hpi'
                }
            }
        }
    }
}