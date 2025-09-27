@file:OptIn(ExperimentalSerializationApi::class)

package com.osucad.osuapi

import com.osucad.osuapi.models.OsuApiUser
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy.Builtins.SnakeCase

interface OsuApiV2 {
    @GET("me")
    suspend fun me(): OsuApiUser

    @GET("users/{user}")
    suspend fun getUser(@Path user: Int): OsuApiUser
}

fun OsuApiV2(
    accessToken: String,
    baseUrl: String = "https://osu.ppy.sh/api/v2/",
    engine: HttpClientEngine = CIO.create(),
): OsuApiV2 {
    val ktorfit = ktorfit {
        baseUrl(baseUrl)
        httpClient(engine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    namingStrategy = SnakeCase
                })
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(accessToken, null)
                    }
                }
            }
        }
    }

    return ktorfit.createOsuApiV2()
}