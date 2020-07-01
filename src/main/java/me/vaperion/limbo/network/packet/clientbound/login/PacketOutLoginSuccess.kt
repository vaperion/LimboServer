package me.vaperion.limbo.network.packet.clientbound.login

import me.vaperion.limbo.network.packet.clientbound.ClientboundPacket
import me.vaperion.limbo.utils.PacketBuffer

class PacketOutLoginSuccess(var username: String, var uniqueId: String) : ClientboundPacket(0x02) {
    constructor() : this("", "")

    override fun writePacketData(): PacketBuffer {
        val buf = PacketBuffer(ByteArray(32767))
        buf.writeString(uniqueId)
        buf.writeString(username)
        return buf
    }
}