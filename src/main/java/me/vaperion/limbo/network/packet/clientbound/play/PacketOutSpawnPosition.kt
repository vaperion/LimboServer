package me.vaperion.limbo.network.packet.clientbound.play

import me.vaperion.limbo.network.packet.clientbound.ClientboundPacket
import me.vaperion.limbo.utils.PacketBuffer
import me.vaperion.limbo.utils.serializable.Position

class PacketOutSpawnPosition(var pos: Position) : ClientboundPacket(0x05) {
    constructor() : this(Position(0,0,0))

    override fun writePacketData(): PacketBuffer {
        val buf = PacketBuffer(ByteArray(32767))
        buf.writeLong(pos.serialize())
        return buf
    }
}