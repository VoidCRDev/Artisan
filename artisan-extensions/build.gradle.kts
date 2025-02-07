plugins {
    id("artisan-common")
}

group = rootProject.group
version = rootProject.version

dependencies {
    api(project(":artisan-format"))
    api(project(":artisan-core"))
    api(libs.asm.tree)
    api(libs.asm)

    testImplementation(project(":artisan-core", "test"))
}
