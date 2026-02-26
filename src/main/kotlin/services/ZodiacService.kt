package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.FlowerRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IFlowerRepository
import java.io.File
import java.util.UUID

class ZodiacService(private val flowerRepository: IFlowerRepository) {

    // ── GET /flowers?search= ─────────────────────────────────────────────────
    suspend fun getAllZodiacs(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""
        val flowers = flowerRepository.getFlowers(search)

        call.respond(
            DataResponse(
                status  = "success",
                message = "Berhasil mengambil daftar data zodiak",
                data    = mapOf("flowers" to flowers),
            )
        )
    }

    // ── GET /flowers/{id} ────────────────────────────────────────────────────
    suspend fun getZodiacById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID zodiak tidak boleh kosong!")

        val flower = flowerRepository.getFlowerById(id)
            ?: throw AppException(404, "Data zodiak tidak ditemukan!")

        call.respond(
            DataResponse(
                status  = "success",
                message = "Berhasil mengambil data zodiak",
                data    = mapOf("flower" to flower),
            )
        )
    }

    // ── Parse multipart request ──────────────────────────────────────────────
    private suspend fun getZodiacRequest(call: ApplicationCall): FlowerRequest {
        val req = FlowerRequest()

        val multipart = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "namaUmum"   -> req.namaUmum   = part.value.trim()
                        "namaLatin"  -> req.namaLatin  = part.value.trim()
                        "makna"      -> req.makna      = part.value.trim()
                        "asalBudaya" -> req.asalBudaya = part.value.trim()
                        "deskripsi"  -> req.deskripsi  = part.value
                    }
                }

                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/flowers/$fileName"

                    val file = File(filePath)
                    file.parentFile.mkdirs()

                    part.provider().copyAndClose(file.writeChannel())
                    req.pathGambar = filePath
                }

                else -> {}
            }
            part.dispose()
        }

        return req
    }

    // ── Validate request ─────────────────────────────────────────────────────
    private fun validateFlowerRequest(req: FlowerRequest) {
        val v = ValidatorHelper(req.toMap())
        v.required("namaUmum",   "Nama zodiak tidak boleh kosong")
        v.required("namaLatin",  "Simbol/nama latin tidak boleh kosong")
        v.required("makna",      "Elemen/sifat tidak boleh kosong")
        v.required("asalBudaya", "Periode tanggal tidak boleh kosong")
        v.required("deskripsi",  "Deskripsi tidak boleh kosong")
        v.required("pathGambar", "Gambar tidak boleh kosong")
        v.validate()

        val file = File(req.pathGambar)
        if (!file.exists()) {
            throw AppException(400, "Gambar zodiak gagal diupload!")
        }
    }

    // ── POST /flowers ────────────────────────────────────────────────────────
    suspend fun createZodiac(call: ApplicationCall) {
        val req = getZodiacRequest(call)
        validateFlowerRequest(req)

        // Cek duplikasi nama
        val existing = flowerRepository.getFlowerByNamaUmum(req.namaUmum)
        if (existing != null) {
            File(req.pathGambar).takeIf { it.exists() }?.delete()
            throw AppException(409, "Zodiak dengan nama ini sudah terdaftar!")
        }

        val flowerId = flowerRepository.addFlower(req.toEntity())

        call.respond(
            DataResponse(
                status  = "success",
                message = "Berhasil menambahkan data zodiak",
                data    = mapOf("flowerId" to flowerId),
            )
        )
    }

    // ── PUT /flowers/{id} ────────────────────────────────────────────────────
    suspend fun updateZodiac(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID zodiak tidak boleh kosong!")

        val oldFlower = flowerRepository.getFlowerById(id)
            ?: throw AppException(404, "Data zodiak tidak ditemukan!")

        val req = getZodiacRequest(call)

        // Pertahankan gambar lama jika tidak ada upload baru
        if (req.pathGambar.isEmpty()) {
            req.pathGambar = oldFlower.pathGambar
        }

        validateFlowerRequest(req)

        // Cek duplikasi nama (hanya jika nama berubah)
        if (req.namaUmum != oldFlower.namaUmum) {
            val existing = flowerRepository.getFlowerByNamaUmum(req.namaUmum)
            if (existing != null) {
                File(req.pathGambar).takeIf { it.exists() }?.delete()
                throw AppException(409, "Zodiak dengan nama ini sudah terdaftar!")
            }
        }

        // Hapus gambar lama jika ada gambar baru
        if (req.pathGambar != oldFlower.pathGambar) {
            File(oldFlower.pathGambar).takeIf { it.exists() }?.delete()
        }

        val isUpdated = flowerRepository.updateFlower(id, req.toEntity())
        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui data zodiak!")
        }

        call.respond(
            DataResponse(
                status  = "success",
                message = "Berhasil mengubah data zodiak",
                data    = null,
            )
        )
    }

    // ── DELETE /flowers/{id} ─────────────────────────────────────────────────
    suspend fun deleteZodiac(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID zodiak tidak boleh kosong!")

        val oldFlower = flowerRepository.getFlowerById(id)
            ?: throw AppException(404, "Data zodiak tidak ditemukan!")

        val oldFile = File(oldFlower.pathGambar)

        val isDeleted = flowerRepository.removeFlower(id)
        if (!isDeleted) {
            throw AppException(400, "Gagal menghapus data zodiak!")
        }

        oldFile.takeIf { it.exists() }?.delete()

        call.respond(
            DataResponse(
                status  = "success",
                message = "Berhasil menghapus data zodiak",
                data    = null,
            )
        )
    }

    // ── GET /flowers/{id}/image ──────────────────────────────────────────────
    suspend fun getZodiacImage(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: return call.respond(HttpStatusCode.BadRequest)

        val flower = flowerRepository.getFlowerById(id)
            ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(flower.pathGambar)
        if (!file.exists()) {
            return call.respond(HttpStatusCode.NotFound)
        }

        call.respondFile(file)
    }
}