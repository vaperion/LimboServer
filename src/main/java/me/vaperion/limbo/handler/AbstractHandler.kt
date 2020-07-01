package me.vaperion.limbo.handler

import me.vaperion.limbo.network.packet.Packet
import me.vaperion.limbo.player.Player

abstract class AbstractHandler(val player: Player) {

    abstract fun handlePlayer()
    abstract fun handleIncomingPacket(packet: Packet)

    abstract fun getName(): String
    abstract override fun toString(): String

}