package org.delcom.dao

import org.delcom.tables.ZodiacTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class ZodiacDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, ZodiacDAO>(ZodiacTable)

    var namaUmum   by ZodiacTable.namaUmum
    var namaLatin  by ZodiacTable.namaLatin
    var makna      by ZodiacTable.makna
    var asalBudaya by ZodiacTable.asalBudaya
    var deskripsi  by ZodiacTable.deskripsi
    var pathGambar by ZodiacTable.pathGambar
    var createdAt  by ZodiacTable.createdAt
    var updatedAt  by ZodiacTable.updatedAt
}
