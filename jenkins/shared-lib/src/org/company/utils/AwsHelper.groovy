package org.company.utils

class AwsHelper implements Serializable {

    def steps

    AwsHelper(steps) {
        this.steps = steps
    }

    def loginToECR() {
        withCredentials([
            [
                $class: 'AmazonWebServicesCredentialsBinding',
                credentialsId: 'my_aws_credential'
            ]
        ])

        steps.sh """
        aws ecr get-login-password --region ${steps.env.AWS_REGION} \
        | docker login --username AWS --password-stdin \
        ${getAccountId()}.dkr.ecr.${steps.env.AWS_REGION}.amazonaws.com
        """
    }

    def getAccountId() {

        return steps.sh(
            script: "aws sts get-caller-identity --query Account --output text",
            returnStdout: true
        ).trim()

    }
}