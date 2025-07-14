package com.michu_tech.swiss_budget.backend.app.tag

import com.michu_tech.swiss_budget.backend.framework.definition.CommandResult
import com.michu_tech.swiss_budget.backend.framework.definition.CommandResultPage
import com.michu_tech.swiss_budget.backend.framework.runtime.ConcreteOptions
import com.michu_tech.swiss_budget.backend.framework.runtime.extract
import com.michu_tech.swiss_budget.backend.framework.runtime.extractOptional
import io.ktor.http.*

suspend fun createTag(options: ConcreteOptions): CommandResult {
    val name = options.extract<String>("name")
    val keywords = options.extract<List<String>>("keywords")
    val apply = options.extractOptional<Boolean>("apply") ?: true

    var message = "Created '$name' tag with keywords:"
    keywords.forEach { message += "\n- $it" }

    if (!apply) {
        message += "\n\nTag will not be applied to existing transactions"
    }

    return CommandResult(HttpStatusCode.OK, CommandResultPage(title = message))
}
