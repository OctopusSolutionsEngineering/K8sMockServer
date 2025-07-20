FROM maven:eclipse-temurin AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests --batch-mode

FROM ghcr.io/octopusdeploylabs/k8s-workertools
RUN apt-get install -y default-jre procps
WORKDIR /k8smock
COPY --from=builder /app/target/mockk8s.jar .
COPY kubectl /app
ENV PATH="/app:${PATH}"