package com.example.listartareas

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class AdaptadorTareas(
    private val contexto: Context,
    private val tareas: MutableList<Tarea>,
    private val onCambio: () -> Unit
) : ArrayAdapter<Tarea>(contexto, 0, tareas) {

    override fun getView(posicion: Int, vistaReciclada: View?, padre: ViewGroup): View {
        val vista = vistaReciclada ?: LayoutInflater.from(contexto)
            .inflate(R.layout.elemento_tarea, padre, false)

        val tarea = tareas[posicion]

        val chkCompletada = vista.findViewById<CheckBox>(R.id.chkCompletada)
        val txtDescripcion = vista.findViewById<TextView>(R.id.txtDescripcion)
        val txtDetalles = vista.findViewById<TextView>(R.id.txtDetalles)
        val btnEditar = vista.findViewById<ImageButton>(R.id.btnEditar)
        val btnEliminar = vista.findViewById<ImageButton>(R.id.btnEliminar)

        // Evitar disparar listener al hacer setChecked
        chkCompletada.setOnCheckedChangeListener(null)
        chkCompletada.isChecked = tarea.completada

        txtDescripcion.text = tarea.descripcion

        // Mostrar fecha de vencimiento y categoría juntos
        txtDetalles.text = "vencimiento: ${tarea.fechaLimite} " + "| Categoría: ${tarea.categoria}"


        txtDescripcion.paintFlags = if (tarea.completada)
            txtDescripcion.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else
            txtDescripcion.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

        chkCompletada.setOnCheckedChangeListener { _, isChecked ->
            tarea.completada = isChecked
            notifyDataSetChanged()
            onCambio()
        }

        btnEliminar.setOnClickListener {
            tareas.removeAt(posicion)
            notifyDataSetChanged()
            onCambio()
        }

        btnEditar.setOnClickListener {
            (contexto as MainActivity).mostrarDialogoEditar(tarea, posicion)
        }

        return vista
    }
}
