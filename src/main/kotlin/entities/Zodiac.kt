package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Zodiac(
    var id         : String  = UUID.randomUUID().toString(),
    var namaUmum   : String,
    var namaLatin  : String,
    var makna      : String,
    var asalBudaya : String,
    var deskripsi  : String,
    var pathGambar : String,

    @Contextual
    val createdAt: Instant = Clock.System.now(),
    @Contextual
    var updatedAt: Instant = Clock.System.now(),
)