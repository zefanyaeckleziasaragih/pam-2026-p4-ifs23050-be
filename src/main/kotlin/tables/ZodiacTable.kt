package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ZodiacTable : UUIDTable("flowers") {
    val namaUmum   = varchar("nama_umum",   100)
    val namaLatin  = varchar("nama_latin",  150)
    val makna      = varchar("makna",       200)
    val asalBudaya = varchar("asal_budaya", 200)
    val deskripsi  = text("deskripsi")
    val pathGambar = varchar("path_gambar", 255)
    val createdAt  = timestamp("created_at")
    val updatedAt  = timestamp("updated_at")
}