package app.vigil.core.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM HH:mm").withZone(ZoneId.systemDefault())

fun Long.formatDateTime(): String = dateTimeFormatter.format(Instant.ofEpochMilli(this))

const val DAY_MS: Long = 24L * 60 * 60 * 1000
