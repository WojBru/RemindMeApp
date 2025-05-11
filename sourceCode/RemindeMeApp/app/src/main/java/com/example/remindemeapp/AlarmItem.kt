package com.example.remindemeapp

import java.time.LocalDateTime
import java.util.Date

data class AlarmItem(
    val time: LocalDateTime,
    val message: String
)
