package com.michu_tech.swiss_budget.backend.framework.runtime

import com.michu_tech.swiss_budget.backend.framework.definition.Option
import com.michu_tech.swiss_budget.backend.framework.definition.RunCommandFunction
import com.michu_tech.swiss_budget.backend.framework.definition.SelfDescribingNode

data class ConcreteCommand(
    override val keyword: String,
    override val name: String,
    override val description: String,
    val runFunction: RunCommandFunction,
    val args: ConcreteOptions,
    val missingOptions: List<Option>,
) : SelfDescribingNode {
    fun isComplete(): Boolean {
        return missingOptions.none { it.required }
    }
}
