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
    parchment {
        minecraftVersion = libs.versions.parchmentMinecraft
        mappingsVersion = libs.versions.parchment
    }
}

repositories{
    maven { url = uri("https://maven.forpleuvoir.moe/snapshots") }
}

dependencies {
    compileOnly(libs.bundles.kotlin)
    compileOnly(libs.mixin)
    compileOnly(libs.minxinExtras.common)
    compileOnly(libs.nebula)

    compileOnly(libs.bundles.jexl)

    annotationProcessor(libs.minxinExtras.common)

    implementation(libs.ibukigourd.common)
}

sourceSets {
    create("devClientTest") {
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
    add("commonResources", sourceSets.main.get().resources.sourceDirectories.singleFile)
}
