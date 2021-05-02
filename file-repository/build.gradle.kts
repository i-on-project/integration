plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
}

group = "org.ionproject"
version = "0.1"

tasks.bootJar { enabled = false }

tasks.withType<Jar> {
    enabled = true
}
dependencies {
    compile(project(":app"))
    implementation(project(":app"))
    implementation(kotlin("stdlib"))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
