package me.vaperion.limbo.network.packet.serverbound.login

import me.vaperion.limbo.network.packet.serverbound.ServerboundPacket
import me.vaperion.limbo.utils.PacketBuffer

class PacketInLoginStart : ServerboundPacket(0x00) {
    var username: String = ""

    override fun readPacketData(bytes: PacketBuffer) {
        username = bytes.readString()
    }
}