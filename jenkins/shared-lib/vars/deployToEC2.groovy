import groovy.json.JsonSlurper
import org.company.utils.AwsHelper

def call() {

    echo "Reading deployment configuration..."

    def config = libraryResource 'config/deployment.json'
    def json = new JsonSlurper().parseText(config)

    def aws = new AwsHelper(this)
    def accountId = aws.getAccountId()
    def registry = "${accountId}.dkr.ecr.${env.AWS_REGION}.amazonaws.com"
    def image = "${registry}/${env.ECR_REPO}:${env.IMAGE_TAG}"

    sshagent(credentials: ['ec2-ssh-key']) {
        sh """
        ssh -o StrictHostKeyChecking=no ${json.ec2_user}@${json.ec2_host} '
            aws ecr get-login-password --region ${env.AWS_REGION} | docker login --username AWS --password-stdin ${registry}
            docker pull ${image}
            docker stop ${json.container_name} || true
            docker rm ${json.container_name} || true
            docker run -d --name ${json.container_name} -p ${json.host_port}:${json.container_port} ${image}
        '
        """
    }
}
