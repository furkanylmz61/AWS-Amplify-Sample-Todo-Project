package com.example.todoappandroid

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.Priority
import com.amplifyframework.datastore.generated.model.Todo
import com.example.todoappandroid.databinding.MainActivityBinding
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding
    private lateinit var todoAdapter: TodoAdapter
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = MainActivityBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val recyclerView = findViewById<RecyclerView>(R.id.recylclerView)
        todoAdapter = TodoAdapter(ArrayList())
        recyclerView.adapter = todoAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        observe()
        updateRecyclerView()

    }



    fun kaydetButton(view: View){
        val name = binding.editTextText.text.toString()
        if (name == "")
        {
            Toast.makeText(applicationContext,"Lutfen bir deger giriniz!!",Toast.LENGTH_LONG).show()
        }
        else
        {
            saveTodo2(name,Priority.HIGH)
            updateRecyclerView()

        }
    }

    fun UpdateButton(view: View){

        val name = binding.editTextText.text.toString()
        val updatedName = binding.editTextText2.text.toString()
        if (name == "" || updatedName == "")
        {
            Toast.makeText(applicationContext,"Lutfen gecerli bir deger giriniz!!", Toast.LENGTH_LONG).show()
        }
        else
        {
            updateTodo(name,updatedName)
            updateRecyclerView()
        }
    }

    fun DeleteButton(view: View)
    {
        val name = binding.editTextText.text.toString()
        if (name == "")
        {
            Toast.makeText(applicationContext,"Isım bos olamaz!!",Toast.LENGTH_LONG).show()
        }
        else
        {
            deleteTodo(name)
            updateRecyclerView()
        }
    }



    fun updateRecyclerView() {
        Amplify.DataStore.query(
            Todo::class.java,
            { todos ->
                val newTodoList = mutableListOf<Todo>()
                while (todos.hasNext()) {
                    newTodoList.add(todos.next())
                }

                newTodoList.reverse()
                runOnUiThread {
                    todoAdapter.updateTodoList(newTodoList)
                }
            },
            { error ->
                Log.e("Tutorial", "Veri sorgulanırken hata oluştu", error)
            }
        )
    }





    fun saveTodo2(name : String, priority: Priority)
    {
        val date = Date()
        val offsetMillis = TimeZone.getDefault().getOffset(date.time).toLong()
        val offsetSeconds = TimeUnit.MILLISECONDS.toSeconds(offsetMillis).toInt()
        val temporalDateTime = Temporal.DateTime(date, offsetSeconds)
        val item = Todo.builder()
            .name(name)
            .priority(priority)
            .completedAt(temporalDateTime)
            .build()

        Amplify.DataStore.save(item,
            { Log.i("Tutorial", "Saved item: ${item.name}") },
            { Log.e("Tutorial", "Could not save item to DataStore", it) })
    }


    /*fun queryTodos(): MutableLiveData<String>
    {
        val result = MutableLiveData<String>()
        Amplify.DataStore.query(Todo::class.java,
            {
                todos ->
                val resultText = StringBuilder()
                while (todos.hasNext()){
                    val todo : Todo = todos.next()
                    Log.i("Tutorial", "==== Todo ====")
                    Log.i("Tutorial", "Name: ${todo.name}")
                    todo.priority?.let { todoPriority -> Log.i("Tutorial", "Priority: $todoPriority") }
                    todo.completedAt?.let { todoCompletedAt -> Log.i("Tutorial", "CompletedAt: $todoCompletedAt") }
                }
                result.postValue(resultText.toString())
            },
            { Log.e("Tutorial", "Could not query DataStore", it)  }
        )
        return result
    }

     */


    fun updateTodo(existingName : String, changeTo: String )
    {
        Amplify.DataStore.query(Todo::class.java, Where.matches(Todo.NAME.eq(existingName)),
            { matches ->
                if (matches.hasNext()) {
                    val todo = matches.next()
                    val updatedTodo = todo.copyOfBuilder()
                        .name(changeTo)
                        .build()
                    Amplify.DataStore.save(updatedTodo,
                        { Log.i("Tutorial", "Updated item: ${updatedTodo.name}") },
                        { Log.e("Tutorial", "Update failed.", it) }
                    )
                }
            },
            { Log.e("Tutorial", "Query failed", it) }
        )
    }

    fun deleteTodo(name: String)
    {
        Amplify.DataStore.query(Todo::class.java, Where.matches(Todo.NAME.eq(name)),
            { matches ->
                if (matches.hasNext()) {
                    val toDeleteTodo = matches.next()
                    Amplify.DataStore.delete(toDeleteTodo,
                        { Log.i("Tutorial", "Deleted item: ${toDeleteTodo.name}") },
                        { Log.e("Tutorial", "Delete failed.", it) }
                    )
                }
            },
            { Log.e("Tutorial", "Query failed.", it) }
        )
    }

    fun observe() {
        Amplify.DataStore.observe(
            Todo::class.java,
            { Log.i("Tutorial", "Observation began") },
            { item ->
                handler.post {
                    updateRecyclerView()
                }
            },
            { Log.e("Tutorial", "Observation failed", it) },
            { Log.i("Tutorial", "Observation complete") }
        )
    }
}