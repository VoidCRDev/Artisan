plugins {
    `java-library`
    id("artisan-common")
}

group = rootProject.group
version = rootProject.version

dependencies {
    api(project(":artisan-core"))
    testImplementation(project(":artisan-core", "test"))
}
