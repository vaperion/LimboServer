package me.vaperion.limbo.network.packet.serverbound.handshaking

import me.vaperion.limbo.network.packet.serverbound.ServerboundPacket
import me.vaperion.limbo.utils.PacketBuffer

class PacketInHandshake : ServerboundPacket(0x00) {
    var version: Int = 0
    var address: String = ""
    var port: Short = 0
    var nextState: Int = 0

    override fun readPacketData(bytes: PacketBuffer) {
        version = bytes.readVarInt()
        address = bytes.readString()
        port = bytes.readShort()
        nextState = bytes.readVarInt()
    }
}