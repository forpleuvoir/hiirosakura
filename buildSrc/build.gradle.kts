plugins {
    id("groovy-gradle-plugin")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
}

repositories {
    mavenCentral()
}