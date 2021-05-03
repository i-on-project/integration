plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.jlleitschuh.gradle.ktlint")
}
val tempDockerTag: String = "i-on-integration-image"

group = "org.ionproject"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11

tasks.register<Copy>("extractUberJar") {
    dependsOn("build")
    dependsOn("test")
    dependsOn("ktlintCheck")
    from(zipTree("$buildDir/libs/${project.name}-$version.jar"))
    into("$buildDir/dependency")
}

tasks.register<Exec>("buildDockerImage") {
    if (project.properties["onlyBuild"].toString().toBoolean()) {
        commandLine("docker", "build", "../.")
    } else {
        commandLine("docker", "build", "../.", "--tag", tempDockerTag)
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-mail:2.3.10.RELEASE")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("technology.tabula:tabula:1.0.4") {
        exclude(group = "org.slf4j")
    }
    implementation("com.itextpdf:kernel:7.1.15")
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.3")
    runtimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.batch:spring-batch-test:4.2.6.RELEASE")
    testImplementation("com.icegreen:greenmail:1.6.3")
    testImplementation("org.apache.commons:commons-email:1.5")
}
