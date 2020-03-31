ARG BUILD_TAG=8-jdk-alpine
ARG RUN_TAG=8-jre-alpine

FROM openjdk:${BUILD_TAG} AS build-env
WORKDIR /src

# Copy src .dockerignore prevents copy of unnecessary files
COPY . .

# Gradle build with daemon disabled
RUN ./gradlew build --no-daemon --stacktrace

FROM openjdk:${RUN_TAG} AS run-env
WORKDIR /app

# Copy generated .jar
COPY --from=build-env /src/build/libs/*.jar i-on-integration.jar

ENTRYPOINT [ "java", "-jar", "i-on-integration.jar" ]