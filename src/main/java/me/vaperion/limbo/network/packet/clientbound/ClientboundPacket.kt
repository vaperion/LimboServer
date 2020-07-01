package me.vaperion.limbo.network.packet.clientbound

import me.vaperion.limbo.network.packet.Packet
import me.vaperion.limbo.utils.PacketBuffer

abstract class ClientboundPacket(id: Int) : Packet(id) {
    override fun readPacketData(bytes: PacketBuffer) {}
}