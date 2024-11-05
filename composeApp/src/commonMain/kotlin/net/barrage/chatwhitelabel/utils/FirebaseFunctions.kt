package net.barrage.chatwhitelabel.utils

inline fun <reified T> Map<String, Any>.getValueOrDefault(key: String, defaultValue: T): T {
    return this[key] as? T ?: defaultValue
}

fun Map<String, Any>.getListOrDefault(key: String): List<String> {
    return (this[key] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
}
