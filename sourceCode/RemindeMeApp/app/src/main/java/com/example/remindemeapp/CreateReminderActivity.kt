package com.example.remindemeapp

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat.getLongDateFormat
import android.util.Log
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.remindemeapp.databinding.ActivityMainBinding
import com.example.remindemeapp.databinding.CreateReminderLayoutBinding
import com.example.remindemeapp.databinding.CreateReminderLayoutBinding.*
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.create_reminder_layout.*
import kotlinx.android.synthetic.main.create_reminder_layout.view.*
import java.sql.Time
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.util.*
import kotlin.collections.ArrayList

class CreateReminderActivity : AppCompatActivity() {

    private lateinit var binding: CreateReminderLayoutBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreateReminderLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val scheduler = AndroidAlarmScheduler(this)
        var alarmItem: AlarmItem? = null

        btnBack.setOnClickListener {
            var mainActivity = Intent(this, MainActivity::class.java)
            startActivity(mainActivity)
        }

        btnSchedule.setOnClickListener {
            var isNewReminderGood = false
            var reminders = intent.getStringArrayExtra("reminders")
            var reminderToCheck = etMessage.text.toString()


            if(!reminders.isNullOrEmpty())
            {
                Log.d("Error user", "1: " + reminders!![0])
                Log.d("Error user", "2: " + reminderToCheck)

                for(i in reminders!!)
                {
                    var reminder = (i.split(", ").toTypedArray())[0]
                    if(reminder == reminderToCheck)
                    {
                        Log.d("Error user", "Reminders nie moga byc takie same")
                        val toast = Toast.makeText(this, "Reminder's title cannot be the same", Toast.LENGTH_SHORT)
                        toast.show()
                        isNewReminderGood = false
                        break
                    }
                    else
                    {
                        isNewReminderGood = true
                    }
                }
            }
            else {
                isNewReminderGood = true
            }

            if(isNewReminderGood)
            {
                alarmItem = AlarmItem(
                    time = getTime(etTime, etTime2),
                    message = etMessage.text.toString()
                )
                alarmItem?.let(scheduler::schedule)

                //dodawanie do listy reminders
                var newReminder = getNewReminder(etMessage.text.toString(), etTime)

                etMessage.setText("")
                var mainActivity = Intent(this, MainActivity::class.java)
                mainActivity.putExtra("newReminder", newReminder)
                startActivity(mainActivity)
            }
        }
    }

    fun getNewReminder(message: String, time: DatePicker): String
    {
        var year = time.year
        var month = time.month + 1
        var day = time.dayOfMonth
        var newReminder = message + ", " + day + "/" + month + "/" + year

        return newReminder
    }

    fun getTime(time: DatePicker, time2: TimePicker):LocalDateTime
    {
        var pickedYear = time.year
        var pickedMonth = time.month + 1
        var pickedDay = time.dayOfMonth
        var pickedHour = time2.hour
        var pickedMinute = time2.minute

        Log.d("picked", pickedYear.toString())
        Log.d("picked", pickedMonth.toString())
        Log.d("picked", pickedDay.toString())
        Log.d("picked", pickedHour.toString())
        Log.d("picked", pickedMinute.toString())

        var plusYears = 0
        var plusMonths = 0
        var plusDays = 0
        var plusHours = 0
        var plusMinutes = 0

        var localDateTime = LocalDateTime.now()


        if(!(localDateTime.year == pickedYear))
        {
            plusYears = pickedYear - localDateTime.year
        }

        if(!(localDateTime.monthValue == pickedMonth))
        {
            plusMonths = pickedMonth - localDateTime.monthValue
        }

        if(!(localDateTime.dayOfMonth == pickedDay))
        {
            plusDays = pickedDay - localDateTime.dayOfMonth
        }

        if(!(localDateTime.hour == pickedHour))
        {
            plusHours = pickedHour - localDateTime.hour - 4
        }

        if(!(localDateTime.minute == pickedMinute))
        {
            plusMinutes = pickedMinute - localDateTime.minute
        }

        Log.d("plusTime", plusYears.toString())
        Log.d("plusTime", plusMonths.toString())
        Log.d("plusTime", plusDays.toString())
        Log.d("plusTime", plusHours.toString())
        Log.d("plusTime", plusMinutes.toString())


        return LocalDateTime.now()
            .plusYears(plusYears.toLong())
            .plusMonths(plusMonths.toLong())
            .plusDays(plusDays.toLong())
            .plusHours(plusHours.toLong())
            .plusMinutes(plusMinutes.toLong())
            //.plusSeconds(0)
    }
}