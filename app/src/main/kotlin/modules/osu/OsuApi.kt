package com.osucad.server.modules.osu

import com.osucad.osuapi.OsuApiV2

fun interface OsuApiFactory {
    fun create(accessToken: String): OsuApiV2

    object Default : OsuApiFactory {
        override fun create(accessToken: String) = OsuApiV2(accessToken)
    }
}
