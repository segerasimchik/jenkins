// Check local properties

properties([disableConcurrentBuilds()])

pipeline {
    agent {
        label 'master'
    }
    parameters {
        choice(name: 'DRY_RUN', choices: ['No', 'Yes'], description: 'Do you want some dry run?')
    }

    triggers { pollSCM('H * * * *') }
    options {
        buildDiscarder(logRotator(numToKeepStr: '3', artifactNumToKeepStr: '2'))
        timestamps()
    }
    stages {
        stage("Docker login") {
           environment {
             dockerPasswd = credentials('dockerHubPasswd')
           }
           steps {
             echo "===========+=+= Docker login =+=+============"
             
             // withCredentials([
             //  string(credentialsId: 'dockerHubPasswd', variable: 'dockerPswd')]) {
                 sh 'docker login -u seger -p $dockerPasswd'
             //}
           }
        }
        stage("Some variables") {
           steps {
              sh 'pwd'
              sh 'whoami'
           }
        }
        stage("Create docker image") {
            environment {
              build_id = "${env.BUILD_ID}"
            }
            steps {
                echo "========== start building image =========="
                dir ('./') {
                    echo "$build_id"
                    sh "docker build -t seger/jenkins-nginx:1.${env.BUILD_ID} ."
                    sh 'docker images'
                    sh '''
                    n_of_img="$(docker images -q | wc -l)"
                    echo "$n_of_img"
                    if [ "$n_of_img" -gt 3 ]; then
                      echo "$n_of_img"
                      amount="$(($n_of_img-3))"
                      echo "$amount"
                      docker rmi -f $(docker images -q | head -n $amount)
                      docker images
                    fi
                    '''
                }
            }
        }
       stage("Push docker image") {
         steps {
           echo "Choice: ${params.DRY_RUN}"
           script {
             if (params.DRY_RUN == 'Yes') {
               echo "[INFO] 'Yes' variant was selected. Nothing will be deployed!"
               currentBuild.result = 'NOT_BUILT'
               return
             }
             else {
               echo "========== Start pushing image ============="
               sh "docker push seger/jenkins-nginx:1.${env.BUILD_ID}"
             }
           }
         }
      }
    }
}
