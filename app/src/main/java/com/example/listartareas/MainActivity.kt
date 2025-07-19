package com.example.listartareas

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listartareas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val tasks = mutableListOf<Task>()
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cargar tareas guardadas
        tasks.addAll(TaskRepository.loadTasks(this))

        // Inicializar adaptador
        adapter = TaskAdapter(
            tasks,
            onTaskChecked = { position, isChecked ->
                tasks[position].completed = isChecked
                TaskRepository.saveTasks(this, tasks)
            },
            onEditClick = { position -> showEditDialog(position) },
            onDeleteClick = { position ->
                tasks.removeAt(position)
                adapter.notifyItemRemoved(position)
                TaskRepository.saveTasks(this, tasks)
            }
        )

        // Configurar RecyclerView
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = adapter

        // Botón para agregar nueva tarea
        binding.btnAddTask.setOnClickListener {
            showAddDialog()
        }
    }

    private fun showAddDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_task, null)
        val etTaskName = dialogView.findViewById<EditText>(R.id.etTaskName)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val etDueDate = dialogView.findViewById<EditText>(R.id.etDueDate)

        val categories = listOf("Trabajo", "Personal", "Otro")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapterSpinner

        AlertDialog.Builder(this)
            .setTitle("Agregar tarea")
            .setView(dialogView)
            .setPositiveButton("Agregar") { dialog, _ ->
                val name = etTaskName.text.toString().trim()
                val category = spinnerCategory.selectedItem.toString()
                val dueDate = etDueDate.text.toString().trim()

                if (name.isNotEmpty()) {
                    tasks.add(Task(name, false, category, if (dueDate.isNotEmpty()) dueDate else null))
                    adapter.notifyItemInserted(tasks.size - 1)
                    TaskRepository.saveTasks(this, tasks)
                } else {
                    Toast.makeText(this, "La tarea no puede estar vacía", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showEditDialog(position: Int) {
        val task = tasks[position]
        val dialogView = layoutInflater.inflate(R.layout.dialog_task, null)
        val etTaskName = dialogView.findViewById<EditText>(R.id.etTaskName)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val etDueDate = dialogView.findViewById<EditText>(R.id.etDueDate)

        etTaskName.setText(task.name)
        etDueDate.setText(task.dueDate ?: "")

        val categories = listOf("Trabajo", "Personal", "Otro")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapterSpinner

        val categoryIndex = categories.indexOf(task.category ?: "Otro")
        spinnerCategory.setSelection(if (categoryIndex >= 0) categoryIndex else 2)

        AlertDialog.Builder(this)
            .setTitle("Editar tarea")
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val newName = etTaskName.text.toString().trim()
                val newCategory = spinnerCategory.selectedItem.toString()
                val newDueDate = etDueDate.text.toString().trim()

                if (newName.isNotEmpty()) {
                    task.name = newName
                    task.category = newCategory
                    task.dueDate = if (newDueDate.isNotEmpty()) newDueDate else null
                    adapter.notifyItemChanged(position)
                    TaskRepository.saveTasks(this, tasks)
                } else {
                    Toast.makeText(this, "La tarea no puede estar vacía", Toast.LENGTH_SHORT).show()
                }

                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}

