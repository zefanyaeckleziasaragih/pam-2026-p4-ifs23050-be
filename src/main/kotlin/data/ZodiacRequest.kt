package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Zodiac

@Serializable
data class ZodiacRequest(
    var nama: String = "",
    var simbol: String = "",
    var elemen: String = "",
    var tanggalLahir: String = "",
    var deskripsi: String = "",
    var karakteristik: String = "",
    var kecocokan: String = "",
    var pathGambar: String = "",
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "nama"          to nama,
        "simbol"        to simbol,
        "elemen"        to elemen,
        "tanggalLahir"  to tanggalLahir,
        "deskripsi"     to deskripsi,
        "karakteristik" to karakteristik,
        "kecocokan"     to kecocokan,
        "pathGambar"    to pathGambar,
    )

    fun toEntity(): Zodiac = Zodiac(
        nama          = nama,
        simbol        = simbol,
        elemen        = elemen,
        tanggalLahir  = tanggalLahir,
        deskripsi     = deskripsi,
        karakteristik = karakteristik,
        kecocokan     = kecocokan,
        pathGambar    = pathGambar,
    )
}