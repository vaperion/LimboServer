package me.vaperion.limbo.handler.impl

import me.vaperion.limbo.handler.AbstractHandler
import me.vaperion.limbo.network.ClientState
import me.vaperion.limbo.network.packet.Packet
import me.vaperion.limbo.network.packet.serverbound.handshaking.PacketInHandshake
import me.vaperion.limbo.player.Player

class InitialHandler(player: Player) : AbstractHandler(player) {

    /*
      C->S : Handshake State=2
      C->S : Login Start
      S->C : Encryption Key Request
      (Client Auth)
      C->S : Encryption Key Response
      (Server Auth, Both enable encryption)
      S->C : Login Success
     */

    override fun handlePlayer() {
    }

    override fun handleIncomingPacket(packet: Packet) {
        if (packet is PacketInHandshake) {
            player.protocolVersion = packet.version

            if (packet.nextState != 1 && packet.nextState != 2) {
                // The client requested an invalid state?! Close their channel.
                println("[${toString()}] Disconnected due to invalid nextState")
                player.tryCloseChannel()
                return
            }

            val nextState = if (packet.nextState == 1) ClientState.STATUS else ClientState.LOGIN

            player.state = nextState
            player.updateHandlerForState(nextState)
        }
    }

    override fun getName(): String = "InitialHandler"
    override fun toString(): String = "${getName()}-${player.state.name}:${player.uniqueId.toString()}-${player.name}"
}