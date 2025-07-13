package com.michu_tech.swiss_budget.backend

import com.michu_tech.swiss_budget.backend.app.SwissBudgetInitializer
import io.ktor.server.application.*
import org.koin.core.context.GlobalContext.get

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // APP
    configureDependencyInjection()

    val initializer: SwissBudgetInitializer = get().get()
    initializer.setupApp()

    // WEB
    configureMonitoring() // TODO
//    configureSecurity() // TODO
    configureHTTP()
    configureRouting()
    configureSockets()
}
