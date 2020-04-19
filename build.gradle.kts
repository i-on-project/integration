import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.6.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.71"
    kotlin("plugin.spring") version "1.3.71"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
}

group = "org.ionproject"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val imageId = "docker.pkg.github.com/i-on-project/integration/i-on-integration"
val onlyBuild: String = "false"
val githubRef: String = ""

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.register<Copy>("extractUberJar") {
    dependsOn("build")
    dependsOn("test")
    dependsOn("ktlintCheck")
    from(zipTree("$buildDir/libs/${rootProject.name}-$version.jar"))
    into("$buildDir/dependency")
}

tasks.register<Exec>("buildDockerImage") {
    if ("$onlyBuild".toBoolean()) {
        commandLine("docker", "build", ".")
    } else {
        commandLine("docker", "build", ".", "--tag", "image")
    }
}

tasks.register<Exec>("tagPushDockerImage") {
    var version: String

    if ("$githubRef".isBlank()) {
        version = "latest"
    } else {
        commandLine("echo", "\"$githubRef\"", "|", "sed -e 's,.*/\\(.*\\),\\1,'")
        version = standardOutput.toString()

        commandLine("echo", "$version", "|", "sed -e 's/^v//'")
        version = standardOutput.toString()
    }

    commandLine("docker", "tag", "image", "$imageId:$version")
    commandLine("docker", "push", "$imageId:$version")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
