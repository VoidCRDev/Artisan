plugins {
    `java-library`
    `version-catalog`
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    val libs = versionCatalogs.named("libs")
    api(libs.findLibrary("jspecify").get())

    testImplementation(platform(libs.findLibrary("junit-bom").get()))
    testImplementation(libs.findLibrary("junit-api").get())
    testRuntimeOnly(libs.findLibrary("junit-engine").get())
    testRuntimeOnly(libs.findLibrary("junit-launcher").get())
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven("https://maven.miles.sh/snapshots") {
            credentials {
                username = System.getenv("REPO_USERNAME")
                password = System.getenv("REPO_PASSWORD")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group as String
            from(components["java"])
        }
    }
}
