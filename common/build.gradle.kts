plugins {
    id("multiloader-common")
    alias(libs.plugins.neoforgedModDev)
}

neoForge {
    neoFormVersion = libs.versions.neoForm.get()
    val at = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }
}

dependencies {
    compileOnly(libs.bundles.kotlin)

    compileOnly(libs.mixin)
    compileOnly(libs.mixinExtras.common)
    annotationProcessor(libs.mixinExtras.common)

    compileOnly(libs.bundles.jexl)

    implementation(libs.ibukigourd.common)
}

sourceSets {
    create("devOnly") {
        compileClasspath += main.get().compileClasspath + main.get().output
    }
}

configurations {
    create("commonJava") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    create("commonKotlin") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    create("commonResources") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
}

artifacts {
    add("commonJava", sourceSets.main.get().java.sourceDirectories.singleFile)
    add("commonKotlin", sourceSets.main.get().kotlin.sourceDirectories.filter { !it.name.endsWith("java") }.singleFile)
    add("commonResources", file("src/main/resources"))
}

val loaderAttribute = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)
listOf<String>(
    "apiElements", "runtimeElements", "sourcesElements"
).forEach {
    configurations.named(it) {
        attributes {
            attribute(loaderAttribute, "common")
        }
    }
}

sourceSets.configureEach {
    listOf(compileClasspathConfigurationName, runtimeClasspathConfigurationName).forEach {
        configurations.named(it) {
            attributes {
                attribute(loaderAttribute, "common")
            }
        }
    }
}