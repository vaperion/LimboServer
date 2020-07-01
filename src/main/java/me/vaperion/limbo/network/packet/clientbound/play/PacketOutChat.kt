package me.vaperion.limbo.network.packet.clientbound.play

import me.vaperion.limbo.network.packet.clientbound.ClientboundPacket
import me.vaperion.limbo.utils.PacketBuffer
import me.vaperion.limbo.utils.serializable.Chat

class PacketOutChat(var msg: Chat, var type: Byte) : ClientboundPacket(0x02) {
    constructor() : this(Chat(""), 0)

    override fun writePacketData(): PacketBuffer {
        val buf = PacketBuffer(ByteArray(32767))
        buf.writeString(msg.serialize())
        buf.writeByte(type)
        return buf
    }
}