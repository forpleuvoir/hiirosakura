pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        //Fabric
        exclusiveContent {
            forRepository {
                maven {
                    name = "Fabric"
                    url = uri("https://maven.fabricmc.net")
                }
            }
            filter {
                includeGroup("net.fabricmc")
                includeGroup("net.fabricmc.unpick")
                includeGroup("fabric-loom")
            }
        }
        exclusiveContent {
            forRepository {
                maven {
                    name = "Sponge"
                    url = uri("https://repo.spongepowered.org/repository/maven-public")
                }
            }
            filter {
                includeGroupAndSubgroups("org.spongepowered")
            }
        }
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://www.jitpack.io") }
        maven { url = uri("https://maven.forpleuvoir.moe/snapshots") }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention").version("0.8.0")
}


rootProject.name = "hiirosakura"

include("common")
include("fabric")
include("neoforge")