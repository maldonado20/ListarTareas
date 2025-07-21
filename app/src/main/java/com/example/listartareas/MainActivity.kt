package com.example.listartareas

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var listaTareasView: ListView

    private lateinit var botonAgregarTarea: Button

    private lateinit var entradaDescripcion: EditText

    private var listaTareas = mutableListOf<Tarea>()

    private lateinit var adaptadorTareas: AdaptadorTareas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listaTareasView = findViewById(R.id.listaTareas)
        botonAgregarTarea = findViewById(R.id.botonAgregar)
        entradaDescripcion = findViewById(R.id.entradaDescripcion)

        cargarLista()

        adaptadorTareas = AdaptadorTareas(this, listaTareas) {
            guardarLista()
        }
        listaTareasView.adapter = adaptadorTareas

        botonAgregarTarea.setOnClickListener {
            val texto = entradaDescripcion.text.toString().trim()
            if (texto.isBlank()) {
                Toast.makeText(this, "Escribe una tarea antes de agregar", Toast.LENGTH_SHORT).show()
            } else {
                mostrarDialogoAgregar(texto)
                entradaDescripcion.text.clear()
            }
        }

        listaTareasView.setOnItemClickListener { _, _, posicion, _ ->
            mostrarDialogoEditar(listaTareas[posicion], posicion)
        }
    }

    private fun guardarLista() {
        val prefs = getSharedPreferences("tareas_app", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(listaTareas)
        editor.putString("lista_tareas", json)
        editor.apply()
    }

    private fun cargarLista() {
        val prefs = getSharedPreferences("tareas_app", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("lista_tareas", null)
        val tipo = object : TypeToken<MutableList<Tarea>>() {}.type
        listaTareas = gson.fromJson(json, tipo) ?: mutableListOf()
    }

    private fun ordenarLista() {
        listaTareas.sortWith(compareBy({ it.categoria }, { it.descripcion }))
    }

    fun mostrarDialogoAgregar(descripcionInicial: String) {
        val vistaDialogo = layoutInflater.inflate(R.layout.dialog_task, null)
        val entradaDesc = vistaDialogo.findViewById<EditText>(R.id.editDescripcion)
        val entradaFecha = vistaDialogo.findViewById<EditText>(R.id.editFecha)
        val spinnerCategoria = vistaDialogo.findViewById<Spinner>(R.id.spinnerCategoria)

        entradaDesc.setText(descripcionInicial)

        ArrayAdapter.createFromResource(
            this,
            R.array.array_categorias,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategoria.adapter = adapter
        }

        AlertDialog.Builder(this)
            .setTitle("Nueva Tarea")
            .setView(vistaDialogo)
            .setPositiveButton("Agregar") { _, _ ->
                val descripcion = entradaDesc.text.toString().trim()
                val categoria = spinnerCategoria.selectedItem.toString()
                val fecha = entradaFecha.text.toString().trim()

                val nuevaTarea = Tarea(descripcion, false, fecha, categoria)
                listaTareas.add(nuevaTarea)
                ordenarLista()
                adaptadorTareas.notifyDataSetChanged()
                guardarLista()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    fun mostrarDialogoEditar(tarea: Tarea, posicion: Int) {
        val vistaDialogo = layoutInflater.inflate(R.layout.dialog_task, null)
        val entradaDesc = vistaDialogo.findViewById<EditText>(R.id.editDescripcion)
        val entradaFecha = vistaDialogo.findViewById<EditText>(R.id.editFecha)
        val spinnerCategoria = vistaDialogo.findViewById<Spinner>(R.id.spinnerCategoria)

        entradaDesc.setText(tarea.descripcion)
        entradaFecha.setText(tarea.fechaLimite)

        ArrayAdapter.createFromResource(
            this,
            R.array.array_categorias,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategoria.adapter = adapter
            if (tarea.categoria.isNotEmpty()) {
                val pos = adapter.getPosition(tarea.categoria)
                spinnerCategoria.setSelection(pos)
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Editar Tarea")
            .setView(vistaDialogo)
            .setPositiveButton("Guardar") { _, _ ->
                tarea.descripcion = entradaDesc.text.toString().trim()
                tarea.fechaLimite = entradaFecha.text.toString().trim()
                tarea.categoria = spinnerCategoria.selectedItem.toString()
                ordenarLista()
                adaptadorTareas.notifyDataSetChanged()
                guardarLista()
            }
            .setNegativeButton("Cancelar", null)
            .setNeutralButton("Eliminar") { _, _ ->
                listaTareas.removeAt(posicion)
                adaptadorTareas.notifyDataSetChanged()
                guardarLista()
            }
            .show()
    }
}
