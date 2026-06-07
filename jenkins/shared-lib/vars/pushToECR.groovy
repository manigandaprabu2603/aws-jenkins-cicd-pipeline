import org.company.utils.AwsHelper

def call() {

    def aws = new AwsHelper(this)

    echo "Logging into AWS ECR..."

    aws.loginToECR()

    sh """
    docker tag ${env.ECR_REPO}:${env.IMAGE_TAG} \
    ${aws.getAccountId()}.dkr.ecr.${env.AWS_REGION}.amazonaws.com/${env.ECR_REPO}:${env.IMAGE_TAG}

    docker push \
    ${aws.getAccountId()}.dkr.ecr.${env.AWS_REGION}.amazonaws.com/${env.ECR_REPO}:${env.IMAGE_TAG}
    """
}