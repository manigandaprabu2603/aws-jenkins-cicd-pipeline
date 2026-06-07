def call() {

    echo "Building Docker Image..."

    sh """
    docker build -t ${env.ECR_REPO}:${env.IMAGE_TAG} -f docker/Dockerfile .
    """

}