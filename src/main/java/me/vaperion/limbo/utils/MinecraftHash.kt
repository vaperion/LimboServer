package me.vaperion.limbo.utils

import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object MinecraftHash {

    fun hash(str: String): String? {
        return try {
            val digest = digest(str, "SHA-1")
            BigInteger(digest).toString(16)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun digest(str: String, algorithm: String): ByteArray {
        val md = MessageDigest.getInstance(algorithm)
        val strBytes: ByteArray = str.toByteArray(Charsets.UTF_8)
        return md.digest(strBytes)
    }

}