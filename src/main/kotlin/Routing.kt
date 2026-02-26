package org.delcom

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.data.AppException
import org.delcom.data.ErrorResponse
import org.delcom.helpers.parseMessageToMap
import org.delcom.services.ProfileService
import org.delcom.services.ZodiacService
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val zodiacService: ZodiacService by inject()
    val profileService: ProfileService by inject()

    install(StatusPages) {
        exception<AppException> { call, cause ->
            val dataMap = parseMessageToMap(cause.message)
            call.respond(
                HttpStatusCode.fromValue(cause.code),
                ErrorResponse(
                    status  = "fail",
                    message = if (dataMap.isEmpty()) cause.message else "Data yang dikirimkan tidak valid!",
                    data    = if (dataMap.isEmpty()) null else dataMap.toString()
                )
            )
        }
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(status = "error", message = cause.message ?: "Unknown error", data = "")
            )
        }
    }

    routing {
        get("/") {
            call.respondText("API Zodiak berjalan. Dibuat oleh Zefanya Ecklezia Saragih.")
        }

        // ── Zodiacs ──────────────────────────────────────────────────────────
        route("/zodiacs") {
            get          { zodiacService.getAllZodiacs(call) }
            post         { zodiacService.createZodiac(call) }
            get("/{id}") { zodiacService.getZodiacById(call) }
            put("/{id}") { zodiacService.updateZodiac(call) }
            delete("/{id}") { zodiacService.deleteZodiac(call) }
            get("/{id}/image") { zodiacService.getZodiacImage(call) }
        }

        // ── Profile ───────────────────────────────────────────────────────────
        route("/profile") {
            get          { profileService.getProfile(call) }
            get("/photo") { profileService.getProfilePhoto(call) }
        }
    }
}