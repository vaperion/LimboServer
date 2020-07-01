package me.vaperion.limbo.network.packet

import me.vaperion.limbo.utils.PacketBuffer

abstract class Packet(val id: Int) {
    abstract fun readPacketData(bytes: PacketBuffer)
    abstract fun writePacketData(): PacketBuffer
}