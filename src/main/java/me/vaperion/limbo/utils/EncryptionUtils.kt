package me.vaperion.limbo.utils

import java.math.BigInteger
import java.security.*
import javax.crypto.Cipher

object EncryptionUtils {

    fun createRsaKeyPair(keysize: Int) : KeyPair {
        return try {
            val generator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
            generator.initialize(keysize)
            generator.generateKeyPair()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Unable to generate RSA keypair", e)
        }
    }

    fun twosComplementHexdigest(digest: ByteArray?): String {
        return BigInteger(digest).toString(16)
    }

    @Throws(GeneralSecurityException::class)
    fun decryptRsa(keyPair: KeyPair, bytes: ByteArray?): ByteArray {
        val cipher: Cipher = Cipher.getInstance("RSA")
        cipher.init(2, keyPair.private)
        return cipher.doFinal(bytes)
    }

    fun generateServerId(sharedSecret: ByteArray?, key: PublicKey): String {
        return try {
            val digest: MessageDigest = MessageDigest.getInstance("SHA-1")
            digest.update(sharedSecret)
            digest.update(key.getEncoded())
            twosComplementHexdigest(digest.digest())
        } catch (e: NoSuchAlgorithmException) {
            throw AssertionError(e as Any)
        }
    }
}