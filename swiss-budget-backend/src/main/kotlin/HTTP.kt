package com.michu_tech.swiss_budget.backend

import com.michu_tech.swiss_budget.backend.framework.runtime.CommandParsingException
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        anyHost() // TODO
    }
    install(Compression)
    install(ContentNegotiation) {
        json()
    }
    install(StatusPages) {
        exception<CommandParsingException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, "Your command could not be parsed: ${cause.message}")
        }
        exception<Throwable> { call, cause ->
            // TODO maybe don't give this much detail in production
            call.respond(HttpStatusCode.InternalServerError, "${cause.javaClass.name}: ${cause.message}")
        }
    }
}
