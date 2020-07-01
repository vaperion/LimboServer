package me.vaperion.limbo.utils

import java.util.*
import java.util.regex.Pattern

object PlayerUtils {

    private val UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})")

    fun convertUUID(str: String): UUID = UUID.fromString(UUID_PATTERN.matcher(str).replaceAll("$1-$2-$3-$4-$5"))

}