package me.vaperion.limbo.utils

import com.google.gson.JsonElement
import com.google.gson.JsonObject

class JsonChain {
    private val json: JsonObject = JsonObject()

    fun addProperty(property: String?, value: String?): JsonChain {
        json.addProperty(property, value)
        return this
    }

    fun addProperty(property: String?, value: Number?): JsonChain {
        json.addProperty(property, value)
        return this
    }

    fun addProperty(property: String?, value: Boolean?): JsonChain {
        json.addProperty(property, value)
        return this
    }

    fun addProperty(property: String?, value: Char?): JsonChain {
        json.addProperty(property, value)
        return this
    }

    fun add(property: String?, element: JsonElement?): JsonChain {
        json.add(property, element)
        return this
    }

    fun get(): JsonObject = json
    fun str(): String = get().toString()

}