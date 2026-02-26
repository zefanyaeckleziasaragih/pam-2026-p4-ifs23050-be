package org.delcom.repositories

import org.delcom.entities.Flower

interface IFlowerRepository {
    suspend fun getFlowers(search: String): List<Flower>
    suspend fun getFlowerById(id: String): Flower?
    suspend fun getFlowerByNamaUmum(namaUmum: String): Flower?
    suspend fun addFlower(flower: Flower): String
    suspend fun updateFlower(id: String, newFlower: Flower): Boolean
    suspend fun removeFlower(id: String): Boolean
}
