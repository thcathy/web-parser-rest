pipeline {
  agent {
    docker {
      args '-v $HOME/.gradle:$HOME/.gradle'
      image 'cimg/openjdk:13.0'
    }
  }

  environment {
    jasypt_encryptor_password = credentials('JASYPT_ENCRYPTOR_PASSWORD')
    docker_image_tag = "${readMavenPom().getVersion()}-${env.BUILD_NUMBER}"
  }

  stages {
    stage('build and test') {
      steps {
        sh 'chmod +x gradlew'
        sh './gradlew dependencies'
        sh './gradlew check'
        publishHTML (target: [
          reportDir: 'build/site/jacoco/',
          reportFiles: 'index.html',
          reportName: "JaCoCo Report"
        ])
        junit '.*/build/test-results/.*xml'
        sh './gradlew assemble'
      }
    }

    stage("Docker build") {
      environment {
        DOCKER_LOGIN = credentials('DOCKER_LOGIN')
      }
      steps {
        sh "docker build -t thcathy/web-parser-rest:latest -t thcathy/web-parser-rest:${docker_image_tag} -f Dockerfile ."
        sh "docker login -u $DOCKER_LOGIN_USR -p $DOCKER_LOGIN_PSW"
        sh "docker push thcathy/web-parser-rest:latest"
        sh "docker push thcathy/web-parser-rest:${docker_image_tag}"
      }
    }

    stage("deploy and verify UAT") {
      when {
        branch 'master'
      }
      environment {
        DEPLOY_USER = 'thcathy'
      }
      agent {
        docker {
          image 'ansible/ansible-runner'
        }
      }
      steps {
        ansiblePlaybook(
          inventory: 'ansible/inventory_uat',
          limit: 'uat',
          extras: "-e docker_image_tag=${docker_image_tag} -e jasypt_encryptor_password=${jasypt_encryptor_password}",
          playbook: 'ansible/deploy.yml',
          credentialsId: 'Jenkins-master'
        )
      }
    }
  }

}
