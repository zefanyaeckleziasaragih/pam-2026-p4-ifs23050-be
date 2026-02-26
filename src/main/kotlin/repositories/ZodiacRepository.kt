package org.delcom.repositories

import org.delcom.dao.ZodiacDAO
import org.delcom.entities.Zodiac
import org.delcom.helpers.daoToZodiac
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.ZodiacTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class ZodiacRepository : IZodiacRepository {

    override suspend fun getZodiacs(search: String): List<Zodiac> = suspendTransaction {
        if (search.isBlank()) {
            ZodiacDAO.all()
                .orderBy(ZodiacTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::daoToZodiac)
        } else {
            val keyword = "%${search.lowercase()}%"
            ZodiacDAO
                .find { ZodiacTable.nama.lowerCase() like keyword }
                .orderBy(ZodiacTable.nama to SortOrder.ASC)
                .limit(20)
                .map(::daoToZodiac)
        }
    }

    override suspend fun getZodiacById(id: String): Zodiac? = suspendTransaction {
        ZodiacDAO
            .find { ZodiacTable.id eq UUID.fromString(id) }
            .limit(1)
            .map(::daoToZodiac)
            .firstOrNull()
    }

    override suspend fun getZodiacByName(name: String): Zodiac? = suspendTransaction {
        ZodiacDAO
            .find { ZodiacTable.nama eq name }
            .limit(1)
            .map(::daoToZodiac)
            .firstOrNull()
    }

    override suspend fun addZodiac(zodiac: Zodiac): String = suspendTransaction {
        ZodiacDAO.new {
            nama          = zodiac.nama
            simbol        = zodiac.simbol
            elemen        = zodiac.elemen
            tanggalLahir  = zodiac.tanggalLahir
            pathGambar    = zodiac.pathGambar
            deskripsi     = zodiac.deskripsi
            karakteristik = zodiac.karakteristik
            kecocokan     = zodiac.kecocokan
            createdAt     = zodiac.createdAt
            updatedAt     = zodiac.updatedAt
        }.id.value.toString()
    }

    override suspend fun updateZodiac(id: String, newZodiac: Zodiac): Boolean = suspendTransaction {
        val dao = ZodiacDAO
            .find { ZodiacTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull() ?: return@suspendTransaction false

        dao.nama          = newZodiac.nama
        dao.simbol        = newZodiac.simbol
        dao.elemen        = newZodiac.elemen
        dao.tanggalLahir  = newZodiac.tanggalLahir
        dao.pathGambar    = newZodiac.pathGambar
        dao.deskripsi     = newZodiac.deskripsi
        dao.karakteristik = newZodiac.karakteristik
        dao.kecocokan     = newZodiac.kecocokan
        dao.updatedAt     = newZodiac.updatedAt
        true
    }

    override suspend fun removeZodiac(id: String): Boolean = suspendTransaction {
        ZodiacTable.deleteWhere { ZodiacTable.id eq UUID.fromString(id) } == 1
    }
}