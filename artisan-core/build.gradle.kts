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

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.api)

    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.launcher)
}

tasks.test {
    useJUnitPlatform()
}

configurations {
    create("test")
}

tasks.register<Jar>("buildTestJar") {
    archiveAppendix = "test"
    from(project.the<SourceSetContainer>()["test"].output)
}

artifacts {
    add("test", tasks["buildTestJar"])
}
