package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Flower

@Serializable
data class FlowerRequest(
    var namaUmum   : String = "",
    var namaLatin  : String = "",
    var makna      : String = "",
    var asalBudaya : String = "",
    var deskripsi  : String = "",
    var pathGambar : String = "",
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "namaUmum"   to namaUmum,
        "namaLatin"  to namaLatin,
        "makna"      to makna,
        "asalBudaya" to asalBudaya,
        "deskripsi"  to deskripsi,
        "pathGambar" to pathGambar,
    )

    fun toEntity(): Flower = Flower(
        namaUmum   = namaUmum,
        namaLatin  = namaLatin,
        makna      = makna,
        asalBudaya = asalBudaya,
        deskripsi  = deskripsi,
        pathGambar = pathGambar,
    )
}
