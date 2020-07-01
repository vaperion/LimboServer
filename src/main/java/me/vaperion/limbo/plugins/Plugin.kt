package me.vaperion.limbo.plugins

import java.io.File

class Plugin(val file : File, val name : String) {
    private var enabled: Boolean = false

    fun isEnabled(): Boolean = enabled
}