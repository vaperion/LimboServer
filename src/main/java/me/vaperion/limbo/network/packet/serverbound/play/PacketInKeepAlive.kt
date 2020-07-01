package me.vaperion.limbo.network.packet.serverbound.play

import me.vaperion.limbo.network.packet.serverbound.ServerboundPacket
import me.vaperion.limbo.utils.PacketBuffer

class PacketInKeepAlive : ServerboundPacket(0x00) {
    var keepAliveId = 0

    override fun readPacketData(bytes: PacketBuffer) {
        keepAliveId = bytes.readVarInt()
    }
}