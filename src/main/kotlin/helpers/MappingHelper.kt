package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.delcom.dao.ZodiacDAO
import org.delcom.entities.Zodiac
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun daoToZodiac(dao: ZodiacDAO) = Zodiac(
    id            = dao.id.value.toString(),
    nama          = dao.nama,
    simbol        = dao.simbol,
    elemen        = dao.elemen,
    tanggalLahir  = dao.tanggalLahir,
    pathGambar    = dao.pathGambar,
    deskripsi     = dao.deskripsi,
    karakteristik = dao.karakteristik,
    kecocokan     = dao.kecocokan,
    createdAt     = dao.createdAt,
    updatedAt     = dao.updatedAt,
)