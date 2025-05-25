package ru.takeshiko.matuleme.presentation.utils

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class FlexibleInstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        val str = decoder.decodeString()
        return try {
            Instant.parse(str)
        } catch (_: Exception) {
            try {
                Instant.parse("${str}Z")
            } catch (_: Exception) {
                val normalizedStr = if (str.contains(".")) {
                    val parts = str.split(".")
                    val fractionalPart = parts[1].take(6).padEnd(6, '0')
                    "${parts[0]}.${fractionalPart}Z"
                } else {
                    "${str}Z"
                }
                Instant.parse(normalizedStr)
            }
        }
    }
}