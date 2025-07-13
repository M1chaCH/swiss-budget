package com.michu_tech.swiss_budget.backend.framework.runtime

import com.michu_tech.swiss_budget.backend.framework.definition.Command
import com.michu_tech.swiss_budget.backend.framework.definition.Directory
import com.michu_tech.swiss_budget.backend.framework.definition.Option
import com.michu_tech.swiss_budget.backend.framework.definition.OptionType

private enum class CommandParserState {
    Command,
    OptionDeclaration,
    QuotedOptionValue,
    OpenOptionValue,
}

class CommandParser(private val commandStore: CommandStore) {
    private val equal = '='
    private val dash = '-'
    private val quote = '"'
    private val space = ' '
    private val nullChar = '\u0000'

    private val buffer = mutableListOf<Char>()

    private lateinit var currentDir: Directory
    private lateinit var commandDefinition: Command

    private var currentState = CommandParserState.Command
    private var currentOptionDoubleDash = false
    private var currentOptionDefinition: Option? = null

    private val args = mutableMapOf<String, Any>()

    /**
     * Takes a string and parses it into a ConcreteCommand.
     * If no matching command could be found in the Store, then we throw.
     * <br>
     * Following are some input examples: <br>
     *   - tags
     *   - tags -o date
     *   - tags -o="date"
     *   - tags --order date
     *   - tags --order="date"
     *   - tag create -n Some Name
     *   - tag create -n="some name"
     *   - tag create --name some name
     *   - tag create --name="some name"
     *   - tag create -n some name --keywords some,cool , list, with gaps,and,more --split-commands="options with dashes" -en 12.4 --dash-in-option="only-when-wrapped-with-quotes" -d="--coolio--"
     * @param input the user input for a command.
     * @throws CommandParsingException whenever something goes wrong
     */
    fun parse(input: String): ConcreteCommand {
        reset()

        input.trim().plus(nullChar).forEach { char ->
            currentState = when (currentState) {
                CommandParserState.Command -> handleCommand(char)
                CommandParserState.OptionDeclaration -> handleOptionDeclaration(char)
                CommandParserState.QuotedOptionValue -> handleQuotedOptionValue(char)
                CommandParserState.OpenOptionValue -> handleOpenOptionValue(char)
            }
        }

        if (!::commandDefinition.isInitialized) {
            throw CommandParsingException("'$input' could not be parsed to command, no command found")
        }

        if (currentOptionDefinition != null) {
            val value = parseOptionValue(currentOptionDefinition!!, buffer.joinToString(""))
            args.put(currentOptionDefinition!!.longKeyword, value)
        }

        val missingOptions = commandDefinition.options.filter { definedOption ->
            definedOption.required && !args.containsKey(definedOption.longKeyword)
        }.toList()

        return ConcreteCommand(
            commandDefinition.keyword,
            commandDefinition.name,
            commandDefinition.description,
            commandDefinition.runFunction,
            args,
            missingOptions
        )
    }

    private fun handleCommand(next: Char): CommandParserState {
        return when {
            buffer.isEmpty() && next == space -> { // first char is empty -> skip
                CommandParserState.Command
            }

            buffer.isNotEmpty() && (next == space || next == nullChar) -> { // directory done
                val token = buffer.joinToString("")
                val nextDir = currentDir.childDirectories[token]

                // directory exists -> use
                if (nextDir != null) {
                    currentDir = nextDir
                    buffer.clear()
                    return CommandParserState.Command
                }

                // directory not found -> check commands
                val command = currentDir.childCommands[token]
                if (command == null) {
                    throw CommandParsingException("'$token' could not be found")
                }

                commandDefinition = command
                buffer.clear()
                return CommandParserState.OptionDeclaration
            }

            else -> {
                buffer.add(next)
                return CommandParserState.Command
            }
        }
    }

