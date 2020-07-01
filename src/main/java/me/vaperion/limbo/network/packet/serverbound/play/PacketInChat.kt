package me.vaperion.limbo.network.packet.serverbound.play

import me.vaperion.limbo.network.packet.serverbound.ServerboundPacket
import me.vaperion.limbo.utils.PacketBuffer

class PacketInChat : ServerboundPacket(0x01) {
    var message: String = ""

    override fun readPacketData(bytes: PacketBuffer) {
        message = bytes.readString()
    }
}