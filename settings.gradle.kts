pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CleanCityApp"
include(":app")

// DevToolSDK is a separate repo. Prefer the published Maven artifact for host builds so
// AGP versions do not need to match across composite builds.
// To develop against a local SDK checkout with includeBuild, both projects must use the
// same AGP version (see DevToolSDK/gradle/libs.versions.toml).
//
// includeBuild("../DevToolSDK") {
//     dependencySubstitution {
//         substitute(module("io.github.krishnapensalwar:devkit"))
//             .using(project(":devtool"))
//     }
// }
