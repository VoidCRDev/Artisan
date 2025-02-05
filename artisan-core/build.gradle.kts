plugins {
    id("artisan-common")
}

group = rootProject.group
version = rootProject.version

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
