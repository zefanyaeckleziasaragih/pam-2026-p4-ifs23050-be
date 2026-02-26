package org.delcom.repositories

import org.delcom.dao.FlowerDAO
import org.delcom.entities.Flower
import org.delcom.helpers.flowerDaoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.FlowerTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class FlowerRepository : IFlowerRepository {

    override suspend fun getFlowers(search: String): List<Flower> = suspendTransaction {
        if (search.isBlank()) {
            FlowerDAO.all()
                .orderBy(FlowerTable.createdAt to SortOrder.DESC)
                .limit(50)
                .map(::flowerDaoToModel)
        } else {
            val keyword = "%${search.lowercase()}%"
            FlowerDAO
                .find {
                    (FlowerTable.namaUmum.lowerCase() like keyword)
                }
                .orderBy(FlowerTable.namaUmum to SortOrder.ASC)
                .limit(50)
                .map(::flowerDaoToModel)
        }
    }

    override suspend fun getFlowerById(id: String): Flower? = suspendTransaction {
        FlowerDAO
            .find { FlowerTable.id eq UUID.fromString(id) }
            .limit(1)
            .map(::flowerDaoToModel)
            .firstOrNull()
    }

    override suspend fun getFlowerByNamaUmum(namaUmum: String): Flower? = suspendTransaction {
        FlowerDAO
            .find { FlowerTable.namaUmum eq namaUmum }
            .limit(1)
            .map(::flowerDaoToModel)
            .firstOrNull()
    }

    override suspend fun addFlower(flower: Flower): String = suspendTransaction {
        val dao = FlowerDAO.new {
            namaUmum   = flower.namaUmum
            namaLatin  = flower.namaLatin
            makna      = flower.makna
            asalBudaya = flower.asalBudaya
            deskripsi  = flower.deskripsi
            pathGambar = flower.pathGambar
            createdAt  = flower.createdAt
            updatedAt  = flower.updatedAt
        }
        dao.id.value.toString()
    }

    override suspend fun updateFlower(id: String, newFlower: Flower): Boolean = suspendTransaction {
        val dao = FlowerDAO
            .find { FlowerTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (dao != null) {
            dao.namaUmum   = newFlower.namaUmum
            dao.namaLatin  = newFlower.namaLatin
            dao.makna      = newFlower.makna
            dao.asalBudaya = newFlower.asalBudaya
            dao.deskripsi  = newFlower.deskripsi
            dao.pathGambar = newFlower.pathGambar
            dao.updatedAt  = newFlower.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun removeFlower(id: String): Boolean = suspendTransaction {
        val rows = FlowerTable.deleteWhere {
            FlowerTable.id eq UUID.fromString(id)
        }
        rows == 1
    }
}
