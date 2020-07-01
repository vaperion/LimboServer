package me.vaperion.limbo.network.packet.clientbound.handshaking

import me.vaperion.limbo.network.packet.clientbound.ClientboundPacket
import me.vaperion.limbo.utils.PacketBuffer

class PacketOutHandshake : ClientboundPacket(0x00) {
    override fun writePacketData(): PacketBuffer = PacketBuffer(ByteArray(0))
}