package org.prm.drica

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform