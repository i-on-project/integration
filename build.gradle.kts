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
java.sourceCompatibility = JavaVersion.VERSION_11

val imageId = "gcr.io/single-conquest-272617/i-on-project/integration/i-on-integration"
val tempDockerTag: String = "i-on-integration-image"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("technology.tabula:tabula:1.0.3") {
        exclude(group = "org.slf4j")
    }
    implementation("com.itextpdf:kernel:7.0.0")
    runtimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    implementation("com.squareup.moshi:moshi-kotlin:1.9.2")
    testImplementation("org.springframework.batch:spring-batch-test:4.2.2.RELEASE")
}

tasks.register<Copy>("extractUberJar") {
    dependsOn("build")
    dependsOn("test")
    dependsOn("ktlintCheck")
    from(zipTree("$buildDir/libs/${rootProject.name}-$version.jar"))
    into("$buildDir/dependency")
}

tasks.register<Exec>("buildDockerImage") {
    if (project.properties["onlyBuild"].toString().toBoolean()) {
        commandLine("docker", "build", ".")
    } else {
        commandLine("docker", "build", ".", "--tag", "$tempDockerTag")
    }
}

tasks.register("tagPushDockerImage") {
    val githubRef = project.properties["githubRef"]
    val finalDockerTag = githubRef?.toString()?.removePrefix("refs/tags/v") ?: "latest"

    doLast {

        exec {
            commandLine("docker", "tag", "$tempDockerTag", "$imageId:$finalDockerTag")
        }
        exec {
            commandLine("docker", "push", "$imageId:$finalDockerTag")
        }
    }
}

tasks.register<Exec>("deploy") {
    val githubRef = project.properties["githubRef"]
    val finalDockerTag = githubRef?.toString()?.removePrefix("refs/tags/v") ?: "latest"
    val containerName = if (githubRef == null) {
        "i-on-integration-staging"
    } else {
        "i-on-integration-production"
    }

    commandLine(
        "gcloud",
        "compute instances",
        "update-container",
        "$containerName",
        "--container-image",
        "$imageId:$finalDockerTag"
    )
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
