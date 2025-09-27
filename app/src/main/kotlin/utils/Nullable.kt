package com.osucad.server.utils

fun <T> T?.expectNotNull(message: String): T = this ?: throw NullPointerException(message)
