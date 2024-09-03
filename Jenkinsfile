pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'hyeonjin-cicd',
                    url: 'https://github.com/oh-my-guide/OMG_project.git',
                    credentialsId: 'hjinnny_github_id'
            }
        }
        stage('Add Env') {
            steps {
                dir('src/main/resources') {
                    withCredentials([file(credentialsId: 'OMGyml', variable: 'YML_FILE')]) {
                        sh """
                        echo "Copying configuration file..."
                        cp ${YML_FILE} application.yml
                        ls -l
                        """
                    }
                }
            }
        }

        stage('Build') {
            steps {
                echo 'Building...'
                sh './gradlew clean build' // Gradle을 사용한 빌드
            }
        }

        stage('Test') {
            steps {
                echo 'Testing...'
                sh './gradlew test' // Gradle을 사용한 테스트 실행
            }
        }

        stage('Archive Artifacts') {
            steps {
                echo 'Archiving JAR...'
                archiveArtifacts artifacts: 'build/libs/*.jar', allowEmptyArchive: false // 생성된 JAR 파일을 Jenkins에 저장
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying...'
                // 배포 관련 스크립트 또는 명령어를 추가
                // 예: SCP로 JAR 파일을 서버로 복사, Docker 이미지를 빌드 및 배포 등
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished.'
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
