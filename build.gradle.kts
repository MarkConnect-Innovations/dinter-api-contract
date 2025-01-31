import org.gradle.api.publish.maven.MavenPublication
import org.openapitools.generator.gradle.plugin.tasks.ValidateTask
import java.util.Locale

plugins {
    id("java")
    `maven-publish`
    id("org.openapi.generator") version "7.2.0"
}

group = "com.dinter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

val taskNames = mutableListOf<String>()
val specFiles = fileTree("$rootDir/src/main/resources/api")
    .matching { include("**/*.yaml") }

specFiles.forEach { specFile ->
    val taskName = "openApiValidate" + specFile.nameWithoutExtension.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
    tasks.register(taskName, ValidateTask::class) {
        inputSpec.set(specFile.absolutePath)
    }
    taskNames.add(taskName)
}
tasks.register("openApiValidateAll") { dependsOn(taskNames) }

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks.build {
    dependsOn("openApiValidateAll")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}