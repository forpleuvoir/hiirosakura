@file:Suppress("UnstableApiUsage")

import java.util.*
import kotlin.random.Random

plugins {
    id("multiloader-loader")
    alias(libs.plugins.fabricLoom)
}

val modId: String = project.properties["mod_id"].toString()

repositories {
    maven {
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com/")
    }
    maven { url = uri("https://maven.forpleuvoir.moe/snapshots") }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${libs.versions.parchmentMinecraft.get()}:${libs.versions.parchment.get()}@zip")
    })
    modImplementation(libs.fabricLoader)
    modImplementation(libs.fabricApi)

    modImplementation(libs.fabricKotlin)
    modImplementation(libs.modMenu)

    compileOnly(libs.nebula)
    implementation(libs.nashorn)
    include(libs.nashorn)

    modImplementation(libs.ibukigourd.fabric)
}

sourceSets {
    create("devClientTest") {
        val test = project(":common").sourceSets["devClientTest"]
        compileClasspath += main.get().compileClasspath + main.get().output + test.compileClasspath + test.output
        runtimeClasspath += main.get().runtimeClasspath + main.get().output + test.runtimeClasspath + test.output
    }
}

loom {
    val aw = project(":common").file("src/main/resources/${modId}.accesswidener")
    if (aw.exists()) {
        accessWidenerPath.set(aw)
    }

    mixin {
        defaultRefmapName.set("${modId}.refmap.json")
    }
    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("runs/client")
            val name: String = System.getenv("mcName") ?: "Dev${Random.nextInt(1000)}"
            val uuid: String = System.getenv("mcUUID") ?: UUID.randomUUID().toString()
            programArgs("--username", name, "--uuid", uuid)
            source(sourceSets["devClientTest"])
        }
        named("server") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
            runDir("runs/server")
        }
    }
}