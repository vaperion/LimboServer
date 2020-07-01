package me.vaperion.limbo.network.packet.clientbound.login

import me.vaperion.limbo.network.packet.clientbound.ClientboundPacket
import me.vaperion.limbo.utils.PacketBuffer
import me.vaperion.limbo.utils.serializable.Chat

class PacketOutDisconnect(var chat: Chat) : ClientboundPacket(0x00) {
    constructor() : this(Chat(""))

    override fun writePacketData(): PacketBuffer {
        val buf = PacketBuffer(ByteArray(32767))
        buf.writeString(chat.serialize())
        return buf
    }
}