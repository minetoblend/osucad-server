package com.osucad.server.modules.documents

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*

fun Application.documentsModule() {
    dependencies {
        provide<IDocumentService>(DocumentService::class)
        provide<IDocumentSnapshotService>(DocumentSnapshotService::class)
    }

    configureDocumentSecurity()
    documentsRouting()
}
