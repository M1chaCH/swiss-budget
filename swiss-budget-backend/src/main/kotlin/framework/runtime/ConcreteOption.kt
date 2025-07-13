package com.michu_tech.swiss_budget.backend.framework.runtime

typealias ConcreteOptions = Map<String, Any>

inline fun <reified T> ConcreteOptions.extractOptional(key: String): T? {
    val value = this[key] ?: return null

    return value as? T
        ?: throw ClassCastException("Value for key '$key' is of type ${value::class.simpleName}, but type ${T::class.simpleName} was expected.")
}

inline fun <reified T> ConcreteOptions.extract(key: String): T {
    return this.extractOptional<T>(key)
        ?: throw IllegalArgumentException("Key '$key' not found in options.")
}