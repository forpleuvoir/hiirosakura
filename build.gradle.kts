import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

plugins {
    alias(libs.plugins.fabricLoom).apply(false)
    alias(libs.plugins.neoforgedModDev).apply(false)
}

val time: String get() = SimpleDateFormat("yyyyMMdd").format(Date())

val gitHash: String by lazy {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "--short", "HEAD") // 获取短哈希值
        standardOutput = stdout
    }
    stdout.toString().trim()
}

val versionWithGitHashAndBuildTime: String = "v$version.$gitHash.$time"

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
            ":fabric:remapJar",
            ":neoforge:jar"
        )
        val minecraftVersion = libs.versions.minecraft.get()
        doFirst {
            val outputDir = File(project.rootDir, "modJar/$minecraftVersion")
            outputDir.mkdirs()
        }

        from(project(":fabric").tasks.named<AbstractArchiveTask>("remapJar").get().archiveFile) {
            rename { "${project.name}-fabric-$versionWithGitHashAndBuildTime-minecraft.$minecraftVersion.jar" }
        }
        from(project(":neoforge").tasks.named<AbstractArchiveTask>("jar").get().archiveFile) {
            rename { "${project.name}-neoforge-$versionWithGitHashAndBuildTime-minecraft.$minecraftVersion.jar" }
        }
        into(file("modJar/$minecraftVersion"))
    }
}