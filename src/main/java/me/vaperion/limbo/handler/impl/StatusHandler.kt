package me.vaperion.limbo.handler.impl

import me.vaperion.limbo.LimboServer
import me.vaperion.limbo.handler.AbstractHandler
import me.vaperion.limbo.network.packet.Packet
import me.vaperion.limbo.network.packet.clientbound.status.PacketOutPong
import me.vaperion.limbo.network.packet.clientbound.status.PacketOutResponse
import me.vaperion.limbo.network.packet.serverbound.status.PacketInPing
import me.vaperion.limbo.network.packet.serverbound.status.PacketInRequest
import me.vaperion.limbo.player.Player
import me.vaperion.limbo.utils.JsonChain

class StatusHandler(player: Player) : AbstractHandler(player) {

    /*
        We have received nextState=STATUS
        The client may send a Ping packet or a MoTD request packet.
     */

    override fun handlePlayer() {
        if (LimboServer.SERVER.logPings) println("[ConnectionHandler] ${player.getPlayerIp()} is pinging")
    }

    override fun handleIncomingPacket(packet: Packet) {
        if (packet is PacketInRequest) {
            player.sendPacket(PacketOutResponse(JsonChain()
                    .add("version", JsonChain()
                            .addProperty("name", "1.8.9")
                            .addProperty("protocol", 47)
                            .get())
                    .add("players", JsonChain()
                            .addProperty("max", LimboServer.SERVER.getOnlinePlayers().size + 1)
                            .addProperty("online", LimboServer.SERVER.getOnlinePlayers().size)
                            .get())
                    .add("description", JsonChain()
                            .addProperty("text", LimboServer.SERVER.motd)
                            .get())
                    .str()))
        }
        if (packet is PacketInPing) {
            player.sendPacket(PacketOutPong(packet.payload))
            player.closeChannelWhenEmpty()
        }
    }

    override fun getName(): String = "StatusHandler"
    override fun toString(): String = "${getName()}-${player.state.name}:${player.uniqueId.toString()}-${player.name}"
}