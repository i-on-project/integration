ARG BUILD_TAG=8-jdk-alpine
ARG RUN_TAG=8-jre-alpine

FROM openjdk:${BUILD_TAG} AS build-env
WORKDIR /src

# Copy project .dockerignore prevents copy of unnecessary files
COPY . .

# Gradle task build and extract dependencies with daemon disabled
RUN ./gradlew extractUberJar --no-daemon --stacktrace

FROM openjdk:${RUN_TAG} AS run-env
ARG EXTRACT_DEPENDENCY_PATH=/src/build/dependency

# Copy dependencies in multi layers
COPY --from=build-env ${EXTRACT_DEPENDENCY_PATH}/BOOT-INF/classes /app
COPY --from=build-env ${EXTRACT_DEPENDENCY_PATH}/BOOT-INF/lib /app/lib

ENTRYPOINT [ "java", "-cp", "app:app/lib/*", "org.ionproject.integration.IOnIntegrationApplicationKt" ]