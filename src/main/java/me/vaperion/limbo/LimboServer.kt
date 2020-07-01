package me.vaperion.limbo

import com.moandjiezana.toml.Toml
import me.vaperion.limbo.network.ClientState
import me.vaperion.limbo.network.packet.clientbound.play.PacketOutChat
import me.vaperion.limbo.player.Player
import me.vaperion.limbo.plugins.PluginManager
import me.vaperion.limbo.utils.EncryptionUtils
import me.vaperion.limbo.utils.serializable.Chat
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import javax.script.Bindings
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class LimboServer(configuration: Toml) {

    companion object {
        lateinit var SERVER: LimboServer

        val SERVER_KEYPAIR = EncryptionUtils.createRsaKeyPair(1024)
        val PACKET_DEBUG_MODE = false

        val EXECUTOR = Executors.newScheduledThreadPool(2)
    }

    /**
     * Defines variables required for the server.
     */
    private lateinit var socket: ServerSocket
    private lateinit var pluginManager: PluginManager
    private var running: Boolean = false
    val players: MutableMap<Player, Socket> = ConcurrentHashMap()

    /**
     * Defines variables for the Nashorn plugin engine.
     */
    private val scriptEngine: ScriptEngine = ScriptEngineManager().getEngineByName("nashorn")
    private val nashornBindings: Bindings = scriptEngine.createBindings()

    /**
     * Defines variables from the limbo.toml configuration file.
     */
    private val bindIp: String = configuration.getString("ip")
    private val bindPort: Int = configuration.getLong("port").toInt()
    val motd: String = configuration.getString("motd")

    val actionBarText: String = configuration.getString("actionBarText")

    val hasJoinedUrl: String = configuration.getString("hasJoinedUrl")
    val preventProxyConnections: Boolean = configuration.getBoolean("preventProxyConnections")
    val logPings: Boolean = configuration.getBoolean("logPings")
    val onlineMode: Boolean = configuration.getBoolean("onlineMode")

    init {
        SERVER = this

        initializeServer()
        initializeNashorn()
        initializePlugins()
        startServer()
    }

    private fun initializeServer() {
        pluginManager = PluginManager(this)
    }

    private fun initializeNashorn() {
        nashornBindings["server"] = this
        nashornBindings["pluginManager"] = pluginManager
    }

    private fun initializePlugins() {
        pluginManager.loadPlugins()
    }

    private fun startServer() {
        socket = ServerSocket(bindPort, Short.MAX_VALUE.toInt(), InetAddress.getByName(bindIp))

        running = true

        while (running) {
            val connection = socket.accept()

            val player = Player(connection, null, null, ClientState.HANDSHAKE)
            players[player] = connection
        }
    }

    fun broadcastMessage(str: String) {
        getOnlinePlayers().forEach { it.sendPacket(PacketOutChat(Chat(str), 0)) }
    }

    fun getOnlinePlayers(): List<Player> = players.keys.filter { it.state == ClientState.PLAY }.toList()

}