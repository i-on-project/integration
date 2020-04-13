ARG BUILD_TAG=8-jdk-alpine
ARG RUN_TAG=8-jre-alpine

FROM openjdk:${BUILD_TAG} AS build-env
WORKDIR /src

# Copy project .dockerignore prevents copy of unnecessary files
COPY . .

# Gradle task for linter to check errors with daemon disabled
RUN ./gradlew ktlintCheck --no-daemon

# Gradle task to test with daemon disabled
RUN ./gradlew test --no-daemon

# Gradle task to build and extract dependencies with daemon disabled
RUN ./gradlew extractUberJar --no-daemon --stacktrace

FROM openjdk:${RUN_TAG} AS run-env
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ARG EXTRACT_DEPENDENCY_PATH=/src/build/dependency

# Copy dependencies in multi layers
COPY --from=build-env ${EXTRACT_DEPENDENCY_PATH}/BOOT-INF/classes /app
COPY --from=build-env ${EXTRACT_DEPENDENCY_PATH}/BOOT-INF/lib /app/lib

ENTRYPOINT [ "java", "-cp", "app:app/lib/*", "org.ionproject.integration.IOnIntegrationApplicationKt" ]