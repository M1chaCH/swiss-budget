package com.michu_tech.swiss_budget.backend.framework.runtime

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.get


@Serializable
data class CommandBody(val command: String)

fun Route.postExecuteCommand() {
    post("/command") {
        val requestBody: CommandBody = call.receive()
        val parser: CommandParser = call.application.get<CommandParser>()
        val command = parser.parse(requestBody.command)

        if (command.isComplete()) {
            val result = command.runFunction(command.args)
            call.respond(HttpStatusCode.OK, result.body)
        } else {
            var prompt = command.missingOptions.first { it.required }.prompt.trim()
            if (!prompt.endsWith("?")) prompt += "?"

            val result = """
                You need to give some more information to the command: ${command.name}
                $prompt
            """.trimIndent()

            call.respond(HttpStatusCode.Accepted, result)
        }
    }
}

fun Route.wsCommandAutoComplete() {
    webSocket("/command/autocomplete") {
        val closingInput = "---exit---"
        val autocomplete: CommandAutoCompleteProvider = call.application.get<CommandAutoCompleteProvider>()

        for (frame in incoming) {
            if (frame is Frame.Close) {
                close(CloseReason(CloseReason.Codes.NORMAL, "Connection closed"))
                continue
            }
            
            require(frame is Frame.Text) { "Text expected" }

            val input = frame.readText()
            if (input == closingInput) {
                close(CloseReason(CloseReason.Codes.NORMAL, "Connection closed"))
                continue
            }

            val result = autocomplete.getAutocompleteSuggestions(input)
            val resultJson = Json.encodeToString(result)
            outgoing.send(Frame.Text(resultJson))
        }
    }
}
