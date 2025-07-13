package com.michu_tech.swiss_budget.backend.app.tag

import com.michu_tech.swiss_budget.backend.framework.definition.*

fun configureTagDirectory(): Directory {
    return create(Directory("tag", "Tag", "From here you can Manage your Tags.")) {
        add(Command("create", "Create a Tag", "Creates a new tag.")) {
            add(
                Option(
                    "n",
                    "name",
                    "Tag name",
                    "The name of the tag to create.",
                    "What will the tag be called?",
                )
            )
            add(
                Option(
                    "k",
                    "keywords",
                    "Keywords",
                    "Keywords for the tag to be created.",
                    "What keywords should this tag have?",
                    isList = true
                )
            )
            add(
                Option(
                    "a",
                    "apply",
                    "Apply Keywords",
                    "After the tag is created, the tags of all transactions will be updated",
                    "Do you want to apply the tag?",
                    required = false,
                    defaultValue = true,
                    type = OptionType.Flag
                )
            )
            action(::createTag)
        }
    }
}