package com.osucad.osuapi.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class OsuApiUser(
    val id: Int,
    val username: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
)