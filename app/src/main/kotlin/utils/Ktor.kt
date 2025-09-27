package com.osucad.server.utils

import io.ktor.server.plugins.*

fun <T> T?.orNotFound(): T = this ?: throw NotFoundException()