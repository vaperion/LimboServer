package me.vaperion.limbo.network.packet.clientbound.play

import me.vaperion.limbo.network.packet.clientbound.ClientboundPacket
import me.vaperion.limbo.utils.PacketBuffer
import me.vaperion.limbo.utils.serializable.Position

class PacketOutPositionLook(var x: Double, var y: Double, var z: Double, var yaw: Float, var pitch: Float, var flags: Byte) : ClientboundPacket(0x08) {
    constructor() : this(0.0, 0.0, 0.0, 0f, 0f, 0x00)

    override fun writePacketData(): PacketBuffer {
        val buf = PacketBuffer(ByteArray(32767))
        buf.writeDouble(x)
        buf.writeDouble(y)
        buf.writeDouble(z)
        buf.writeFloat(yaw)
        buf.writeFloat(pitch)
        buf.writeByte(flags)
        return buf
    }
}