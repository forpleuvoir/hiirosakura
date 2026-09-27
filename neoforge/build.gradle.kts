import org.gradle.internal.extensions.stdlib.capitalized
import java.util.*
import kotlin.random.Random

plugins {
    id("multiloader-loader")
    alias(libs.plugins.neoforgedModDev)
}

val modId: String = project.properties["mod_id"].toString()

sourceSets {
    create("devOnly") {
        // 只取 :common devOnly 源集的产物，不要把它自己的 configuration（devOnlyCompileClasspath /
        // devOnlyRuntimeClasspath）拼进来：Gradle 9 在并行构建下禁止执行期解析别的 project 的
        // configuration，runClient 解析 classpath 时会直接失败：
        //   Resolution of the configuration ':common:devOnlyRuntimeClasspath' was attempted
        //   without an exclusive lock. This is unsafe and not allowed.
        // :common 的 devOnly 只在本模块 main 的 classpath 之上追加自己的输出，依赖与 main 一致，
        // 而这些依赖已经在 loader 模块 main 的 classpath 上，因此这里引产物即可。
        val commonDevOnly = project(":common").sourceSets["devOnly"]
        compileClasspath += main.get().compileClasspath + main.get().output + commonDevOnly.output
        runtimeClasspath += main.get().runtimeClasspath + main.get().output + commonDevOnly.output
    }
}

neoForge {
    version = libs.versions.neoforge.get()
    // Automatically enable neoforge AccessTransformers if the file exists
    val at = project(":common").file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }
    runs {
        configureEach {
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            ideName = "NeoForge ${name.capitalized()} (${project.path})" // Unify the run config names with fabric
        }
        register("client") {
            client()
            val name: String = System.getenv("mcName") ?: "Dev${Random.nextInt(1000)}"
            val uuid: String = System.getenv("mcUUID") ?: UUID.randomUUID().toString()
            programArguments.addAll("--username", name, "--uuid", uuid)
            gameDirectory = file("runs/client")
            //好像并没有作用
//            sourceSet = sourceSets["devOnly"]
        }
        register("data") {
            clientData()
            programArguments.addAll(
                "--mod",
                modId,
                "--all",
                "--output",
                file("src/generated/resources/").absolutePath,
                "--existing",
                file("src/main/resources/").absolutePath
            )
        }
        register("server") {
            server()
            gameDirectory = file("runs/server")
        }
    }
    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main.get().resources { srcDir("src/generated/resources") }

val loaderAttribute = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)

listOf("apiElements", "runtimeElements", "sourcesElements").forEach {
    configurations.named(it) {
        attributes {
            attribute(loaderAttribute, "neoforge")
        }
    }
}

sourceSets.configureEach {
    listOf(compileClasspathConfigurationName, runtimeClasspathConfigurationName, getTaskName(null, "jarJar")).forEach {
        configurations.named(it) {
            attributes {
                attribute(loaderAttribute, "neoforge")
            }
        }
    }
}

dependencies {
    implementation(libs.forgeKotlin)

    libs.bundles.jexl.let {
        implementation(it)
        jarJar(it)
    }

    implementation(libs.ibukigourd.neoforge)
}