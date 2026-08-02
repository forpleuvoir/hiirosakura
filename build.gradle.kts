import java.text.SimpleDateFormat
import java.util.*

plugins {
    alias(libs.plugins.fabricLoom).apply(false)
    alias(libs.plugins.neoforgedModDev).apply(false)
}

val time: String get() = SimpleDateFormat("yyyyMMdd").format(Date())

val gitHash: Provider<String> by lazy {
    providers.exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
    }.standardOutput.asText.map { it.trim() }
}

val versionWithGitHashAndBuildTime: String = "v$version.${gitHash.get()}.$time"

run {
    //只是为了生成一个toml方便https://shields.io/读取
    val outputDir: File = project.rootDir

    outputDir.mkdirs()
    val tomlContent = """
    [mod_info]
    name = "${project.findProperty("mod_name")}"
    id = "${project.findProperty("mod_id")}"
    version = "$version"
    minecraft_version = "${libs.versions.minecraft.get()}"
""".trimIndent()

    val tomlFile = File(outputDir, "${project.name}.info.toml")
    tomlFile.writeText(tomlContent)
}

tasks {
    register("publishModToSnapshotsRepository") {
        dependsOn(
            ":common:publishMavenJavaPublicationToSnapshotsRepository",
            ":fabric:publishMavenJavaPublicationToSnapshotsRepository",
            ":neoforge:publishMavenJavaPublicationToSnapshotsRepository"
        )
    }

    register("publishModToReleasesRepository") {
        dependsOn(
            ":common:publishMavenJavaPublicationToReleasesRepository",
            ":fabric:publishMavenJavaPublicationToReleasesRepository",
            ":neoforge:publishMavenJavaPublicationToReleasesRepository"
        )
    }

    register("publishModToLocalRepository") {
        dependsOn(
            ":common:publishMavenJavaPublicationToMavenLocal",
            ":fabric:publishMavenJavaPublicationToMavenLocal",
            ":neoforge:publishMavenJavaPublicationToMavenLocal"
        )
    }

    register<Copy>("buildAllModJar") {
        dependsOn(
            ":fabric:jar",
            ":neoforge:jar"
        )
        val minecraftVersion = libs.versions.minecraft.get()
        doFirst {
            val outputDir = File(project.rootDir, "modJar/$minecraftVersion")
            outputDir.mkdirs()
        }

        from(project(":fabric").tasks.named<AbstractArchiveTask>("jar").get().archiveFile) {
            rename { "${project.name}-fabric-$versionWithGitHashAndBuildTime-minecraft.$minecraftVersion.jar" }
        }
        from(project(":neoforge").tasks.named<AbstractArchiveTask>("jar").get().archiveFile) {
            rename { "${project.name}-neoforge-$versionWithGitHashAndBuildTime-minecraft.$minecraftVersion.jar" }
        }
        into(file("modJar/$minecraftVersion"))
    }
}