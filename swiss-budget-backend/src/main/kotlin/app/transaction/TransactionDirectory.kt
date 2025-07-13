package com.michu_tech.swiss_budget.backend.app.transaction

import com.michu_tech.swiss_budget.backend.framework.definition.Directory
import com.michu_tech.swiss_budget.backend.framework.definition.create

fun configureTransactionDirectory(): Directory {
    return create(Directory("transaction", "Transactions", "Transactions are the records of every transaction made by the user")) {

    }
}