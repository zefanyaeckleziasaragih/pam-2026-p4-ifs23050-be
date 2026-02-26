package org.delcom.repositories

import org.delcom.dao.ZodiacDAO
import org.delcom.entities.Zodiac
import org.delcom.helpers.zodiacDaoToModel
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
                .limit(50)
                .map(::zodiacDaoToModel)
        } else {
            val keyword = "%${search.lowercase()}%"
            ZodiacDAO
                .find {
                    (ZodiacTable.namaUmum.lowerCase() like keyword)
                }
                .orderBy(ZodiacTable.namaUmum to SortOrder.ASC)
                .limit(50)
                .map(::zodiacDaoToModel)
        }
    }

    override suspend fun getZodiacById(id: String): Zodiac? = suspendTransaction {
        ZodiacDAO
            .find { ZodiacTable.id eq UUID.fromString(id) }
            .limit(1)
            .map(::zodiacDaoToModel)
            .firstOrNull()
    }

    override suspend fun getZodiacByNamaUmum(namaUmum: String): Zodiac? = suspendTransaction {
        ZodiacDAO
            .find { ZodiacTable.namaUmum eq namaUmum }
            .limit(1)
            .map(::zodiacDaoToModel)
            .firstOrNull()
    }

    override suspend fun addZodiac(zodiac: Zodiac): String = suspendTransaction {
        val dao = ZodiacDAO.new {
            namaUmum   = zodiac.namaUmum
            namaLatin  = zodiac.namaLatin
            makna      = zodiac.makna
            asalBudaya = zodiac.asalBudaya
            deskripsi  = zodiac.deskripsi
            pathGambar = zodiac.pathGambar
            createdAt  = zodiac.createdAt
            updatedAt  = zodiac.updatedAt
        }
        dao.id.value.toString()
    }

    override suspend fun updateZodiac(id: String, newZodiac: Zodiac): Boolean = suspendTransaction {
        val dao = ZodiacDAO
            .find { ZodiacTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (dao != null) {
            dao.namaUmum   = newZodiac.namaUmum
            dao.namaLatin  = newZodiac.namaLatin
            dao.makna      = newZodiac.makna
            dao.asalBudaya = newZodiac.asalBudaya
            dao.deskripsi  = newZodiac.deskripsi
            dao.pathGambar = newZodiac.pathGambar
            dao.updatedAt  = newZodiac.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun removeZodiac(id: String): Boolean = suspendTransaction {
        val rows = ZodiacTable.deleteWhere {
            ZodiacTable.id eq UUID.fromString(id)
        }
        rows == 1
    }
}