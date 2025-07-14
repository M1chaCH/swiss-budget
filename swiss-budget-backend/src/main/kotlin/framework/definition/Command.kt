package com.michu_tech.swiss_budget.backend.framework.definition

import com.michu_tech.swiss_budget.backend.framework.runtime.ConcreteOptions
import io.ktor.http.*
import kotlinx.serialization.Serializable

data class Command(
    override val keyword: String,
    override val name: String,
    override val description: String
) : SelfDescribingNode {
    val options: MutableList<Option> = mutableListOf()
    var parent: Directory? = null
    var runFunction: RunCommandFunction = { CommandResult(HttpStatusCode.NotImplemented, CommandResultBody(error = "Not Implemented!")) }
}

typealias RunCommandFunction = suspend (options: ConcreteOptions) -> CommandResult

data class CommandResult(
    val statusCode: HttpStatusCode,
    val body: CommandResultBody,
)

@Serializable
data class CommandResultBody(
    val table: Map<String, List<String>>? = null,
    val prequel: String? = null,
    val sequel: String? = null,
    val error: String? = null,
)

fun Command.action(block: RunCommandFunction) {
    this.runFunction = block
}

fun Command.add(o: Option) {
    this.options.add(o)
}