package me.vaperion.limbo.utils.serializable

import me.vaperion.limbo.utils.JsonChain

class Chat(initialString: String) {

    private var text: String = ""

    init {
        text += initialString
    }

    fun append(str: String): Chat {
        text += str
        return this
    }

    fun serialize(): String {
        return JsonChain()
                .addProperty("text", text)
                .str()
    }

}