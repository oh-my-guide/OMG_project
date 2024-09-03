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

        stage('Build') {
            steps {
                echo 'Building...'
                // build steps
            }
        }

        stage('Test') {
            steps {
                echo 'Testing...'
                // test steps
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying...'
                // deploy steps
            }
        }
    }
}
