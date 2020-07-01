package me.vaperion.limbo.handler.impl

import com.google.gson.JsonParser
import me.vaperion.limbo.LimboServer
import me.vaperion.limbo.handler.AbstractHandler
import me.vaperion.limbo.network.ClientState
import me.vaperion.limbo.network.packet.Packet
import me.vaperion.limbo.network.packet.clientbound.login.PacketOutEncryptionRequest
import me.vaperion.limbo.network.packet.clientbound.login.PacketOutLoginSuccess
import me.vaperion.limbo.network.packet.serverbound.login.PacketInEncryptionResponse
import me.vaperion.limbo.network.packet.serverbound.login.PacketInLoginStart
import me.vaperion.limbo.player.Player
import me.vaperion.limbo.utils.EncryptionUtils
import me.vaperion.limbo.utils.PlayerUtils
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import java.net.URLEncoder
import java.security.KeyPair
import java.util.*
import kotlin.random.Random

class LoginHandler(player: Player) : AbstractHandler(player) {

    /*
      C->S : Login Start

      S->C : Encryption Key Request (Client Auth)
      C->S : Encryption Key Response (Server Auth, Both enable encryption)

      S->C : Login Success
     */

    private val client: CloseableHttpClient = HttpClients.createDefault()
    private val verifyBytes = ByteArray(4)

    override fun handlePlayer() {
        Random.nextBytes(verifyBytes)
    }

    override fun handleIncomingPacket(packet: Packet) {
        if (packet is PacketInLoginStart) {
            player.name = packet.username

            if (player.isLocalhostPlayer() || !LimboServer.SERVER.onlineMode) { // Localhost players do not have encryption enabled
                authenticatePlayer(Random.nextBytes(1), LimboServer.SERVER_KEYPAIR)
                return
            }

            player.sendPacket(PacketOutEncryptionRequest(
                    "",
                    LimboServer.SERVER_KEYPAIR.public.encoded,
                    verifyBytes))
        }
        if (packet is PacketInEncryptionResponse) {
            val keyPair = LimboServer.SERVER_KEYPAIR
            val privateKey = keyPair.private

            val decryptedToken = EncryptionUtils.decryptRsa(keyPair, packet.verifyToken)

            if (!verifyBytes.contentEquals(decryptedToken)) {
                player.disconnectPlayer("Failed to verify your session.")
                return
            }

            val decryptedShare = EncryptionUtils.decryptRsa(keyPair, packet.sharedSecret)

            authenticatePlayer(decryptedShare, keyPair)
        }
    }

    private fun authenticatePlayer(decryptedShare: ByteArray, keyPair: KeyPair) {
        if (player.isLocalhostPlayer() || !LimboServer.SERVER.onlineMode) {
            player.uniqueId = UUID.nameUUIDFromBytes("OfflinePlayer:${player.name}".toByteArray(Charsets.UTF_8))
            continueToPlay()
            return
        }

        val serverId = EncryptionUtils.generateServerId(decryptedShare, keyPair.public)

        val url = LimboServer.SERVER.hasJoinedUrl
                .replace("%name%", URLEncoder.encode(player.name!!, "UTF-8"))
                .replace("%serverid%", URLEncoder.encode(serverId, "UTF-8")) +
                if (LimboServer.SERVER.preventProxyConnections) "&ip=" + URLEncoder.encode(player.getPlayerIp(), "UTF-8") else ""

        client.execute(HttpGet(url)).use {
            if (!player.connected) return // The player has disconnected after being authenticated
            if (decryptedShare.size > 1) player.enableEncryption(decryptedShare) // Enable encryption

            when (it.statusLine.statusCode) {
                200 -> { // Successful authentication
                    val json = JsonParser().parse(it.entity.content.readBytes().toString(Charsets.UTF_8)).asJsonObject
                    player.uniqueId = PlayerUtils.convertUUID(json["id"].asString)
                    player.name = json["name"].asString

                    continueToPlay()
                }
                204 -> { // No response - offline player on online mode?, close connection
                    player.disconnectPlayer("This server is online mode only.")
                }
                else -> { // Unexpected response, close connection
                    player.disconnectPlayer("Unexpected response from the authentication server.")
                }
            }
        }
    }

    private fun continueToPlay() {
        player.sendPacket(PacketOutLoginSuccess(player.name!!, player.uniqueId.toString()))

        Thread.sleep(5L)

        player.state = ClientState.PLAY
        player.updateHandlerForState(ClientState.PLAY)

        println("${player.name}[${player.getPlayerIp()}] has logged in.")
    }

    override fun getName(): String = "LoginHandler"
    override fun toString(): String = "${getName()}-${player.state.name}:${player.uniqueId.toString()}-${player.name}"
}