package com.example.listartareas

data class Tarea(
    var descripcion: String,
    var completada: Boolean = false,
    var fechaLimite: String = "",
    var categoria: String = ""

)