package com.michu_tech.swiss_budget.backend.app

import com.michu_tech.swiss_budget.backend.app.tag.configureTagDirectory
import com.michu_tech.swiss_budget.backend.app.transaction.configureTransactionDirectory
import com.michu_tech.swiss_budget.backend.framework.definition.Command
import com.michu_tech.swiss_budget.backend.framework.definition.Directory
import com.michu_tech.swiss_budget.backend.framework.definition.add
import com.michu_tech.swiss_budget.backend.framework.definition.create
import com.michu_tech.swiss_budget.backend.framework.runtime.CommandStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SwissBudgetInitializer(
    private val commandStore: CommandStore,
) {
    private val logger: Logger = LoggerFactory.getLogger(SwissBudgetInitializer::class.java)

    fun setupApp() {
        logger.info("Initializing SwissBudget...")
        commandStore.initialize(createRootDirectory())
        logger.info("SwissBudget initialized")
    }

    private fun createRootDirectory(): Directory {
        return create(Directory("", "Root", "Root Directory")) {
            add(Command("tags", "Tags", "Show all Tags")) {
            }

            add(configureTagDirectory()) {}
            add(configureTransactionDirectory()) {}
        }
    }
}