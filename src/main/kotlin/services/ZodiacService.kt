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
import org.delcom.data.ZodiacRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IZodiacRepository
import java.io.File
import java.util.UUID

class ZodiacService(private val zodiacRepository: IZodiacRepository) {

    // GET /zodiacs?search=
    suspend fun getAllZodiacs(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""
        val zodiacs = zodiacRepository.getZodiacs(search)
        call.respond(
            DataResponse("success", "Berhasil mengambil daftar zodiak", mapOf("zodiacs" to zodiacs))
        )
    }

    // GET /zodiacs/{id}
    suspend fun getZodiacById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID zodiak tidak boleh kosong!")
        val zodiac = zodiacRepository.getZodiacById(id)
            ?: throw AppException(404, "Data zodiak tidak tersedia!")
        call.respond(
            DataResponse("success", "Berhasil mengambil data zodiak", mapOf("zodiac" to zodiac))
        )
    }

    // Parse multipart form — shared by create & update
    private suspend fun getZodiacRequest(call: ApplicationCall): ZodiacRequest {
        val req = ZodiacRequest()
        val multipart = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> when (part.name) {
                    "nama"          -> req.nama          = part.value.trim()
                    "simbol"        -> req.simbol        = part.value.trim()
                    "elemen"        -> req.elemen        = part.value.trim()
                    "tanggalLahir"  -> req.tanggalLahir  = part.value.trim()
                    "deskripsi"     -> req.deskripsi     = part.value
                    "karakteristik" -> req.karakteristik = part.value
                    "kecocokan"     -> req.kecocokan     = part.value
                }
                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" } ?: ""
                    val fileName   = "${UUID.randomUUID()}$ext"
                    val filePath   = "uploads/zodiacs/$fileName"
                    val file       = File(filePath)
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

    // Validate — pathGambar required only for create (caller passes requireImage flag via req state)
    private fun validateZodiacRequest(req: ZodiacRequest, requireImage: Boolean = true) {
        val v = ValidatorHelper(req.toMap())
        v.required("nama",          "Nama tidak boleh kosong")
        v.required("simbol",        "Simbol tidak boleh kosong")
        v.required("elemen",        "Elemen tidak boleh kosong")
        v.required("tanggalLahir",  "Tanggal lahir tidak boleh kosong")
        v.required("deskripsi",     "Deskripsi tidak boleh kosong")
        v.required("karakteristik", "Karakteristik tidak boleh kosong")
        v.required("kecocokan",     "Kecocokan tidak boleh kosong")
        if (requireImage) v.required("pathGambar", "Gambar tidak boleh kosong")
        v.validate()

        if (req.pathGambar.isNotEmpty() && !File(req.pathGambar).exists()) {
            throw AppException(400, "Gambar zodiak gagal diupload!")
        }
    }

    // POST /zodiacs
    suspend fun createZodiac(call: ApplicationCall) {
        val req = getZodiacRequest(call)
        validateZodiacRequest(req, requireImage = true)

        val existing = zodiacRepository.getZodiacByName(req.nama)
        if (existing != null) {
            File(req.pathGambar).takeIf { it.exists() }?.delete()
            throw AppException(409, "Zodiak dengan nama ini sudah terdaftar!")
        }

        val zodiacId = zodiacRepository.addZodiac(req.toEntity())
        call.respond(
            DataResponse("success", "Berhasil menambahkan data zodiak", mapOf("zodiacId" to zodiacId))
        )
    }

    // PUT /zodiacs/{id}
    suspend fun updateZodiac(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID zodiak tidak boleh kosong!")
        val oldZodiac = zodiacRepository.getZodiacById(id)
            ?: throw AppException(404, "Data zodiak tidak tersedia!")

        val req = getZodiacRequest(call)

        // If no new image uploaded, keep the old one
        if (req.pathGambar.isEmpty()) {
            req.pathGambar = oldZodiac.pathGambar
        }

        validateZodiacRequest(req, requireImage = false)

        // Check name uniqueness only when name changed
        if (req.nama != oldZodiac.nama) {
            val existing = zodiacRepository.getZodiacByName(req.nama)
            if (existing != null) {
                File(req.pathGambar).takeIf { it.exists() && req.pathGambar != oldZodiac.pathGambar }?.delete()
                throw AppException(409, "Zodiak dengan nama ini sudah terdaftar!")
            }
        }

        // Delete old image if replaced
        if (req.pathGambar != oldZodiac.pathGambar) {
            File(oldZodiac.pathGambar).takeIf { it.exists() }?.delete()
        }

        val updated = zodiacRepository.updateZodiac(id, req.toEntity())
        if (!updated) throw AppException(400, "Gagal memperbarui data zodiak!")

        call.respond(DataResponse<Nothing>("success", "Berhasil mengubah data zodiak"))
    }

    // DELETE /zodiacs/{id}
    suspend fun deleteZodiac(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID zodiak tidak boleh kosong!")
        val oldZodiac = zodiacRepository.getZodiacById(id)
            ?: throw AppException(404, "Data zodiak tidak tersedia!")

        val deleted = zodiacRepository.removeZodiac(id)
        if (!deleted) throw AppException(400, "Gagal menghapus data zodiak!")

        File(oldZodiac.pathGambar).takeIf { it.exists() }?.delete()
        call.respond(DataResponse<Nothing>("success", "Berhasil menghapus data zodiak"))
    }

    // GET /zodiacs/{id}/image
    suspend fun getZodiacImage(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: return call.respond(HttpStatusCode.BadRequest)
        val zodiac = zodiacRepository.getZodiacById(id)
            ?: return call.respond(HttpStatusCode.NotFound)
        val file = File(zodiac.pathGambar)
        if (!file.exists()) return call.respond(HttpStatusCode.NotFound)
        call.respondFile(file)
    }
}