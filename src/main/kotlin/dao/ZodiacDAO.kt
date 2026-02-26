package org.delcom.dao

import org.delcom.tables.ZodiacTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class ZodiacDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, ZodiacDAO>(ZodiacTable)

    var nama          by ZodiacTable.nama
    var simbol        by ZodiacTable.simbol
    var elemen        by ZodiacTable.elemen
    var tanggalLahir  by ZodiacTable.tanggalLahir
    var pathGambar    by ZodiacTable.pathGambar
    var deskripsi     by ZodiacTable.deskripsi
    var karakteristik by ZodiacTable.karakteristik
    var kecocokan     by ZodiacTable.kecocokan
    var createdAt     by ZodiacTable.createdAt
    var updatedAt     by ZodiacTable.updatedAt
}