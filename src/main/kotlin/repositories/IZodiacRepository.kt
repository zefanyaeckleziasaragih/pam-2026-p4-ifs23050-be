package org.delcom.repositories

import org.delcom.entities.Zodiac

interface IZodiacRepository {
    suspend fun getZodiacs(search: String): List<Zodiac>
    suspend fun getZodiacById(id: String): Zodiac?
    suspend fun getZodiacByNamaUmum(namaUmum: String): Zodiac?
    suspend fun addZodiac(zodiac: Zodiac): String
    suspend fun updateZodiac(id: String, newZodiac: Zodiac): Boolean
    suspend fun removeZodiac(id: String): Boolean
}