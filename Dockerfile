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

ARG EXTRACT_DEPENDENCY_PATH=/src/build/dependency
ARG SPRING_PROFILE
ARG GOOGLE_CLOUD_SQL_POSTGRES_USER
ARG GOOGLE_CLOUD_SQL_POSTGRES_PASSWORD
ARG ION_CORE_BASE_URL
ARG ION_CORE_TOKEN

# Set active profile
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILE
ENV POSTGRES_USER=$GOOGLE_CLOUD_SQL_POSTGRES_USER
ENV POSTGRES_PASSWORD=$GOOGLE_CLOUD_SQL_POSTGRES_PASSWORD
ENV BASE_URL=$ION_CORE_BASE_URL
ENV TOKEN=$ION_CORE_TOKEN

# Copy dependencies in multi layers
COPY --from=build-env ${EXTRACT_DEPENDENCY_PATH}/BOOT-INF/classes /app
COPY --from=build-env ${EXTRACT_DEPENDENCY_PATH}/BOOT-INF/lib /app/lib

ENTRYPOINT [ "java", "-cp", "app:app/lib/*", "org.ionproject.integration.IOnIntegrationApplicationKt" ]
