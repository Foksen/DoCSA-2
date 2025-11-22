FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /workspace/app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

COPY infra infra
COPY service service
COPY config config

ARG SERVICE_NAME
RUN chmod +x gradlew && ./gradlew :${SERVICE_NAME//\//:}:build -x test --no-daemon \
    -Dorg.gradle.jvmargs="-Xmx512m -XX:MaxMetaspaceSize=256m"

FROM eclipse-temurin:17-jre-alpine

ARG SERVICE_NAME
WORKDIR /app

ARG SERVICE_NAME
COPY --from=build /workspace/app/${SERVICE_NAME}/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

