package com.michu_tech.swiss_budget.backend.framework.definition

data class Option(
    val shortKeyword: String,
    val longKeyword: String,
    val name: String,
    val description: String,
    val prompt: String,
    val required: Boolean = true,
    val defaultValue: Any? = null,
    val isList: Boolean = false,
    val type: OptionType = OptionType.Text,
    val valueSuggestionLoader: DynamicOptionValueSuggestionLoader? = null,
)

enum class OptionType {
    Number,
    Text,
    Flag,
}

typealias DynamicOptionValueSuggestionLoader = suspend (String) -> List<String>
