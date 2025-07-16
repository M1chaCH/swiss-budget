package com.michu_tech.swiss_budget.backend

import com.michu_tech.swiss_budget.backend.app.SwissBudgetInitializer
import com.michu_tech.swiss_budget.backend.framework.runtime.CommandAutoCompleteProvider
import com.michu_tech.swiss_budget.backend.framework.runtime.CommandParser
import com.michu_tech.swiss_budget.backend.framework.runtime.CommandStore
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDependencyInjection() {
    install(Koin) {
        slf4jLogger()
        modules(module {
            // Framework
            single<CommandStore> { CommandStore() }
            factory<CommandParser> { CommandParser(get()) }
            factory<CommandAutoCompleteProvider> { CommandAutoCompleteProvider(get()) }

            // SwissBudget
            single { SwissBudgetInitializer(get()) }
        })
    }
}
