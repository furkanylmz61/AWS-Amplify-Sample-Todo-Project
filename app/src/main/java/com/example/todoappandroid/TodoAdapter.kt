package com.example.todoappandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.Todo

class TodoAdapter(val todoList: ArrayList<Todo>) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.todoNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo = todoList[position]
        holder.nameTextView.text = todo.name
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

        fun addTodo(todo: Todo) {
            todoList.add(todo)
            notifyDataSetChanged()
        }

        fun removeTodoAt(position: Int) {
            if (position >= 0 && position < todoList.size) {
                todoList.removeAt(position)
                notifyDataSetChanged()
            }
        }

        fun addTodoFirst(todo: Todo) {
            todoList.add(0, todo)
            notifyDataSetChanged()
        }

    fun updateTodoList(newList: List<Todo>) {
        val diffCallback = TodoDiffCallback(todoList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        todoList.clear()
        todoList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    }