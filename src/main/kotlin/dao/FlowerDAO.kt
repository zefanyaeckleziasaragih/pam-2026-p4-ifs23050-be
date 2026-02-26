package org.delcom.dao

import org.delcom.tables.FlowerTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class FlowerDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, FlowerDAO>(FlowerTable)

    var namaUmum   by FlowerTable.namaUmum
    var namaLatin  by FlowerTable.namaLatin
    var makna      by FlowerTable.makna
    var asalBudaya by FlowerTable.asalBudaya
    var deskripsi  by FlowerTable.deskripsi
    var pathGambar by FlowerTable.pathGambar
    var createdAt  by FlowerTable.createdAt
    var updatedAt  by FlowerTable.updatedAt
}
