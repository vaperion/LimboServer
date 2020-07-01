package me.vaperion.limbo.network.packet.clientbound.status

import me.vaperion.limbo.network.packet.clientbound.ClientboundPacket
import me.vaperion.limbo.utils.PacketBuffer

class PacketOutResponse(var data: String) : ClientboundPacket(0x00) {
    constructor() : this("")

    override fun writePacketData(): PacketBuffer {
        val buf = PacketBuffer(ByteArray(32767))
        buf.writeString(data)
        return buf
    }
}