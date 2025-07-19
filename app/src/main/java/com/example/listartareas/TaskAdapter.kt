package com.example.listartareas

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.listartareas.databinding.ItemTaskBinding

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskChecked: (Int, Boolean) -> Unit,
    private val onEditClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.cbTask.setOnCheckedChangeListener { _, isChecked ->
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onTaskChecked(pos, isChecked)
                    updateTaskStyle(binding.tvTaskName, isChecked)
                }
            }

            binding.btnEdit.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onEditClick(pos)
                }
            }

            binding.btnDelete.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onDeleteClick(pos)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.binding.tvTaskName.text = task.name
        holder.binding.cbTask.isChecked = task.completed
        holder.binding.tvCategory.text = "Categoría: ${task.category ?: "Otro"}"
        holder.binding.tvDueDate.text = "Fecha de vencimiento: ${task.dueDate ?: "-"}"
        updateTaskStyle(holder.binding.tvTaskName, task.completed)
    }

    override fun getItemCount(): Int = tasks.size

    private fun updateTaskStyle(textView: TextView, completed: Boolean) {
        if (completed) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            textView.setTextColor(textView.resources.getColor(android.R.color.darker_gray))
        } else {
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textView.setTextColor(textView.resources.getColor(android.R.color.black))
        }
    }
}
