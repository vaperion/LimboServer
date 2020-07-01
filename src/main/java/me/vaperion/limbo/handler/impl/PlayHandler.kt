package me.vaperion.limbo.handler.impl

import me.vaperion.limbo.LimboServer
import me.vaperion.limbo.handler.AbstractHandler
import me.vaperion.limbo.network.packet.Packet
import me.vaperion.limbo.network.packet.clientbound.play.*
import me.vaperion.limbo.network.packet.serverbound.play.PacketInKeepAlive
import me.vaperion.limbo.player.Player
import me.vaperion.limbo.utils.serializable.Chat
import java.util.concurrent.TimeUnit

class PlayHandler(player: Player) : AbstractHandler(player) {

    private var sentId = 0
    private var lastClientKeepAlive = 0L

    override fun handlePlayer() {
        LimboServer.EXECUTOR.scheduleAtFixedRate({
            sentId++
            player.sendPacket(PacketOutKeepAlive(sentId))

            if ((System.currentTimeMillis() - lastClientKeepAlive) >= 30_000L && lastClientKeepAlive != 0L)
                player.disconnectPlayer("Timed out")
        }, 1L, 5L, TimeUnit.SECONDS)

        player.sendPacket(PacketOutJoinGame())

        for (x in 0 until 7) {
            for (z in 0 until 7) {
                player.sendPacket(PacketOutChunkData(x, z, true, 0x00, 0x00))
            }
        }
        for (x in -7 until 0) {
            for (z in -7 until 0) {
                player.sendPacket(PacketOutChunkData(x, z, true, 0x00, 0x00))
            }
        }

        player.sendPacket(PacketOutSpawnPosition())
        player.sendPacket(PacketOutPositionLook())

        LimboServer.EXECUTOR.scheduleAtFixedRate({
            player.sendPacket(PacketOutChat(Chat(LimboServer.SERVER.actionBarText), 2))
        }, 0L, 2L, TimeUnit.SECONDS)
    }

    override fun handleIncomingPacket(packet: Packet) {
        if (packet is PacketInKeepAlive) {
            lastClientKeepAlive = System.currentTimeMillis()
        }
    }

    override fun getName(): String = "PlayHandler"
    override fun toString(): String = "${getName()}-${player.state.name}:${player.uniqueId.toString()}-${player.name}"
}