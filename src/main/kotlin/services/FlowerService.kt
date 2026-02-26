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

class FlowerService(private val flowerRepository: IFlowerRepository) {

    // ── GET /flowers?search= ─────────────────────────────────────────────────
    suspend fun getAllFlowers(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""
        val flowers = flowerRepository.getFlowers(search)

        call.respond(
            DataResponse(
                status  = "success",
                message = "Berhasil mengambil daftar bahasa bunga",
                data    = mapOf("flowers" to flowers),
            )
        )
    }

    // ── GET /flowers/{id} ────────────────────────────────────────────────────
    suspend fun getFlowerById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID bunga tidak boleh kosong!")

        val flower = flowerRepository.getFlowerById(id)
            ?: throw AppException(404, "Data bunga tidak ditemukan!")

        call.respond(
            DataResponse(
                status  = "success",
                message = "Berhasil mengambil data bunga",
                data    = mapOf("flower" to flower),
            )
        )
    }

    // ── Parse multipart request ──────────────────────────────────────────────
    private suspend fun getFlowerRequest(call: ApplicationCall): FlowerRequest {
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
        v.required("namaUmum",   "Nama umum tidak boleh kosong")
        v.required("namaLatin",  "Nama latin tidak boleh kosong")
        v.required("makna",      "Makna tidak boleh kosong")
        v.required("asalBudaya", "Asal budaya tidak boleh kosong")
        v.required("deskripsi",  "Deskripsi tidak boleh kosong")
        v.required("pathGambar", "Gambar tidak boleh kosong")
        v.validate()

        val file = File(req.pathGambar)
        if (!file.exists()) {
            throw AppException(400, "Gambar bunga gagal diupload!")
        }
    }

    // ── POST /flowers ────────────────────────────────────────────────────────
    suspend fun createFlower(call: ApplicationCall) {
        val req = getFlowerRequest(call)
        validateFlowerRequest(req)

        // Cek duplikasi nama umum
        val existing = flowerRepository.getFlowerByNamaUmum(req.namaUmum)
        if (existing != null) {
            File(req.pathGambar).takeIf { it.exists() }?.delete()
            throw AppException(409, "Bunga dengan nama umum ini sudah terdaftar!")
        }

        val flowerId = flowerRepository.addFlower(req.toEntity())

        call.respond(
            DataResponse(
                status  = "success",
                message = "Berhasil menambahkan data bunga",
                data    = mapOf("flowerId" to flowerId),
            )
        )
    }

    // ── PUT /flowers/{id} ────────────────────────────────────────────────────
    suspend fun updateFlower(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID bunga tidak boleh kosong!")

        val oldFlower = flowerRepository.getFlowerById(id)
            ?: throw AppException(404, "Data bunga tidak ditemukan!")

        val req = getFlowerRequest(call)

        // Pertahankan gambar lama jika tidak ada upload baru
        if (req.pathGambar.isEmpty()) {
            req.pathGambar = oldFlower.pathGambar
        }

        validateFlowerRequest(req)

        // Cek duplikasi nama umum (hanya jika nama berubah)
        if (req.namaUmum != oldFlower.namaUmum) {
            val existing = flowerRepository.getFlowerByNamaUmum(req.namaUmum)
            if (existing != null) {
                File(req.pathGambar).takeIf { it.exists() }?.delete()
                throw AppException(409, "Bunga dengan nama umum ini sudah terdaftar!")
            }
        }

        // Hapus gambar lama jika ada gambar baru
        if (req.pathGambar != oldFlower.pathGambar) {
            File(oldFlower.pathGambar).takeIf { it.exists() }?.delete()
        }

        val isUpdated = flowerRepository.updateFlower(id, req.toEntity())
        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui data bunga!")
        }

        call.respond(
            DataResponse(
                status  = "success",
                message = "Berhasil mengubah data bunga",
                data    = null,
            )
        )
    }

    // ── DELETE /flowers/{id} ─────────────────────────────────────────────────
    suspend fun deleteFlower(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID bunga tidak boleh kosong!")

        val oldFlower = flowerRepository.getFlowerById(id)
            ?: throw AppException(404, "Data bunga tidak ditemukan!")

        val oldFile = File(oldFlower.pathGambar)

        val isDeleted = flowerRepository.removeFlower(id)
        if (!isDeleted) {
            throw AppException(400, "Gagal menghapus data bunga!")
        }

        oldFile.takeIf { it.exists() }?.delete()

        call.respond(
            DataResponse(
                status  = "success",
                message = "Berhasil menghapus data bunga",
                data    = null,
            )
        )
    }

    // ── GET /flowers/{id}/image ──────────────────────────────────────────────
    suspend fun getFlowerImage(call: ApplicationCall) {
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
