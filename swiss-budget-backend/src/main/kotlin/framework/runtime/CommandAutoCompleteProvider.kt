package com.michu_tech.swiss_budget.backend.framework.runtime

import com.michu_tech.swiss_budget.backend.framework.definition.Command
import com.michu_tech.swiss_budget.backend.framework.definition.Directory
import com.michu_tech.swiss_budget.backend.framework.definition.Option
import com.michu_tech.swiss_budget.backend.framework.definition.OptionType

private enum class CommandAutoCompleteProviderState {
    Start,
    Command,
    OptionDefinition,
    QuoteOptionValue,
    OpenOptionValue,
    Done,
}

class CommandAutoCompleteProvider(
    private val store: CommandStore,
) {
    private val equal = '='
    private val dash = '-'
    private val quote = '"'
    private val space = ' '
    private val nullChar = '\u0000'

    private var alreadyGoodInput = ""
    private val buffer = mutableListOf<Char>()
    private val result = mutableListOf<String>()
    private lateinit var currentDir: Directory

    private var state = CommandAutoCompleteProviderState.Start
    private var commandDefinition: Command? = null
    private val alreadyDefinedOptions = mutableListOf<String>()
    private var longOptionDefinition = false
    private var currentOption: Option? = null

    suspend fun getAutocompleteSuggestions(input: String): List<String> {
        reset()

        input.trimStart().plus(nullChar).forEach { char ->
            state = when (state) {
                CommandAutoCompleteProviderState.Start -> handleStart(char)
                CommandAutoCompleteProviderState.Command -> handleCommand(char)
                CommandAutoCompleteProviderState.OptionDefinition -> handleOptionDefinition(char)
                CommandAutoCompleteProviderState.QuoteOptionValue -> handleQuoteOptionValue(char)
                CommandAutoCompleteProviderState.OpenOptionValue -> handleOpenOptionValue(char)
                CommandAutoCompleteProviderState.Done -> CommandAutoCompleteProviderState.Done
            }
        }

        return result
    }

    private fun handleStart(next: Char): CommandAutoCompleteProviderState {
        if (next == nullChar) {
            result.addAll(currentDir.childDirectories.keys.map { "$it " })
            result.addAll(currentDir.childCommands.keys.map { "$it " })
            return CommandAutoCompleteProviderState.Done
        }

        buffer.add(next)
        return CommandAutoCompleteProviderState.Command
    }

    private fun handleCommand(next: Char): CommandAutoCompleteProviderState {
        if (buffer.isEmpty() && next == space) {
            return CommandAutoCompleteProviderState.Command
        }

        if (next == nullChar) {
            val filter = flushBuffer()
            result.addAll(currentDir.childDirectories.keys.filter { it.startsWith(filter, true) }.map { "$alreadyGoodInput$it " })
            result.addAll(currentDir.childCommands.keys.filter { it.startsWith(filter, true) }.map { "$alreadyGoodInput$it " })
            return CommandAutoCompleteProviderState.Done
        }

        if (next == space) {
            val token = flushBuffer()

            val nextDirectory = currentDir.childDirectories[token]
            if (nextDirectory == null) {
                val nextCommand = currentDir.childCommands[token]
                if (nextCommand == null) {
                    return CommandAutoCompleteProviderState.Done
                }

                commandDefinition = nextCommand
                alreadyGoodInput += nextCommand.keyword + " "
                return CommandAutoCompleteProviderState.OptionDefinition
            }

            currentDir = nextDirectory
            alreadyGoodInput = currentDir.keyword + " "
            return CommandAutoCompleteProviderState.Command
        }

        buffer.add(next)
        return CommandAutoCompleteProviderState.Command
    }

    private fun handleOptionDefinition(next: Char): CommandAutoCompleteProviderState {
        // skip leading spaces
        if (buffer.isEmpty() && next == space) {
            return CommandAutoCompleteProviderState.OptionDefinition
        }

        // no CommandDefinition -> invalid state, initially no dash -> wrong syntax
        if (commandDefinition == null) {
            return CommandAutoCompleteProviderState.Done
        }

        // done -> find suggestions
        if (next == nullChar) {
            val optionFilter = replaceLeadingDashes(flushBuffer())
            val matchingOptions = getOptionSuggestions(optionFilter)
            result.addAll(matchingOptions)
            return CommandAutoCompleteProviderState.Done
        }

        if (buffer.isEmpty() && next != dash) {
            return CommandAutoCompleteProviderState.Done
        }

        // "--" -> long options
        if (buffer.size == 1 && next == dash) {
            longOptionDefinition = true
            buffer.add(next)
            return CommandAutoCompleteProviderState.OptionDefinition
        }

        // option definition done -> find option in command definition
        if (next == equal || next == space) {
            val keyword = replaceLeadingDashes(flushBuffer())
            currentOption = commandDefinition!!.options.find { o ->
                if (longOptionDefinition) {
                    o.longKeyword == keyword
                } else {
                    o.shortKeyword == keyword
                }
            }

            if (currentOption == null) {
                return CommandAutoCompleteProviderState.Done
            }

            alreadyGoodInput += if (longOptionDefinition) "--" else "-"
            alreadyGoodInput += keyword
            return if (next == equal) CommandAutoCompleteProviderState.QuoteOptionValue else CommandAutoCompleteProviderState.OpenOptionValue
        }

        // default -> fill buffer and continue
        buffer.add(next)
        return CommandAutoCompleteProviderState.OptionDefinition
    }

    private suspend fun handleQuoteOptionValue(next: Char): CommandAutoCompleteProviderState {
        // no option defined -> invalid state && must start with "
        if (currentOption == null || (buffer.isEmpty() && next != quote)) {
            return CommandAutoCompleteProviderState.Done
        }

        // done -> find suggestions
        if (next == nullChar) {
            val filter = flushBuffer().substring(1) // skip leading "
            val suggestedOptionValues = getSuggestedValuesForCurrentOption(filter)
            result.addAll(suggestedOptionValues.map {
                "$alreadyGoodInput$equal$quote$it$quote "
            })
            return CommandAutoCompleteProviderState.Done
        }

        // value defined -> mark as defined and continue
        if (buffer.isNotEmpty() && next == quote) {
            val valueInput = flushBuffer()
            alreadyGoodInput += "$equal$valueInput$quote "
            alreadyDefinedOptions.add(currentOption!!.longKeyword)
            return CommandAutoCompleteProviderState.OptionDefinition
        }

        // default -> add to buffer and continue
        buffer.add(next)
        return CommandAutoCompleteProviderState.QuoteOptionValue
    }

    private suspend fun handleOpenOptionValue(next: Char): CommandAutoCompleteProviderState {
        if (currentOption == null) {
            return CommandAutoCompleteProviderState.Done
        }

        if (buffer.isEmpty() && next == space) {
            return CommandAutoCompleteProviderState.OpenOptionValue
        }

        if (next == nullChar) {
            val filter = flushBuffer()
            val suggestedOptionValues = getSuggestedValuesForCurrentOption(filter)

            if (suggestedOptionValues.isNotEmpty()) {
                alreadyGoodInput += " "
                result.addAll(suggestedOptionValues.map { "$alreadyGoodInput$it " })
                return CommandAutoCompleteProviderState.Done
            }

            alreadyGoodInput += " ${filter.trim()} "
            alreadyDefinedOptions.add(currentOption!!.longKeyword)
            val suggestedOptions = getOptionSuggestions("")
            result.addAll(suggestedOptions)
            return CommandAutoCompleteProviderState.Done
        }

        if (next == dash) {
            val valueInput = flushBuffer()
            alreadyGoodInput += " ${valueInput.trim()} "
            alreadyDefinedOptions.add(currentOption!!.longKeyword)
            state = CommandAutoCompleteProviderState.OptionDefinition
            longOptionDefinition = false
            currentOption = null
            return handleOptionDefinition(next)
        }

        buffer.add(next)
        return CommandAutoCompleteProviderState.OpenOptionValue
    }

    private fun getOptionSuggestions(filter: String): List<String> {
        if (commandDefinition == null) {
            return emptyList()
        }

        return commandDefinition!!.options
            .filter {
                filter.isBlank() || it.longKeyword.startsWith(filter, true) ||
                        it.shortKeyword.startsWith(filter, true)
            }
            .filter {
                !alreadyDefinedOptions.contains(it.longKeyword)
            }
            .map { alreadyGoodInput + "--" + it.longKeyword + "=$quote" }
    }

    private suspend fun getSuggestedValuesForCurrentOption(filter: String): List<String> {
        if (currentOption == null) {
            return emptyList()
        }

        val suggestedValues = mutableListOf<String>()
        if (currentOption!!.valueSuggestionLoader != null) {
            val dynamicResult = currentOption!!.valueSuggestionLoader?.invoke(filter) ?: listOf()
            suggestedValues.addAll(dynamicResult)
        } else if (currentOption!!.type == OptionType.Flag && !currentOption!!.isList) {
            if ("true".startsWith(filter, true)) {
                suggestedValues.add("true")
            }
            if ("false".startsWith(filter, true)) {
                suggestedValues.add("false")
            }
        }

        return suggestedValues
    }

    private fun flushBuffer(): String {
        val value = buffer.joinToString("")
        buffer.clear()
        return value
    }

    private fun reset() {
        currentDir = store.getRoot()
        buffer.clear()
        longOptionDefinition = false
        state = CommandAutoCompleteProviderState.Start
        alreadyGoodInput = ""
        result.clear()
        alreadyDefinedOptions.clear()
        commandDefinition = null
        currentOption = null
    }
}