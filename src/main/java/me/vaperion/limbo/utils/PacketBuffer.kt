package me.vaperion.limbo.utils

import java.nio.ByteOrder
import java.util.*
import kotlin.experimental.and

@Suppress("DEPRECATED_IDENTITY_EQUALS", "unused")
class PacketBuffer(private val array: ByteArray) {
    companion object {
        @JvmStatic
        private val bigEndian: Boolean = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN

        fun getVarIntSize(b: Int): Int {
            var i = b
            var size = 0

            while (i and -0x80 !== 0x0) {
                size++
                i = i ushr 7
            }

            return size
        }
    }

    private var writerIndex = 0
    private var readerIndex = 0

    fun readByte(): Byte {
        val byte = array[readerIndex]
        readerIndex++
        return byte
    }

    fun readBytes(n: Int): ByteArray {
        val bytes = ByteArray(n)
        for (i in 0 until n) bytes[i] = readByte()
        return bytes
    }

    fun readAllBytes(): ByteArray = readBytes(array.size - readerIndex)

    fun readString(): String {
        val len = readVarInt()
        val bytes = readBytes(len)
        return bytes.toString(Charsets.UTF_8)
    }

    fun readInt(): Int {
        val bytes = readBytes(4)

        return if (bigEndian)
            bytes[0].toInt() shl 24 or (bytes[1].toInt() and 0xFF shl 16) or (bytes[2].toInt() and 0xFF shl 8) or (bytes[3].toInt() and 0xFF)
        else
            bytes[3].toInt() shl 24 or (bytes[2].toInt() and 0xFF shl 16) or (bytes[1].toInt() and 0xFF shl 8) or (bytes[0].toInt() and 0xFF)
    }

    fun readShort(): Short {
        val bytes = readBytes(2)

        return (if (bigEndian)
            bytes[0].toInt() shl 8 or bytes[1].toInt() and 0xFF
        else
            bytes[1].toInt() shl 8 or bytes[0].toInt() and 0xFF).toShort()
    }

    fun readLong(): Long {
        val bytes = readBytes(8)

        return if (bigEndian)
            bytes[0].toLong() shl 56 or (bytes[1].toLong() and 0xFF shl 48) or (bytes[2].toLong() and 0xFF shl 40) or (bytes[3].toLong() and 0xFF shl 32) or bytes[4].toLong() shl 24 or (bytes[5].toLong() and 0xFF shl 16) or (bytes[6].toLong() and 0xFF shl 8) or (bytes[7].toLong() and 0xFF)
        else
            bytes[7].toLong() shl 56 or (bytes[6].toLong() and 0xFF shl 48) or (bytes[5].toLong() and 0xFF shl 40) or (bytes[4].toLong() and 0xFF shl 32) or bytes[3].toLong() shl 24 or (bytes[2].toLong() and 0xFF shl 16) or (bytes[1].toLong() and 0xFF shl 8) or (bytes[0].toLong() and 0xFF)
    }

    fun readBoolean(): Boolean = readByte() != 0.toByte()

    fun readUnsignedByte(): Short = readByte().toShort() and 0xFF
    fun readUnsignedInt(): Long = readInt().toLong() and 0xFFFFFFFFL
    fun redUnsignedShort(): Int = readShort().toInt() and 0xFFFF

    fun readChar(): Char = readShort().toChar()
    fun readFloat(): Float = Float.fromBits(readInt())
    fun readDouble(): Double = Double.fromBits(readLong())

    fun writeByte(byte: Byte) {
        array[writerIndex] = byte
        writerIndex++
    }

    fun writeBytes(arr: ByteArray) {
        for (byte in arr) {
            writeByte(byte)
        }
    }

    fun writeBoolean(bool: Boolean) {
        writeByte(if (bool) 1 else 0)
    }

    fun writeShort(sh: Short) {
        if (bigEndian) {
            writeByte((sh.toInt() ushr 8).toByte())
            writeByte(sh.toByte())
        } else {
            writeByte(sh.toByte())
            writeByte((sh.toInt() ushr 8).toByte())
        }
    }

    fun writeUnsignedShort(sh: Short) {
        writeByte(sh.toByte())
        writeByte((sh.toInt() ushr 8).toByte())
    }

    fun writeInt(i: Int) {
        if (bigEndian) {
            writeShort((i ushr 16).toShort())
            writeShort(i.toShort())
        } else {
            writeShort(i.toShort())
            writeShort((i ushr 16).toShort())
        }
    }

    fun writeUnsignedInt(i: Int) {
        writeShort(i.toShort())
        writeShort((i ushr 16).toShort())
    }

    fun writeDouble(d: Double) {
        writeLong(java.lang.Double.doubleToRawLongBits(d))
    }

    fun writeFloat(f: Float) {
        writeInt(java.lang.Float.floatToRawIntBits(f))
    }

    fun writeLong(l: Long) {
        if (bigEndian) {
            writeInt((l ushr 32).toInt())
            writeInt(l.toInt())
        } else {
            writeInt(l.toInt())
            writeInt((l ushr 32).toInt())
        }
    }

    fun writeUnsignedLong(l: Long) {
        writeInt(l.toInt())
        writeInt((l ushr 32).toInt())
    }

    fun writeVarInt(b: Int) {
        var i = b

        while (i and -0x80 !== 0x0) {
            writeByte((i and 0x7F or 0x80).toByte())
            i = i ushr 7
        }

        writeByte(i.toByte())
    }

    fun writeString(str: String) {
        writeVarInt(str.length)
        writeBytes(str.toByteArray(Charsets.UTF_8))
    }

    fun readVarInt(): Int {
        var i = 0
        var chunk = 0
        var b: Byte
        do {
            b = readByte()
            i = i or ((b and 0x7F.toByte()).toInt() shl chunk++ * 7)
            if (chunk > 5) {
                throw RuntimeException("VarInt too big")
            }
        } while ((b and 0x80.toByte()).toInt() == 0x80)
        return i
    }

    fun readVarLong(): Long {
        var i = 0L
        var chunk = 0
        var b: Byte
        do {
            b = readByte()
            i = i or ((b and 0x7F.toByte()).toLong() shl chunk++ * 7)
            if (chunk > 10) {
                throw RuntimeException("VarLong too big")
            }
        } while ((b and 0x80.toByte()).toInt() == 0x80)
        return i
    }

    fun readUUID(): UUID = UUID(readVarLong(), readVarLong())

    fun getByteArray(): ByteArray = array
    fun toByteArray(): ByteArray {
        if (writerIndex == 0) return ByteArray(0)
        return array.sliceArray(IntRange(0, writerIndex))
    }
}