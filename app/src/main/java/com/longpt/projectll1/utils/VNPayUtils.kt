package com.longpt.projectll1.utils

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.text.isNullOrEmpty

object VNPayUtils {
    fun hmacSHA512(key: String, data: String): String {
        val hmac = Mac.getInstance("HmacSHA512")
        val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA512")
        hmac.init(secretKey)
        val hashBytes = hmac.doFinal(data.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun buildHashData(params: Map<String, String>): Pair<StringBuilder, StringBuilder> {
        val fieldNames = params.keys.sorted()
        val hashData = StringBuilder()
        val query = StringBuilder()

        fieldNames.forEachIndexed { index, fieldName ->
            val fieldValue = params[fieldName]
            if (!fieldValue.isNullOrEmpty()) {
                val encodedValue =
                    URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString())
                val encodedName = URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())

                hashData.append("$encodedName=$encodedValue")
                query.append("$encodedName=$encodedValue")

                if (index < fieldNames.size - 1) {
                    hashData.append('&')
                    query.append('&')
                }
            }
        }
        return  Pair(hashData, query)
    }
}