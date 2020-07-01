package me.vaperion.limbo.plugins

import me.vaperion.limbo.LimboServer
import java.util.concurrent.ConcurrentHashMap

class PluginManager(val server : LimboServer) {

    private val pluginMap: MutableMap<String, Plugin> = ConcurrentHashMap()

    fun loadPlugins() {

    }

    fun getPlugins(): List<Plugin> = pluginMap.values.toList()
}