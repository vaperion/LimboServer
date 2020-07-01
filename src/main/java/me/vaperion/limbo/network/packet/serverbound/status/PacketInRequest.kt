package me.vaperion.limbo.network.packet.serverbound.status

import me.vaperion.limbo.network.packet.serverbound.ServerboundPacket
import me.vaperion.limbo.utils.PacketBuffer

class PacketInRequest : ServerboundPacket(0x00) {
    override fun readPacketData(bytes: PacketBuffer) {
    }
}