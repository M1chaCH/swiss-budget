package com.michu_tech.swiss_budget.backend

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSockets()
    configureFrameworks()
    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureSecurity()
    configureHTTP()
    configureRouting()
}
