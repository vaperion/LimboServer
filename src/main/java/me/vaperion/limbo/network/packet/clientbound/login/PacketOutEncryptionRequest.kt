package me.vaperion.limbo.network.packet.clientbound.login

import me.vaperion.limbo.network.packet.clientbound.ClientboundPacket
import me.vaperion.limbo.utils.PacketBuffer

class PacketOutEncryptionRequest(var serverId: String, var pubkey: ByteArray, var random: ByteArray) : ClientboundPacket(0x01) {
    constructor() : this("", ByteArray(0), ByteArray(0))

    override fun writePacketData(): PacketBuffer {
        val buf = PacketBuffer(ByteArray(32767))
        buf.writeString(serverId)
        buf.writeVarInt(pubkey.size)
        buf.writeBytes(pubkey)
        buf.writeVarInt(random.size)
        buf.writeBytes(random)
        return buf
    }
}