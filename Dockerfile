ARG BUILD_TAG=11.0.7_10-jdk-hotspot-bionic
ARG RUN_TAG=11.0.7_10-jre-hotspot-bionic

FROM adoptopenjdk:${BUILD_TAG} AS build-env
WORKDIR /src

# Copy project .dockerignore prevents copy of unnecessary files
COPY . .

# Gradle task to build, test, run lint and extract dependencies with daemon disabled
RUN ./gradlew extractUberJar --no-daemon --stacktrace

FROM adoptopenjdk:${RUN_TAG} AS run-env
RUN useradd -m spring && usermod -a -G spring spring
USER spring:spring

# Copy dependencies in multi layers
COPY --from=build-env ${EXTRACT_DEPENDENCY_PATH}/BOOT-INF/classes /app
COPY --from=build-env ${EXTRACT_DEPENDENCY_PATH}/BOOT-INF/lib /app/lib

ENTRYPOINT [ "java", "-cp", "app:app/lib/*", "org.ionproject.integration.IOnIntegrationApplicationKt" ]
