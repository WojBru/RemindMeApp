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
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.lifecycleScope
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    val CHANNEL_ID = "reminderID"
    val CHANNEL_NAME = "reminders"
    val NOTIFICATION_ID = 0

    private lateinit var dataStore: DataStore<Preferences>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()

        dataStore = createDataStore(name = "settings")

        btnDeleteReminder.setOnClickListener {
            lifecycleScope.launch {
                showDeleteRemindersOptions()
            }
        }

        btnAddReminder.setOnClickListener {
            var secondActivity = Intent(this, CreateReminderActivity::class.java)
            lifecycleScope.launch {
                val reminders = getReminders()
                secondActivity.putExtra("reminders", reminders)
            }


            startActivity(secondActivity)
            finish()
        }

        lifecycleScope.launch {
            createReminders(dataStore)
        }
    }

    fun createNotification(notificationText: String) {
        //Notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Reminder")
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH).apply {
                lightColor = Color.GREEN
                enableLights(true)
                enableVibration(true)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    suspend fun checkMessageToKey(message: String): String
    {
        var reminders = mutableListOf<String>()
        for (i in 1..10) {
            try {
                val dataStoreKey = preferencesKey<String>(i.toString())
                val preferences = dataStore.data.first()
                val reminder = preferences[dataStoreKey]

                if (reminder != null && reminder == message) {
                    reminders.add(reminder.toString())
                    return i.toString()
                }
            } catch (e: ArrayIndexOutOfBoundsException) {
                Log.d("reminders", "nie ma takiego indeksu")
            }
        }
        return ""
    }

    fun deleteReminders(toDelete: MutableList<String>) {

        for(name in toDelete)
        {
            lifecycleScope.launch {
                var key = checkMessageToKey(name)
                Log.d("usuwanie_name", name)
                Log.d("usuwanie_key", key)

                deleteNamePreferences(key)
            }
        }
    }

    suspend fun showDeleteRemindersOptions() {

            val options = getReminders()
            val toDelete = mutableListOf<String>()

            val multiChoiceDialog = AlertDialog.Builder(this@MainActivity)
                .setTitle("Delete reminder")
                .setMultiChoiceItems(
                    options,
                    booleanArrayOf(false, false, false, false, false, false, false, false, false, false)
                ) { p, i, isChecked ->
                    if (isChecked)
                    {
                        Log.d("ciekawosc dodano",options[i])

                        toDelete.add(options[i])
                    }

                    if (!isChecked)
                    {
                        Log.d("ciekawosc usunieta",options[i])

                        toDelete.remove(options[i])
                    }
                }
                .setPositiveButton("Delete") { _, _ ->
                    for(i in toDelete)
                    {
                        Log.d("ciekawosc koncowa",i.toString())
                    }

                    deleteReminders(toDelete)
                    val mainActivity = Intent(this@MainActivity, MainActivity::class.java)
                    startActivity(mainActivity)
                }
                .setNegativeButton("Cancel") { _, _ ->
                    //Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
                }.create().show()

    }

    private suspend fun save(key: String, value: String) {
        val dataStoreKey = preferencesKey<String>(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    private suspend fun read(key: String): String? {
        val dataStoreKey = preferencesKey<String>(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    suspend fun deleteAllPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun deleteNamePreferences(name: String) {
        val dataStoreKey = preferencesKey<String>(name)
        dataStore.edit { preferences ->
            preferences.remove(dataStoreKey)
        }
    }

    var intentt2 = ""
    override fun onResume() {
        super.onResume()

        Log.d("resume", "resuming")

        //NEW REMINDER
        //var intentt2 = ""
        var intentt1 = intent.getStringExtra("newReminder")

        Log.d("resume", "NewReminder: " + intentt1.toString())
        if(intentt1 != "" && intentt1.toString() != intentt2)
        {
            intentt2 = intentt1.toString()

            //liczenie ile jest reminders
            lifecycleScope.launch {
                var counter = countReminders()
                if (intentt1 != null) {
                    save(counter, intentt1.toString())
                    createReminders(dataStore)
                }
            }
        }
    }

    suspend fun getReminders(): Array<String> {
        var reminders = mutableListOf<String>()
        var counter = 1
        for (i in 1..10) {
            try {
                val dataStoreKey = preferencesKey<String>(i.toString())
                val preferences = dataStore.data.first()
                val reminder = preferences[dataStoreKey]
                Log.d("getReminders", reminder.toString())
                if (reminder != null) {
                    reminders.add(reminder.toString())
                }
            } catch (e: ArrayIndexOutOfBoundsException) {
                Log.d("reminders", "nie ma takiego indeksu")
            }
        }

        return reminders.toTypedArray()
    }

    suspend fun countReminders(): String {
        var reminders = mutableListOf<String>()
        var counter = 1
        for(i in 1..10)
        {
            try {
                val dataStoreKey = preferencesKey<String>(i.toString())
                val preferences = dataStore.data.first()
                val reminder = preferences[dataStoreKey]
                if(reminder != null)
                {
                    counter++
                }
            }
            catch (e: ArrayIndexOutOfBoundsException)
            {
                Log.d("reminders", "nie ma takiego indeksu")
            }

        }

        return counter.toString()
    }

    suspend fun createReminders(dataStore: DataStore<Preferences>) {
        var reminders = mutableListOf<String>()
        for(i in 1..10)
        {
            try {
                val dataStoreKey = preferencesKey<String>(i.toString())
                val preferences = dataStore.data.first()
                val reminder = preferences[dataStoreKey]
                if(reminder != null)
                {
                    reminders.add(reminder.toString())
                }
            }
            catch (e: ArrayIndexOutOfBoundsException)
            {
                Log.d("reminders", "nie ma takiego indeksu")
            }
        }

        for(reminder in reminders) {
            Log.d("reminders", reminder)
        }
        Log.d("reminders", "koniec funkcji")

        resumeReminders(reminders)
    }

    fun resumeReminders(reminders: MutableList<String>)
    {
        llReminders.removeAllViews()

        //Dodawanie przypomnien
        for(reminder in reminders.orEmpty())
        {
            var title = (reminder.split(", ").toTypedArray())[0]
            var date = (reminder.split(", ").toTypedArray())[1]

            //Tworznenie textView
            val tvReminder = TextView(this)
            tvReminder.textSize = 30f
            tvReminder.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.purple_200))
            tvReminder.text = title
            tvReminder.id = R.id.reminder1

            tvReminder.setOnClickListener {
                calendarView.setDate(SimpleDateFormat("dd/MM/yyyy").parse(date).time, true, true)
            }
            llReminders.addView(tvReminder)
        }
    }
}