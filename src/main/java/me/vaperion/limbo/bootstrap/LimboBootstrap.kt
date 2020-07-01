package me.vaperion.limbo.bootstrap

import com.moandjiezana.toml.Toml
import me.vaperion.limbo.LimboServer
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object LimboBootstrap {

    @JvmStatic
    fun main(args: Array<String>) {
        println("Loading configuration...")
        val configFile = createConfiguration()
        val config = Toml().read(configFile)
        println("Initializing server...")
        LimboServer(config)
    }

    private fun createConfiguration(): File {
        val file = File("limbo.toml")
        if (file.exists()) return file
        Files.copy(LimboBootstrap::class.java.getResourceAsStream("/limbo.toml"), Paths.get(file.absolutePath))
        return file
    }

}