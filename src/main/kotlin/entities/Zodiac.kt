package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Zodiac(
    val id: String = UUID.randomUUID().toString(),
    val nama: String,
    val simbol: String,
    val elemen: String,
    val tanggalLahir: String,
    val pathGambar: String,
    val deskripsi: String,
    val karakteristik: String,
    val kecocokan: String,

    @Contextual
    val createdAt: Instant = Clock.System.now(),
    @Contextual
    val updatedAt: Instant = Clock.System.now(),
)