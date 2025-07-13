package com.michu_tech.swiss_budget.backend.framework

import io.ktor.server.websocket.*
import io.ktor.websocket.*

val handleAutoCompleteConnection: suspend DefaultWebSocketServerSession.() -> Unit = {
    for (frame in incoming) {
        if (frame is Frame.Text) {
            val text = frame.readText()
            outgoing.send(Frame.Text("YOU SAID: $text"))
            if (text.equals("bye", ignoreCase = true)) {
                close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
            }
        }
    }
}