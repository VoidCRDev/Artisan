plugins {
    `java-library`
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    api(libs.jspecify)
    api(project(":artisan-core"))

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.api)
    testImplementation(project(":artisan-core", "test"))

    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.launcher)
}

tasks.test {
    useJUnitPlatform()
}
