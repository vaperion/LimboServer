package me.vaperion.limbo.network.packet.clientbound.play

import me.vaperion.limbo.network.packet.clientbound.ClientboundPacket
import me.vaperion.limbo.utils.PacketBuffer

class PacketOutKeepAlive(var keepAliveId: Int) : ClientboundPacket(0x00) {
    constructor() : this(0)

    override fun writePacketData(): PacketBuffer {
        val buf = PacketBuffer(ByteArray(32767))
        buf.writeVarInt(keepAliveId)
        return buf
    }
}