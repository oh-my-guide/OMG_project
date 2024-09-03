pipeline {
    agent any
    environment {
        AWS_DEFAULT_REGION = 'ap-northeast-2'
        S3_BUCKET = 'omg-build'
        JAR_FILE = 'build/libs/OMG_project-0.0.1-SNAPSHOT.jar'
        APP_NAME = 'OMG'
        DEPLOY_GROUP = 'OMG-group-name'
        DEPLOY_ZIP = 'deployment.zip'
    }

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
                sh 'chmod 755 ./gradlew'
                sh './gradlew build'
            }
        }

        stage('Test') {
            steps {
                echo 'Testing...'
            }
        }

        stage('Prepare Deployment Package') {
            steps {
                echo 'Preparing deployment package...'
                sh """
                cd deployment
                zip -r ${env.DEPLOY_ZIP} appspec.yml scripts/
                mv ${env.DEPLOY_ZIP} ../
                cd ..
                zip -r ${env.DEPLOY_ZIP} -g ${env.JAR_FILE}
                """
            }
        }

        stage('Upload to S3') {
            steps {
                withAWS(credentials: 'aws_omg') {
                    s3Upload(bucket: env.S3_BUCKET, file: env.DEPLOY_ZIP)
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying...'
                withAWS(credentials: 'aws_omg') {
                sh """
                aws deploy create-deployment \
                    --application-name ${env.APP_NAME} \
                    --deployment-group-name ${env.DEPLOY_GROUP} \
                    --s3-location bucket=${env.S3_BUCKET},key=${env.DEPLOY_ZIP},bundleType=zip
                """
                }
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
