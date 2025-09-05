# ========= Étape 1 : Build Gradle =========
FROM debian:bullseye-slim AS builder

ENV GRADLE_VERSION=8.5 \
    DEBIAN_FRONTEND=noninteractive

RUN apt-get update && \
    apt-get install -y curl unzip zip git wget openjdk-17-jdk ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# Installer Gradle "standalone" (si tu n'utilises pas le wrapper)
RUN wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -P /tmp && \
    unzip -d /opt/gradle /tmp/gradle-${GRADLE_VERSION}-bin.zip && \
    ln -s /opt/gradle/gradle-${GRADLE_VERSION}/bin/gradle /usr/bin/gradle

WORKDIR /app
COPY . .

# Build (sans tests)
RUN gradle build -x test --no-daemon

# ========= Étape 2 : Runtime =========
FROM openjdk:17-slim

# curl pour le healthcheck
RUN apt-get update && apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Récupérer le JAR buildé
ARG JAR_FILE=/app/build/libs/*.jar
COPY --from=builder ${JAR_FILE} /app/app.jar

EXPOSE 8080

# (optionnel) healthcheck directement dans l’image
HEALTHCHECK --interval=10s --timeout=5s --retries=20 \
  CMD curl -fsS http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
