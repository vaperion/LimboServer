package me.vaperion.limbo.network.packet

import me.vaperion.limbo.network.ClientState
import me.vaperion.limbo.network.packet.clientbound.handshaking.PacketOutHandshake
import me.vaperion.limbo.network.packet.clientbound.login.PacketOutDisconnect
import me.vaperion.limbo.network.packet.clientbound.login.PacketOutEncryptionRequest
import me.vaperion.limbo.network.packet.clientbound.login.PacketOutLoginSuccess
import me.vaperion.limbo.network.packet.clientbound.play.*
import me.vaperion.limbo.network.packet.clientbound.status.PacketOutPong
import me.vaperion.limbo.network.packet.clientbound.status.PacketOutResponse
import me.vaperion.limbo.network.packet.serverbound.handshaking.PacketInHandshake
import me.vaperion.limbo.network.packet.serverbound.login.PacketInEncryptionResponse
import me.vaperion.limbo.network.packet.serverbound.login.PacketInLoginStart
import me.vaperion.limbo.network.packet.serverbound.play.PacketInChat
import me.vaperion.limbo.network.packet.serverbound.play.PacketInKeepAlive
import me.vaperion.limbo.network.packet.serverbound.status.PacketInPing
import me.vaperion.limbo.network.packet.serverbound.status.PacketInRequest
import java.util.function.Supplier

object PacketRegistry {

    val PACKETS: Map<ClientState, Map<PacketDirection, Map<Int, Supplier<out Packet>>>> = mapOf(
            ClientState.HANDSHAKE to mapOf(
                    PacketDirection.SERVERBOUND to mapOf(
                            0x00 to Supplier { PacketInHandshake() }
                    ),

                    PacketDirection.CLIENTBOUND to mapOf(
                            0x00 to Supplier { PacketOutHandshake() }
                    )
            ),

            ClientState.STATUS to mapOf(
                    PacketDirection.SERVERBOUND to mapOf(
                            0x00 to Supplier { PacketInRequest() },
                            0x01 to Supplier { PacketInPing() }
                    ),

                    PacketDirection.CLIENTBOUND to mapOf(
                            0x00 to Supplier { PacketOutResponse() },
                            0x01 to Supplier { PacketOutPong() }
                    )
            ),

            ClientState.LOGIN to mapOf(
                    PacketDirection.SERVERBOUND to mapOf(
                            0x00 to Supplier { PacketInLoginStart() },
                            0x01 to Supplier { PacketInEncryptionResponse() }
                    ),

                    PacketDirection.CLIENTBOUND to mapOf(
                            0x00 to Supplier { PacketOutDisconnect() },
                            0x01 to Supplier { PacketOutEncryptionRequest() },
                            0x02 to Supplier { PacketOutLoginSuccess() }
                    )
            ),

            ClientState.PLAY to mapOf(
                    PacketDirection.SERVERBOUND to mapOf(
                            0x00 to Supplier { PacketInKeepAlive() },
                            0x01 to Supplier { PacketInChat() }
                    ),

                    PacketDirection.CLIENTBOUND to mapOf(
                            0x00 to Supplier { PacketOutKeepAlive() },
                            0x01 to Supplier { PacketOutJoinGame() },
                            0x02 to Supplier { PacketOutChat() },
                            0x05 to Supplier { PacketOutSpawnPosition() },
                            0x08 to Supplier { PacketOutPositionLook() },
                            0x21 to Supplier { PacketOutChunkData() }
                    )
            )
    )

    fun getPacketByID(state: ClientState, direction: PacketDirection, id: Int): Packet? {
        val allPackets = PACKETS[state] ?: return null
        val packets = allPackets[direction] ?: return null
        val supplier = packets[id] ?: return null
        return supplier.get()
    }

}