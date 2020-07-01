package me.vaperion.limbo.network.packet.serverbound

import me.vaperion.limbo.network.packet.Packet
import me.vaperion.limbo.utils.PacketBuffer

abstract class ServerboundPacket(id: Int) : Packet(id) {
    override fun writePacketData(): PacketBuffer = PacketBuffer(ByteArray(0))
}