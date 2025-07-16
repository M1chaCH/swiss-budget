package com.michu_tech.swiss_budget.backend.framework.runtime


fun replaceLeadingDashes(s: String): String {
    var value = s
    while (value.startsWith("-")) {
        value = value.substring(1)
    }

    return value
}