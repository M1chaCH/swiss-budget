package com.michu_tech.swiss_budget.backend.framework.definition

data class Directory(
    override val keyword: String,
    override val name: String,
    override val description: String
) : SelfDescribingNode {
    val childDirectories: MutableMap<String, Directory> = mutableMapOf()
    val childCommands: MutableMap<String, Command> = mutableMapOf()
    var parent: Directory? = null

    fun getChild(key: String): SelfDescribingNode? {
        val command = childCommands[key]
        if (command != null) {
            return command
        }

        return childDirectories[key]
    }
}

fun create(directory: Directory, block: Directory.() -> Unit): Directory {
//    val directory = Directory(keyword.lowercase(), name, description)
    block(directory)
    return directory
}

fun Directory.add(directory: Directory, block: Directory.() -> Unit) {
    block(directory)
    directory.parent = this
    this.childDirectories.put(directory.keyword, directory)
}

fun Directory.add(command: Command, block: Command.() -> Unit) {
    block(command)
    command.parent = this
    this.childCommands.put(command.keyword, command)
}
