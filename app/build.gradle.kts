plugins {
    id("buildsrc.convention.kotlin-jvm")

    alias(libs.plugins.kotlinPluginSerialization)
    alias(ktorLibs.plugins.ktor)
    alias(libs.plugins.mappie)
}

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}


dependencies {
    implementation(project(":libs:osu-api"))

    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.cors)
    implementation(ktorLibs.server.di)
    implementation(ktorLibs.server.auth)
    implementation(ktorLibs.server.sessions)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.compression)
    implementation(ktorLibs.server.autoHeadResponse)
    implementation(ktorLibs.server.callLogging)
    implementation(ktorLibs.server.callId)
    implementation(ktorLibs.server.rateLimit)
    implementation(ktorLibs.server.statusPages)
    implementation(ktorLibs.server.metrics.micrometer)
    implementation(libs.micrometer.registry.prometheus)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.cio)
    implementation(libs.logback)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.r2dbc)
    implementation(libs.exposed.migrations.core)
    implementation(libs.exposed.migrations.jdbc)
    implementation(libs.h2)
    implementation(libs.flyway)
    implementation(libs.mappie)

    testImplementation(ktorLibs.server.testHost)
    testImplementation(ktorLibs.client.mock)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotest.frameworkEngine)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.assertions.ktor)
}
