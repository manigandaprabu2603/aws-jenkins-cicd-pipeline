import groovy.json.JsonSlurper

def call() {

    echo "Reading deployment configuration..."

    def config = libraryResource 'config/deployment.json'
    def json = new JsonSlurper().parseText(config)

    sh """
    ssh ec2-user@${json.ec2_host} << EOF
        docker pull ${json.ecr_repo}:${env.IMAGE_TAG}
        docker stop app || true
        docker rm app || true
        docker run -d -p 80:3000 --name app ${json.ecr_repo}:${env.IMAGE_TAG}
    EOF
    """

}