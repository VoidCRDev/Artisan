plugins {
    `java-library`
    `version-catalog`
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

tasks.test {
    useJUnitPlatform()
}
