import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.3.10.RELEASE"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.0"
    kotlin("plugin.spring") version "1.5.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

repositories {
    mavenCentral()
}

group = "org.ionproject"
version = "1.2.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11

val tempDockerTag: String = "i-on-integration-image"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mail:2.3.10.RELEASE")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("technology.tabula:tabula:1.0.4") {
        exclude(group = "org.slf4j")
    }
    implementation("com.itextpdf:kernel:7.1.15")
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.3")
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.11.0.202103091610-r")
    implementation("org.springdoc:springdoc-openapi-ui:1.5.9")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.5.9")
    runtimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.batch:spring-batch-test:4.2.6.RELEASE")
    testImplementation("com.icegreen:greenmail:1.6.3")
    testImplementation("org.apache.commons:commons-email:1.5")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
}

tasks.register<Copy>("extractUberJar") {
    dependsOn("build")
    dependsOn("ktlintCheck")
    from(zipTree("$buildDir/libs/${rootProject.name}-$version.jar"))
    into("$buildDir/dependency")
}

tasks.register<Exec>("buildDockerImage") {
    if (project.properties["onlyBuild"].toString().toBoolean()) {
        commandLine("docker", "build", ".")
    } else {
        commandLine("docker", "build", ".", "--tag", tempDockerTag)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
