package me.vaperion.limbo.network.packet.clientbound.status

import me.vaperion.limbo.network.packet.clientbound.ClientboundPacket
import me.vaperion.limbo.utils.PacketBuffer

class PacketOutPong(var payload: Long) : ClientboundPacket(0x01) {
    constructor() : this(0L)

    override fun writePacketData(): PacketBuffer {
        val buf = PacketBuffer(ByteArray(32767))
        buf.writeLong(payload)
        return buf
    }
}