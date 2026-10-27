import com.modrinth.minotaur.dependencies.Dependency
import com.modrinth.minotaur.dependencies.DependencyType
import com.modrinth.minotaur.dependencies.ModDependency
import com.modrinth.minotaur.dependencies.VersionDependency
import moe.forpleuvoir.gradle.ModrinthDeps
import java.text.SimpleDateFormat
import java.util.*

plugins {
    alias(libs.plugins.fabricLoom).apply(false)
    alias(libs.plugins.neoforgedModDev).apply(false)
    alias(libs.plugins.minotaur).apply(false)
}

val time: String get() = SimpleDateFormat("yyyyMMdd").format(Date())

val gitHash: Provider<String> by lazy {
    providers.exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
    }.standardOutput.asText.map { it.trim() }
}

val versionWithGitHashAndBuildTime: String = "v$version.${gitHash.get()}.$time"

/** 指定更新日志文件中与 [version] 对应的条目正文；没有该版本条目时返回空串。 */
fun changelogSection(fileName: String, version: String): String {
    val log = file(fileName)
    if (!log.isFile) return ""
    val lines = log.readLines()
    val start = lines.indexOfFirst { it.trim() == "v$version" }
    if (start < 0) return ""
    return lines.drop(start + 1).takeWhile { it.isNotBlank() }.joinToString("\n").trim()
}

/** 上传用的更新日志：取英文条目，缺失时回退到中文条目。 */
fun changelogFor(version: String): String {
    val english = changelogSection("CHANGE_LOG-ENG.md", version)
    if (english.isNotEmpty()) return english
    val chinese = changelogSection("CHANGE_LOG.md", version)
    if (chinese.isNotEmpty()) logger.warn("CHANGE_LOG-ENG.md 缺少 v$version 条目，回退到 CHANGE_LOG.md")
    return chinese
}

/** 上传到 Modrinth 的依赖声明；[versionNumber] 为 null 时只声明项目级依赖。 */
data class ModrinthRequirement(
    val projectId: String,
    val slug: String,
    val versionNumber: String? = null,
    val type: DependencyType = DependencyType.REQUIRED,
)

/**
 * 构造 Modrinth 依赖：给出 [ModrinthRequirement.versionNumber] 时先解析成版本 ID 再声明版本级依赖，
 * 解析不到时退化为项目级依赖并告警。版本级依赖在 Modrinth 上是「不低于该版本」的要求，
 * 避免调用方装到旧版本运行时导致字节码不兼容。
 */
fun resolveModrinthDependency(
    project: Project,
    requirement: ModrinthRequirement,
    loader: String,
): Pair<ModrinthRequirement, Dependency> {
    val versionNumber = requirement.versionNumber
    if (versionNumber == null) {
        return requirement to ModDependency(requirement.projectId, requirement.type)
    }
    val override = project.providers.gradleProperty("modrinth.versionId.${requirement.slug}").orNull
    val versionId = override ?: runCatching {
        ModrinthDeps.resolveVersionId(requirement.slug, versionNumber, loader, 15000)
    }.onFailure {
        project.logger.warn("查询 ${requirement.slug} $versionNumber 的 Modrinth 版本 ID 失败：${it.message}")
    }.getOrNull()
    if (versionId == null) {
        project.logger.warn(
            "Modrinth 上没有 ${requirement.slug} $versionNumber（$loader）的版本记录，依赖退化为任意版本；" +
                    "可用 -Pmodrinth.versionId.${requirement.slug}=<版本 ID> 指定"
        )
    }
    val dependency: Dependency = if (versionId != null) {
        VersionDependency(requirement.projectId, versionId, requirement.type)
    } else {
        ModDependency(requirement.projectId, requirement.type)
    }
    return requirement to dependency
}

/** 各加载器在 Modrinth 上需要声明的前置依赖；运行时依赖由 IbukiGourd 自己声明，这里只钉它的版本。 */
fun modrinthRequirementsFor(loader: String): List<ModrinthRequirement> = when (loader) {
    "fabric", "neoforge" -> listOf(
        ModrinthRequirement("zT6MNo3I", "ibukigourd", libs.versions.ibukigourd.get()),
    )

    else                 -> emptyList()
}

