ARG BUILD_TAG=7.0.0-jdk11-hotspot
ARG RUN_TAG=11.0.7_10-jre-hotspot-bionic

FROM gradle:${BUILD_TAG} AS build-env
WORKDIR /src

# Copy project .dockerignore prevents copy of unnecessary files
COPY . .

# Gradle task to build, test, run lint and extract dependencies with daemon disabled
RUN gradle extractUberJar --no-daemon --stacktrace

FROM adoptopenjdk:${RUN_TAG} AS run-env
RUN useradd -m spring && usermod -a -G spring spring \
    && mkdir -p /app/resources \
    && chown spring /app/resources \
    && mkdir -p /app/resources/output \
    && chown spring /app/resources/output \
    && mkdir -p /app/staging \
    && chown spring /app/staging

USER spring:spring

ARG EXTRACT_DEPENDENCY_PATH=/src/build/dependency

# Copy dependencies in multi layers
COPY --from=build-env ${EXTRACT_DEPENDENCY_PATH}/BOOT-INF/classes /app
COPY --from=build-env ${EXTRACT_DEPENDENCY_PATH}/BOOT-INF/lib /app/lib


ENTRYPOINT [ "java", "-cp", "app:app/lib/*", "org.ionproject.integration.IOnIntegrationApplicationKt", "-XX:+UseContainerSupport", "echo --server.port=$PORT" ]
