FROM gradle:8.14.3-jdk21-alpine AS build

WORKDIR /workspace/app

COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

COPY infra infra
COPY service service
COPY config config

ARG SERVICE_NAME
RUN gradle :${SERVICE_NAME}:build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine

ARG SERVICE_NAME
WORKDIR /app

COPY --from=build /workspace/app/${SERVICE_NAME}/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

