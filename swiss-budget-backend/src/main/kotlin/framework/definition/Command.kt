package com.michu_tech.swiss_budget.backend.framework.definition

import com.michu_tech.swiss_budget.backend.framework.runtime.ConcreteOptions

data class Command(
    override val keyword: String,
    override val name: String,
    override val description: String
) : SelfDescribingNode {
    val options: MutableList<Option> = mutableListOf()
    var parent: Directory? = null
    var runFunction: RunCommandFunction = { CommandResult(error = "Not Implemented!") }
}

typealias RunCommandFunction = suspend (options: ConcreteOptions) -> CommandResult

data class CommandResult(
    val table: Map<String, List<String>>? = null,
    val prequel: String? = null,
    val sequel: String? = null,
    val error: String? = null
)

fun Command.action(block: RunCommandFunction) {
    this.runFunction = block
}

fun Command.add(o: Option) {
    this.options.add(o)
}