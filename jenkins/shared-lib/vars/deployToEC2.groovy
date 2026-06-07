import groovy.json.JsonSlurper

def call() {

    echo "Reading deployment configuration..."

    def config = libraryResource 'config/deployment.json'
    def json = new JsonSlurper().parseText(config)

    sh """
    ssh ec2-user@${json.ec2_host} << EOF
        docker pull ${getAccountId()}.dkr.ecr.${steps.env.AWS_REGION}.amazonaws.com/${env.ECR_REPO}:${env.IMAGE_TAG}
        docker stop ${json.container_name} || true
        docker rm ${json.container_name} || true
        docker run -d \
            --name ${json.container_name} \
            -p ${json.host_port}:${json.container_port} \
            ${json.ecr_repo}:${env.IMAGE_TAG}
    EOF
    """

}