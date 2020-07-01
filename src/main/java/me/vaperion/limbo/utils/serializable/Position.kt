package me.vaperion.limbo.utils.serializable

import me.vaperion.limbo.utils.PacketBuffer

class Position(var x: Long, var y: Long, var z: Long) {

    companion object {
        fun deserialize(long: Long) : Position {
            val x = long shr 38
            val y = long and 0xFFF
            val z = long shl 26 shr 38

            return Position(x, y, z)
        }
    }

    fun serialize() : Long {
        return ((x and 0x3FFFFFF) shl 38) or ((z and 0x3FFFFFF) shl 12) or (y and 0xFFF)
    }

}