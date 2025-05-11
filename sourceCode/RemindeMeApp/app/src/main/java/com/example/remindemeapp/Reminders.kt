package com.example.remindemeapp

import android.util.Log

class Reminders()
{
    fun getReminders(): MutableList<String>
    {
        var reminders = mutableListOf(
            "Matma Sprawdzian, 28/02/2023",
            "Polski Sprawdzian, 2/03/2023",
            "Informatyka Kartkówka, 05/03/2023"
        )

        return reminders
    }

    fun addReminder(reminders: MutableList<String>, text: String): MutableList<String>
    {
        reminders.add(text)

        for(reminder in reminders)
        {
            Log.d("newReminders", reminder)
        }

        return reminders
    }
}
