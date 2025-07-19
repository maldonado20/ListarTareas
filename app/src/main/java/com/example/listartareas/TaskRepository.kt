package com.example.listartareas

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TaskRepository {
    private const val PREF_NAME = "com.example.listartareas.tasks"
    private const val KEY_TASKS = "tasks"

    fun saveTasks(context: Context, tasks: List<Task>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(tasks)
        editor.putString(KEY_TASKS, json)
        editor.apply()
    }

    fun loadTasks(context: Context): MutableList<Task> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_TASKS, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            Gson().fromJson(json, type)
        } else {
            mutableListOf()
        }
    }
}
