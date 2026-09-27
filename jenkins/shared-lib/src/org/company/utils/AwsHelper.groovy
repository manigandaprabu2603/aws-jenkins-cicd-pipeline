package org.company.utils

class AwsHelper implements Serializable {

    def steps
    static final String CREDENTIALS_ID = 'my_aws_credential'

    AwsHelper(steps) {
        this.steps = steps
    }

    private withAwsCredentials(Closure body) {
        steps.withCredentials([
            [
                $class: 'AmazonWebServicesCredentialsBinding',
                credentialsId: CREDENTIALS_ID
            ]
        ], body)
    }

    def loginToECR() {
        withAwsCredentials {
            steps.sh """
            aws ecr get-login-password --region ${steps.env.AWS_REGION} \
            | docker login --username AWS --password-stdin \
            ${getAccountId()}.dkr.ecr.${steps.env.AWS_REGION}.amazonaws.com
            """
        }
    }

    def getAccountId() {
        return withAwsCredentials {
            steps.sh(
                script: "aws sts get-caller-identity --query Account --output text",
                returnStdout: true
            ).trim()
        }
    }
}
