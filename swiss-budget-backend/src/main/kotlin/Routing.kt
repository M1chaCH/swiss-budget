package com.michu_tech.swiss_budget.backend

import com.michu_tech.swiss_budget.backend.framework.runtime.postExecuteCommand
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, "hello world")
        }
        postExecuteCommand()
    }
}