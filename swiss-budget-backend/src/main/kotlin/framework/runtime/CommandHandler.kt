package com.michu_tech.swiss_budget.backend.framework.runtime

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.get

@Serializable
data class CommandBody(val command: String)

fun Route.postExecuteCommand() {
    post("/command") {
        val body: CommandBody = call.receive()
        val commandStore: CommandStore = call.application.get<CommandStore>()

        val command = parseCommand(body.command, commandStore)
        if (!command.isComplete()) {
            call.respond(HttpStatusCode.OK, "Command $command is missing required parameters")
            return@post
        }

        call.respond(HttpStatusCode.OK, command)
    }
}