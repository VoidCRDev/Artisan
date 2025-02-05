plugins {
    `java-library`
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    api(project(":artisan-format"))
    api(project(":artisan-core"))
    api(libs.asm)
    api(libs.asm.tree)
    api(libs.jspecify)

    testImplementation(project(":artisan-core", "test"))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.api)

    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.launcher)
}

tasks.test {
    useJUnitPlatform()
}
