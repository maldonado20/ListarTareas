package com.example.listartareas

data class Task(
    var name: String,
    var completed: Boolean = false,
    var category: String? = "Otro",
    var dueDate: String? = null
)