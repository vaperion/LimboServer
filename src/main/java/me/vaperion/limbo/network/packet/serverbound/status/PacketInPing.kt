package me.vaperion.limbo.network.packet.serverbound.status

import me.vaperion.limbo.network.packet.serverbound.ServerboundPacket
import me.vaperion.limbo.utils.PacketBuffer

class PacketInPing : ServerboundPacket(0x01) {
    var payload: Long = 0L
        private set

    override fun readPacketData(bytes: PacketBuffer) {
        payload = bytes.readLong()
    }
}