#!groovy

node('host_docker') {

stage('Build') {
notifyHipChat('STARTED', 'Build')

echo "Running ${env.BUILD_ID} [branch:${env.BRANCH_NAME}] on ${env.JENKINS_URL}"
deleteDir() //delete the cloned dir before each build
checkout scm //Jenkins with multibranch support
sh '/opt/maven/bin/mvn clean package -DskipTests -U'
}

stage('Test') {
echo "Running TEST of ${env.BUILD_ID} [branch:${env.BRANCH_NAME}] on ${env.JENKINS_URL}"
sh '/opt/maven/bin/mvn test -U'
}

stage('Release') {
notifyHipChat('STARTED', 'Release')

def imageName = "${DOCKER_REPO}/${PROJECT_NAME}"
def imageId = dockerBuildTag(imageName)
dockerPush(imageName, imageId)

notifyHipChat('FINISHED', 'Release')
}

