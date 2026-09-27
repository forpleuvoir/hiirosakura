@file:Suppress("UnstableApiUsage")

import java.util.*
import kotlin.random.Random

plugins {
    id("multiloader-loader")
    alias(libs.plugins.fabricLoom)
}

val modId: String = project.findProperty("mod_id").toString()

repositories {
    maven {
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com/")
    }
}

dependencies {
    minecraft(libs.minecraft)

    //Fabric
    implementation(libs.fabricLoader)
    implementation(libs.fabricApi)

    implementation(libs.fabricKotlin)
    implementation(libs.modMenu)

    libs.bundles.jexl.let {
        implementation(it)
        include(it)
    }

    implementation(libs.ibukigourd.fabric)
}

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

loom {
    val aw = project(":common").file("src/main/resources/${modId}.classtweaker")
    if (aw.exists()) {
        accessWidenerPath.set(aw)
    }

    runs {
        named("client") {
            client()
            displayName = "Fabric Client"
            generateRunConfig = true
            runDirectory.set(File("runs/client"))
            val name: String = System.getenv("mcName") ?: "Dev${Random.nextInt(1000)}"
            val uuid: String = System.getenv("mcUUID") ?: UUID.randomUUID().toString()
            programArguments.addAll("--username", name, "--uuid", uuid)
            sourceSet = "devOnly"
        }
        named("server") {
            server()
            displayName = "Fabric Server"
            generateRunConfig = true
            runDirectory.set(File("runs/server"))
        }
    }
}

val loaderAttribute = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)
listOf<String>(
    "apiElements", "runtimeElements", "sourcesElements", "includeInternal", "modCompileClasspath"
).forEach {
    configurations.named(it) {
        attributes {
            attribute(loaderAttribute, "fabric")
        }
    }
}

sourceSets.configureEach {
    listOf(compileClasspathConfigurationName, runtimeClasspathConfigurationName).forEach {
        configurations.named(it) {
            attributes {
                attribute(loaderAttribute, "fabric")
            }
        }
    }
}
