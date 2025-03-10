rootProject.name = "Artisan"

gradle.rootProject {
    group = "sh.miles.artisan"
    version = "1.1.0-SNAPSHOT"
}

include("artisan-core", "artisan-format", "artisan-extensions")
