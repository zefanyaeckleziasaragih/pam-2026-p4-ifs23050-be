package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.delcom.dao.FlowerDAO
import org.delcom.dao.PlantDAO
import org.delcom.entities.Flower
import org.delcom.entities.Plant
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun daoToModel(dao: PlantDAO) = Plant(
    dao.id.value.toString(),
    dao.nama,
    dao.pathGambar,
    dao.deskripsi,
    dao.manfaat,
    dao.efekSamping,
    dao.createdAt,
    dao.updatedAt
)

fun flowerDaoToModel(dao: FlowerDAO) = Flower(
    id         = dao.id.value.toString(),
    namaUmum   = dao.namaUmum,
    namaLatin  = dao.namaLatin,
    makna      = dao.makna,
    asalBudaya = dao.asalBudaya,
    deskripsi  = dao.deskripsi,
    pathGambar = dao.pathGambar,
    createdAt  = dao.createdAt,
    updatedAt  = dao.updatedAt,
)