subprojects {
    plugins.withId("com.modrinth.minotaur") {
        val requirements = modrinthRequirementsFor(project.name)
        fun resolvedDependencies(): List<Pair<ModrinthRequirement, Dependency>> =
            requirements.map { resolveModrinthDependency(project, it, project.name) }

        extensions.configure<com.modrinth.minotaur.ModrinthExtension> {
            val mcVersion = libs.versions.minecraft.get()
            val modVersion = project.version.toString()
            val loaderName = when (project.name) {
                "fabric"   -> "Fabric"
                "neoforge" -> "NeoForge"
                else       -> project.name.replaceFirstChar { it.uppercase() }
            }
            projectId.set("hiirosakura")
            versionNumber.set(modVersion)
            versionName.set("$modVersion $loaderName $mcVersion")
            versionType.set(
                when {
                    modVersion.contains("alpha", ignoreCase = true) -> "alpha"
                    modVersion.contains("beta", ignoreCase = true)  -> "beta"
                    else                                            -> "release"
                }
            )
            changelog.set(changelogFor(modVersion).ifEmpty { "CHANGE_LOG-ENG.md 中没有 v$modVersion 条目" })
            file.set(tasks.named<Jar>("jar").flatMap { it.archiveFile })
            gameVersions.add(mcVersion)
            loaders.add(project.name)
            token.convention(
                providers.gradleProperty("modrinth.token").orElse(providers.environmentVariable("MODRINTH_TOKEN"))
            )
        }

        val modrinthExtension = extensions.getByType(com.modrinth.minotaur.ModrinthExtension::class.java)

        tasks.named("modrinth") {
            doFirst {
                modrinthExtension.dependencies.set(resolvedDependencies().map { it.second })
                resolvedDependencies().forEach { (requirement, dependency) ->
                    val detail = if (dependency is VersionDependency) {
                        "固定版本 ${dependency.versionId}"
                    } else {
                        "任意版本"
                    }
                    logger.lifecycle("${project.name}: ${requirement.slug} ${requirement.versionNumber} -> $detail")
                }
                val hasToken = providers.gradleProperty("modrinth.token").isPresent ||
                        providers.environmentVariable("MODRINTH_TOKEN").isPresent
                if (!hasToken) {
                    throw GradleException(
                        "未配置 Modrinth 令牌：设置环境变量 MODRINTH_TOKEN，或在用户级 gradle.properties 里写 modrinth.token=<个人访问令牌>"
                    )
                }
            }
        }
    }
}

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
            ":common:publishModPublicationToSnapshotsRepository",
            ":fabric:publishModPublicationToSnapshotsRepository",
            ":neoforge:publishModPublicationToSnapshotsRepository"
        )
    }

    register("publishModToReleasesRepository") {
        dependsOn(
            ":common:publishModPublicationToReleasesRepository",
            ":fabric:publishModPublicationToReleasesRepository",
            ":neoforge:publishModPublicationToReleasesRepository"
        )
    }

    register("modrinthDependencies") {
        group = "publishing"
        description = "打印 fabric 与 neoforge 将要上传到 Modrinth 的依赖，不执行上传"
        doLast {
            listOf("fabric", "neoforge").forEach { loader ->
                val target = project(":$loader")
                modrinthRequirementsFor(loader).forEach { requirement ->
                    val (_, dependency) = resolveModrinthDependency(target, requirement, loader)
                    val detail = if (dependency is VersionDependency) {
                        "固定版本 ${dependency.versionId}"
                    } else {
                        "任意版本"
                    }
                    logger.lifecycle("$loader: ${requirement.slug} ${requirement.versionNumber} -> $detail")
                }
            }
        }
    }

    register("publishModToModrinth") {
        group = "publishing"
        description = "上传 Fabric 与 NeoForge 产物到 Modrinth"
        dependsOn(project(":fabric").tasks.named("modrinth"), project(":neoforge").tasks.named("modrinth"))
    }

    register("publishModToLocalRepository") {
        dependsOn(
            ":common:publishModPublicationToMavenLocal",
            ":fabric:publishModPublicationToMavenLocal",
            ":neoforge:publishModPublicationToMavenLocal"
        )
    }

    register<Copy>("buildAllModJar") {
        dependsOn(
            ":fabric:jar",
            ":neoforge:jar"
        )
        val minecraftVersion = libs.versions.minecraft.get()
        doFirst {
            val outputDir = File(project.rootDir, "modJar/$minecraftVersion/$version")
            outputDir.mkdirs()
        }

        from(project(":fabric").tasks.named<AbstractArchiveTask>("jar").get().archiveFile) {
            rename { "${project.name}-fabric-$versionWithGitHashAndBuildTime-minecraft.$minecraftVersion.jar" }
        }
        from(project(":neoforge").tasks.named<AbstractArchiveTask>("jar").get().archiveFile) {
            rename { "${project.name}-neoforge-$versionWithGitHashAndBuildTime-minecraft.$minecraftVersion.jar" }
        }
        into(file("modJar/$minecraftVersion/$version"))
    }
}