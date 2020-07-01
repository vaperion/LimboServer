package me.vaperion.limbo.network.packet.clientbound.play

import me.vaperion.limbo.network.packet.clientbound.ClientboundPacket
import me.vaperion.limbo.utils.PacketBuffer

class PacketOutChunkData(var x: Int, var z: Int, var groundUp: Boolean, var bitMask: Short, var size: Int) : ClientboundPacket(0x21) {
    constructor() : this(0, 0, true, 0, 0)

    override fun writePacketData(): PacketBuffer {
        val buf = PacketBuffer(ByteArray(32767))
        buf.writeInt(x)
        buf.writeInt(z)
        buf.writeBoolean(groundUp)
        buf.writeShort(bitMask)
        buf.writeVarInt(size)
        return buf
    }
}