    private fun handleOptionDeclaration(next: Char): CommandParserState {
        // first char is empty -> skip
        if (buffer.isEmpty() && (next == space || next == nullChar)) {
            return CommandParserState.OptionDeclaration
        }

        // option declaration must start with a dash
        if (buffer.isEmpty() && next != dash) {
            throw CommandParsingException("Invalid syntax: Option expected, but option did not start with '-'")
        }

        if (buffer.size == 1 && next == dash) {
            currentOptionDoubleDash = true
            buffer.add(next)
            return CommandParserState.OptionDeclaration
        }

        if (next == space || next == equal || next == nullChar) {
            val keyword = replaceLeadingDashes(buffer.joinToString("").lowercase())
            currentOptionDefinition = commandDefinition.options.find { o ->
                if (currentOptionDoubleDash) {
                    o.longKeyword == keyword
                } else {
                    o.shortKeyword == keyword
                }
            }


            if (currentOptionDefinition == null) {
                throw CommandParsingException("'$keyword' is not a known option for command '${commandDefinition.keyword}'")
            }

            currentOptionDoubleDash = false
            buffer.clear()
            return if (next == equal) CommandParserState.QuotedOptionValue else CommandParserState.OpenOptionValue
        }

        buffer.add(next)
        return CommandParserState.OptionDeclaration
    }

    private fun handleOpenOptionValue(next: Char): CommandParserState {
        if (next == dash || next == nullChar) {
            val valueString = buffer.joinToString("").trim()
            val value = parseOptionValue(currentOptionDefinition!!, valueString)

            args.put(currentOptionDefinition!!.longKeyword, value)
            buffer.clear()
            currentOptionDefinition = null
            currentState = CommandParserState.OptionDeclaration
            return if (next == nullChar) CommandParserState.OptionDeclaration else handleOptionDeclaration(next)
        }

        buffer.add(next)
        return CommandParserState.OpenOptionValue
    }

    private fun handleQuotedOptionValue(next: Char): CommandParserState {
        if (buffer.isEmpty() && next != quote) {
            throw CommandParsingException("invalid syntax: when using the '-o=' notation you must wrap the argument in '\"'. (ex: -o=\"some value\" OR --option=\"some value\")")
        }

        if (buffer.isNotEmpty() && next == quote) {
            val valueString = buffer.joinToString("").trim().substring(1) // substring: remove first quote
            val value = parseOptionValue(currentOptionDefinition!!, valueString)

            args.put(currentOptionDefinition!!.longKeyword, value)
            buffer.clear()
            currentOptionDefinition = null
            return CommandParserState.OptionDeclaration
        }

        buffer.add(next)
        return CommandParserState.QuotedOptionValue
    }

    private fun reset() {
        currentState = CommandParserState.Command
        currentDir = commandStore.getRoot()
        currentOptionDoubleDash = false
        currentOptionDefinition = null
        buffer.clear()
    }

    // TODO respect the configured default value
    private fun parseOptionValue(option: Option, value: String): Any {
        return if (option.isList) {
            parseOptionListValue(option, value)
        } else {
            parseOptionSingleValue(option, value)
        }
    }

    private fun parseOptionSingleValue(option: Option, value: String): Any {
        val optionName = option.name

        return when (option.type) {
            OptionType.Text -> value.trim()
            OptionType.Number -> value.toDoubleOrNull()
                ?: throw CommandParsingException("Option: '$optionName' must be a number but was not.")

            OptionType.Flag -> value.isEmpty() || value.lowercase() == "true" || value.lowercase() == "on" || value == "1"
        }
    }

    private fun parseOptionListValue(option: Option, value: String): Any {
        var resultList: List<Any> = when (option.type) {
            OptionType.Text -> mutableListOf<String>()
            OptionType.Number -> mutableListOf<Double>()
            OptionType.Flag -> mutableListOf<Boolean>()
        }

        val splits = value.split(",").map { it.trim() }.toList()
        for (part in splits) {
            resultList = resultList.plus(parseOptionSingleValue(option, part))
        }

        return resultList
    }

    private fun replaceLeadingDashes(s: String): String {
        var value = s
        while (value.startsWith("-")) {
            value = value.substring(1)
        }

        return value
    }
}

class CommandParsingException(message: String) : Exception(message)
