# Jenkins CI/CD Pipeline with Docker, AWS ECR & EC2

A **production-ready CI/CD pipeline project** demonstrating how to build, containerize, push, and deploy an application using:

- Jenkins
- Docker
- AWS ECR (Elastic Container Registry)
- AWS EC2 (Elastic Compute Cloud)
- Node.js
- GitHub

The pipeline uses **Jenkins Shared Libraries** to implement a reusable and modular CI/CD workflow.

---

# Project Overview

This project demonstrates a **complete DevOps pipeline**:

1. Developer pushes code to GitHub
2. Jenkins pipeline triggers
3. Application is built
4. Docker image is created
5. Image is pushed to AWS ECR
6. EC2 pulls the latest image
7. Application container is restarted

---

# Architecture

```
Developer
    │
    ▼
GitHub Repository
    │
    ▼
Jenkins Pipeline
    │
    ├── Build Node Application
    │
    ├── Build Docker Image
    │
    ├── Push Image → AWS ECR
    │
    └── Deploy Container → EC2
```

---

# Project Structure

```
project-root
│
├── app
│   ├── server.js
│   └── package.json
│
├── docker
│   └── Dockerfile
│
├── jenkins
│   ├── Jenkinsfile
│   │
│   └── shared-lib
│       │
│       ├── vars
│       │   ├── buildDocker.groovy
│       │   ├── pushToECR.groovy
│       │   └── deployToEC2.groovy
│       │
│       ├── src
│       │   └── org/company/utils
│       │       └── AwsHelper.groovy
│       │
│       └── resources
│           └── config
│               └── deployment.json
│
├── .gitignore
└── README.md
```

---

# Step 1: Create EC2 Instance for Jenkins

Launch an EC2 instance that automatically installs Jenkins and Docker.

### EC2 Configuration

Instance Type

```
t2.medium
```

Operating System

```
Ubuntu 24.04
```

Security Group

Allow these ports:

```
22  → SSH
8080 → Jenkins
3000 → Application
80 → Web
```

---

# Step 2: EC2 User Data Script (Auto Install Jenkins)

During EC2 creation, paste the following script into **User Data**.

```
#!/bin/bash

exec > /var/log/user-data.log 2>&1
set -eux

export DEBIAN_FRONTEND=noninteractive

# -------------------------------
# System update
# -------------------------------
apt-get update -y

# -------------------------------
# Install base dependencies
# -------------------------------
apt-get install -y \
  fontconfig \
  openjdk-21-jre \
  docker.io \
  curl \
  wget \
  gnupg \
  ca-certificates \
  unzip

curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "/tmp/awscliv2.zip"
unzip /tmp/awscliv2.zip -d /tmp
/tmp/aws/install
aws --version

systemctl enable docker
systemctl start docker

usermod -aG docker ubuntu

# -------------------------------
# Jenkins Install
# -------------------------------

# Create keyrings directory
mkdir -p /etc/apt/keyrings

# Download NEW Jenkins key (2026)
wget -O /etc/apt/keyrings/jenkins-keyring.asc \
  https://pkg.jenkins.io/debian-stable/jenkins.io-2026.key

# Add Jenkins repo
echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/" \
  > /etc/apt/sources.list.d/jenkins.list

# Update package list
apt-get update -y

# Install Jenkins
apt-get install -y jenkins

systemctl enable jenkins
systemctl start jenkins

# Add Jenkins to Docker group
usermod -aG docker jenkins

# Restart services
systemctl restart docker
systemctl restart jenkins
```

This script installs:

- Java
- Docker
- Jenkins
- AWS CLI

---

# Step 3: Access Jenkins

After EC2 is running, open:

```
http://EC2_PUBLIC_IP:8080
```

To get the Jenkins initial password:

```
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

---

# Step 4: Install Jenkins Plugins

Install recommended plugins plus:

```
Docker Pipeline
Pipeline
Git
AWS Credentials
SSH Agent
```

---

# Step 5: Configure AWS Credentials in Jenkins

Go to:

```
Manage Jenkins
→ Credentials
→ Global
→ Add Credentials (AWS Credentials)
```

Add:

```
ID (my_aws_credential)
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
```

---

# Step 6: Create AWS ECR Repository

Example:

```
jenkins-demo-app
```

You can create it using AWS CLI:

```
aws ecr create-repository --repository-name jenkins-demo-app
```

---

# Step 7: Create Jenkins Pipeline

Create a **Pipeline Project** in Jenkins.

Pipeline Definition:

```
Pipeline script from SCM
```

Repository:

```
GitHub Repository URL
```

Script Path:

```
jenkins/Jenkinsfile
```

---

# step to setup shared library

---

# Jenkins Pipeline Stages

| Stage | Description |
|-----|-------------|
| Checkout | Pull code from GitHub |
| Build | Build Docker image |
| Push | Push image to AWS ECR |
| Deploy | Deploy container to EC2 |

---

# Jenkins Shared Library

This project uses **Jenkins Shared Libraries**.

| Folder | Purpose |
|------|--------|
| vars | Pipeline steps |
| src | Reusable Groovy classes |
| resources | Config files |

Example:

```
@Library('local-shared-lib') _
```

---

# Run Application Locally

Install dependencies:

```
npm install
```

Start server:

```
node app/server.js
```

Access:

```
http://localhost:3000
```

---

# Test Docker Locally

Build image:

```
docker build -f docker/Dockerfile -t demo-app .
```

Run container:

```
docker run -p 3000:3000 demo-app
```

---

# CI/CD Workflow

```
Git Push
   │
   ▼
Jenkins Pipeline
   │
   ├── Build Application
   ├── Build Docker Image
   ├── Push Image → AWS ECR
   └── Deploy → EC2
```

---

# Security Best Practices

- Use IAM roles instead of access keys
- Store secrets in Jenkins Credentials
- Restrict EC2 SSH access
- Enable ECR image scanning

---

# Author

DevOps CI/CD Demonstration Project

Built to demonstrate **production-ready Jenkins pipelines using AWS and Docker**.