package me.vaperion.limbo.player

import me.vaperion.limbo.LimboServer
import me.vaperion.limbo.handler.AbstractHandler
import me.vaperion.limbo.handler.impl.InitialHandler
import me.vaperion.limbo.handler.impl.LoginHandler
import me.vaperion.limbo.handler.impl.PlayHandler
import me.vaperion.limbo.handler.impl.StatusHandler
import me.vaperion.limbo.network.ClientState
import me.vaperion.limbo.network.packet.Packet
import me.vaperion.limbo.network.packet.PacketDirection
import me.vaperion.limbo.network.packet.PacketRegistry
import me.vaperion.limbo.network.packet.clientbound.login.PacketOutDisconnect
import me.vaperion.limbo.utils.PacketBuffer
import me.vaperion.limbo.utils.PacketSerializer
import me.vaperion.limbo.utils.serializable.Chat
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.SocketException
import java.util.*
import kotlin.concurrent.thread
import kotlin.experimental.and


class Player(private val socket: Socket, var uniqueId: UUID?, var name: String?, var state: ClientState) {
    var playerHandler: AbstractHandler = InitialHandler(this)

    /**
     * Defines variables related to the client.
     */
    var protocolVersion = 0

    /**
     * Defines variables required for the server <-> client connection.
     */
    var connected: Boolean = true
        private set
    private var socketInput: InputStream = socket.getInputStream()
    private var socketOutput: OutputStream = socket.getOutputStream()

    private val packetQueue: Queue<Packet> = LinkedList()
    private var packetSerializer: PacketSerializer? = null
    private var closingChannel: Boolean = false

    init {
        // Thread that receives and parses packets from the client
        thread {
            val buffer = ByteArray(4096) // Allocate a byte buffer of 4096 bytes

            while (connected) {
                try {
                    val totalLength = readPacketSize(socketInput)
                    socketInput.read(buffer, 0, totalLength)
                    val bytes = buffer.clone().sliceArray(IntRange(0, totalLength))
                    val buf = PacketBuffer(buffer)

                    /*
                    Without compression
                    Field Name	      Field Type	      Notes
                    Length	          VarInt	          Length of Packet ID + Data
                    Packet ID	      VarInt
                    Data	          Byte Array	      Depends on the connection state and packet ID, see the sections below
                    */

                    val packetId = buf.readVarInt()
                    val packetData = buf.readAllBytes()

                    val packet = PacketRegistry.getPacketByID(state, PacketDirection.SERVERBOUND, packetId)

                    if (packet != null) {
                        packet.readPacketData(PacketBuffer(packetData))
                        if (LimboServer.PACKET_DEBUG_MODE) println("[C -> S]: Read packet ${packet::class.java.simpleName}(${packet.id}) in state ${state.name}")
                        playerHandler.handleIncomingPacket(packet)
                    }

                    Thread.sleep(10L)
                } catch (e: Exception) {
                    if (!closingChannel) disconnectPlayer("Read error: ${e.message}")
                    return@thread
                }
            }
        }

        // Thread that sends packets to the client
        thread {
            while (connected) {
                if (packetQueue.isEmpty() && closingChannel) socket.close()

                val packet = packetQueue.poll()

                if (packet != null) {
                    try {
                        val id = packet.id
                        val packetData = packet.writePacketData()
                        val data = packetData.toByteArray()

                        val buf = PacketBuffer(ByteArray(32767))
                        val size = PacketBuffer.getVarIntSize(id) + data.size

                        buf.writeVarInt(size)
                        buf.writeVarInt(id)
                        if (data.isNotEmpty())
                            buf.writeBytes(data)

                        socketOutput.write(buf.toByteArray())

                        if (LimboServer.PACKET_DEBUG_MODE) println("[S -> C]: Wrote packet ${packet::class.java.simpleName}($id) in state $state")
                    } catch (e: Exception) {
                        if (!closingChannel) disconnectPlayer("Write error: ${e.message}")
                        return@thread
                    }
                } else Thread.sleep(1L)
            }
        }
    }

    fun dropPlayer(reason: String) {
        tryCloseChannel()
        println("[Server] Player $name dropped for: \"$reason\"")
        connected = false
        if (state == ClientState.PLAY) {
            LimboServer.SERVER.broadcastMessage("$name has left.")
        }
    }

    fun disconnectPlayer(reason: String) {
        if (!connected) return
        if (state == ClientState.LOGIN) {
            sendPacket(PacketOutDisconnect(Chat(reason)))
            closingChannel = true
            println("[Server] Player $name disconnected for: \"$reason\"")
        } else dropPlayer(reason)
    }

    fun getRemoteAddress(): InetSocketAddress = socket.remoteSocketAddress as InetSocketAddress
    fun getPlayerIp(): String = getRemoteAddress().hostString

    fun isLocalhostPlayer(): Boolean {
        val addr = getRemoteAddress().address

        return if (addr.isAnyLocalAddress || addr.isLoopbackAddress) true else try {
            NetworkInterface.getByInetAddress(addr) != null
        } catch (e: SocketException) {
            false
        }
    }

    fun sendPacket(packet: Packet): Boolean = packetQueue.offer(packet)
    fun enableEncryption(secret: ByteArray) {
        packetSerializer = PacketSerializer(this, secret)
    }

    fun closeChannelWhenEmpty() {
        closingChannel = true
    }

    fun tryCloseChannel() {
        try {
            socket.close()
        } catch (e: Exception) {}
        LimboServer.SERVER.players.remove(this)
    }

    fun updateHandlerForState(state: ClientState) {
        playerHandler = when (state) {
            ClientState.HANDSHAKE -> InitialHandler(this)
            ClientState.STATUS -> StatusHandler(this)
            ClientState.LOGIN -> LoginHandler(this)
            ClientState.PLAY -> PlayHandler(this)
        }
        playerHandler.handlePlayer()
    }

    private fun readPacketSize(stream: InputStream): Int {
        var i = 0
        var chunk = 0
        var b: Byte
        do {
            b = stream.read().toByte()
            i = i or ((b and 0x7F.toByte()).toInt() shl chunk++ * 7)
            if (chunk > 5) {
                throw RuntimeException("VarInt too big")
            }
        } while ((b and 0x80.toByte()).toInt() == 0x80)
        return i
    }
}