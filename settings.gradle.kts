rootProject.name = "integration"
include("app", "file-repository")

pluginManagement {
    plugins {
        val kotlinVersion: String by settings
        val springBootVersion: String by settings
        val springVersion: String by settings
        val ktlintVersion: String by settings

        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintVersion
    }
}
