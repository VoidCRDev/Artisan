plugins {
    id("artisan-common")
}

group = rootProject.group
version = rootProject.version

dependencies {
    api(project(":artisan-core"))
    testImplementation(project(":artisan-core", "test"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
