import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

plugins {
    java
    signing
    alias(libs.plugins.fabricLoom)
    alias(libs.plugins.kotlinJVM)
    id("maven-publish")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://www.jitpack.io") }
    maven { url = uri("https://maven.terraformersmc.com/") }
//	maven { url = uri("https://maven.forpleuvoir.moe/releases") }
    maven { url = uri("https://maven.forpleuvoir.moe/snapshots") }
    maven {
        url = uri("https://maven.latvian.dev/releases")
        content {
            includeGroup("dev.latvian.mods")
        }
    }
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

val time: String get() = SimpleDateFormat("yyyyMMdd").format(Date())

val gitHash: String by lazy {
    val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
        .redirectErrorStream(true)
        .start()

    process.inputStream.bufferedReader().use { it.readText().trim() }
}

val modName: String = properties["archives_base_name"].toString()
version = libs.versions.modVersion.get().toString()
group = properties["maven_group"].toString()

dependencies {
    fun implementationAndInclude(dependencyNotation: Any) {
        implementation(dependencyNotation)
        include(dependencyNotation)
    }

    minecraft(libs.minecraft)
    mappings("${libs.yarnMappings.get()}:v2")
    modImplementation(libs.fabricLoader)
    modImplementation(libs.fabricApi)

    modImplementation(libs.fabricKotlin)

    //其他mod依赖
    modImplementation(libs.modMenu)
    modImplementation(libs.ibukigourd)
    //兼容测试
    modImplementation("maven.modrinth", "sodium", "mc1.21.4-0.6.3-fabric")
    modImplementation("maven.modrinth", "iris", "1.8.5+1.21.4-fabric")
    implementation("org.anarres:jcpp:1.4.14")
    implementation("org.antlr:antlr4-runtime:4.13.1")
    implementation("io.github.douira:glsl-transformer:2.0.1")


    //nebula
    implementation(libs.nebula)

    //其他第三方库依赖
    implementationAndInclude(libs.nashorn)

    //test
    testImplementation(kotlin("test"))
}

loom {
    splitEnvironmentSourceSets()
    mods {
        create(modName) {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets["client"])
        }
    }
    accessWidenerPath.set(file("src/main/resources/hiirosakura.accesswidener"))
}

sourceSets {
    val devClient = create("devClientTest") {
        compileClasspath += main.get().compileClasspath + main.get().output + sourceSets["client"].compileClasspath + sourceSets["client"].output
        runtimeClasspath += main.get().runtimeClasspath + main.get().output + sourceSets["client"].runtimeClasspath + sourceSets["client"].output
    }
    named("test") {
        compileClasspath += devClient.compileClasspath + devClient.output
        runtimeClasspath += devClient.runtimeClasspath + devClient.output
    }
}

loom {
    runs {
        create("clientTest") {
            val name: String = System.getenv("mcName") ?: "Dev${Random.nextInt(1000)}"
            val uuid: String = System.getenv("mcUUID") ?: UUID.randomUUID().toString()
            programArgs("--username", name, "--uuid", uuid)
            client()
            name("ClientTest")
            ideConfigGenerated(true)
            source(sourceSets["devClientTest"])
        }
    }
}

java {
    withSourcesJar()
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
//        apiVersion.set(KotlinVersion.KOTLIN_2_0)
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

tasks {

    processResources {
        inputs.property("version", version)
        filteringCharset = "UTF-8"
        filesMatching("fabric.mod.json") {
            expand("version" to version)
        }
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(21)
        targetCompatibility = JavaVersion.VERSION_21.toString()
        sourceCompatibility = JavaVersion.VERSION_21.toString()
    }

    named<JavaCompile>("compileClientJava") {
        dependsOn("compileJava")
    }
    named<JavaCompile>("compileDevClientTestJava") {
        dependsOn("compileClientJava")
    }

    named<KotlinCompile>("compileClientKotlin") {
        dependsOn("compileKotlin")
    }
    named<KotlinCompile>("compileDevClientTestKotlin") {
        dependsOn("compileClientKotlin")
    }

    jar {
        from("LICENSE") {
            rename { "${it}_$modName" }
        }
    }

    register<Copy>("modJar") {
        dependsOn(remapJar)
        mustRunAfter(remapJar)
        val outPath = "$rootDir/modJar/$version"
        val name = remapJar.get().archiveFileName.get()
        val newName = "$modName-$version.$gitHash.$time-minecraft.${libs.versions.minecraftVersion.get()}-fabric.jar"
        from("build/libs")
        into(outPath)
        include(name)
        doLast {
            delete("$outPath/$newName")
            file("$outPath/$name").renameTo(file("$outPath/$newName"))
        }
    }

}

publishing {
    //https://reposilite.com/guide/gradle
    repositories {
        maven {
            name = "releases"
            url = uri("https://maven.forpleuvoir.moe/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
        maven {
            name = "snapshots"
            url = uri("https://maven.forpleuvoir.moe/snapshots")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>(project.name) {
            artifact(tasks.remapJar)
            artifact(tasks.remapSourcesJar)
            pom {
                name.set(project.name)
                description.set("forpleuvoir的minecraft客户端增强mod")
                url.set("https://github.com/forpleuvoir/ibuki_gourd")
                licenses {
                    license {
                        name.set("GNU General Public License, version 3 (GPLv3)")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("forpleuvoir")
                        name.set("forpleuvoir")
                        email.set("forpleuvoir@gmail.com")
                    }
                }
            }
        }
    }
}