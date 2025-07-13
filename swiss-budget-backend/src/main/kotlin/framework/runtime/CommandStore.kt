package com.michu_tech.swiss_budget.backend.framework.runtime

import com.michu_tech.swiss_budget.backend.framework.definition.Command
import com.michu_tech.swiss_budget.backend.framework.definition.Directory

class CommandStore {
    private var root: Directory? = null

    fun initialize(directory: Directory) {
        root = directory
    }

    fun getRoot(): Directory {
        checkNotNull(root) {
            "CommandStore is accessed before it was initialized"
        }
        return root!!
    }

    fun getCommand(tokens: List<String>): Command? {
        checkNotNull(root) {
            "CommandStore is accessed before it was initialized"
        }

        if (tokens.isEmpty()) {
            return null
        }

        var dir = root!!
        for (token in tokens) {
            val node = dir.getChild(token)
            when (node) {
                is Command -> return node
                is Directory -> dir = node
                null -> throw CommandParsingException("$token is not part of the dictionary ${dir.name}")
                else -> throw IllegalArgumentException("Unexpected token (type miss match): $token")
            }
        }

        return null
    }
}