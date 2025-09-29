plugins {
    id("buildsrc.convention.kotlin-jvm")

    alias(libs.plugins.kotlin.serialization)
    alias(ktorLibs.plugins.ktor)
    alias(libs.plugins.mappie)
    alias(libs.plugins.kotest)
}

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

sourceSets {
    create("scripts") {
        kotlin {
            srcDir("src/srcipts/kotlin")
        }

        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

configurations {
    named("scriptsImplementation") {
        extendsFrom(configurations.implementation.get())
    }
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
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.cio)
    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.logback)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.migrations.core)
    implementation(libs.exposed.migrations.jdbc)
    implementation(libs.db.driver.h2)
    implementation(libs.db.driver.postgres)
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)
    implementation(libs.mappie)
    implementation(libs.hikaricp)

    testImplementation(libs.bundles.test.ktor)
    testImplementation(ktorLibs.server.testHost)

    sourceSets.named("scripts") {
        implementation(libs.testcontainers.core)
        implementation(libs.testcontainers.postgres)
    }
}

tasks.named("buildOpenApi") {
    enabled = false
}

kotlin {
    compilerOptions {
        optIn.addAll(
            "org.jetbrains.exposed.v1.core.ExperimentalDatabaseMigrationApi"
        )
    }
}
