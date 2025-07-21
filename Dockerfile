FROM maven:eclipse-temurin AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn verify --fail-never
COPY src ./src
RUN mvn clean package -DskipTests --batch-mode

FROM ghcr.io/octopusdeploylabs/k8s-workertools

RUN echo 'APT::Install-Suggests "0";' >> /etc/apt/apt.conf.d/00-docker
RUN echo 'APT::Install-Recommends "0";' >> /etc/apt/apt.conf.d/00-docker
RUN DEBIAN_FRONTEND=noninteractive \
  apt-get update \
  && apt-get install -y default-jre procps \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /k8smock
COPY --from=builder /app/target/mockk8s.jar .
COPY kubectl .
RUN chmod +x kubectl
ENV PATH="/k8smock:${PATH}"