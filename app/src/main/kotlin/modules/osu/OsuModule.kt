package com.osucad.server.modules.osu

import io.ktor.server.application.*
import io.ktor.server.config.property
import io.ktor.server.plugins.di.*

fun Application.osuModule(
    oauthConfig: OAuthConfig = property("oauth.osu")
) {
    dependencies {
        provide<OsuApiFactory> { OsuApiFactory.Default }
    }

    configureOsuOauth(oauthConfig)
}
