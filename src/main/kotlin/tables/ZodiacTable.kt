package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ZodiacTable : UUIDTable("zodiacs") {
    val nama          = varchar("nama", 100)
    val simbol        = varchar("simbol", 20)
    val elemen        = varchar("elemen", 50)
    val tanggalLahir  = varchar("tanggal_lahir", 100)
    val pathGambar    = varchar("path_gambar", 255)
    val deskripsi     = text("deskripsi")
    val karakteristik = text("karakteristik")
    val kecocokan     = text("kecocokan")
    val createdAt     = timestamp("created_at")
    val updatedAt     = timestamp("updated_at")
}