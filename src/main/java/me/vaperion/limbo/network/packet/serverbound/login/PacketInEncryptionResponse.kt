package me.vaperion.limbo.network.packet.serverbound.login

import me.vaperion.limbo.network.packet.serverbound.ServerboundPacket
import me.vaperion.limbo.utils.PacketBuffer

class PacketInEncryptionResponse : ServerboundPacket(0x01) {
    var sharedSecret: ByteArray = ByteArray(0)
    var verifyToken: ByteArray = ByteArray(0)

    override fun readPacketData(bytes: PacketBuffer) {
        val sharedLength = bytes.readVarInt()
        sharedSecret = bytes.readBytes(sharedLength)
        val verifyLength = bytes.readVarInt()
        verifyToken = bytes.readBytes(verifyLength)
    }
}