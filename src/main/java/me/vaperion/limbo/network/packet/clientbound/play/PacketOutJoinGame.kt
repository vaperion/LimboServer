package me.vaperion.limbo.network.packet.clientbound.play

import me.vaperion.limbo.network.packet.clientbound.ClientboundPacket
import me.vaperion.limbo.utils.PacketBuffer

class PacketOutJoinGame(var entityId: Int, var gamemode: Byte, var dimension: Byte, var difficulty: Byte, var maxPlayers: Byte, var levelType: String, var reducedDebug: Boolean) : ClientboundPacket(0x01) {
    constructor() : this(1, 0x00, 0x00, 0x00, 0x64, "default", false)

    override fun writePacketData(): PacketBuffer {
        val buf = PacketBuffer(ByteArray(32767))
        buf.writeInt(entityId)
        buf.writeByte(gamemode)
        buf.writeByte(dimension)
        buf.writeByte(difficulty)
        buf.writeByte(maxPlayers)
        buf.writeString(levelType)
        buf.writeBoolean(reducedDebug)
        return buf
    }
}