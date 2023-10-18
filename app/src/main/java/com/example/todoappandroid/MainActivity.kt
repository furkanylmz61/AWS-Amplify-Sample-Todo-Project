package com.example.todoappandroid

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.Priority
import com.amplifyframework.datastore.generated.model.Todo
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        //saveTodo1()
        //saveTodo2()
        queryTodos()
        //queryTodos2()
        //uptadeTodo()
        //deleteTodo()
        //observe()
        //saveTodo3()
    }

    fun saveTodo1()
    {
        val item = Todo.builder()
            .name("Build Android Application")
            .priority(Priority.NORMAL)
            .build()

        Amplify.DataStore.save(item,
            { Log.i("Tutorial", "Saved item: ${item.name}") },
            { Log.e("Tutorial", "Could not save item to DataStore", it) })
    }
    fun saveTodo2()
    {
        val date = Date()
        val offsetMillis = TimeZone.getDefault().getOffset(date.time).toLong()
        val offsetSeconds = TimeUnit.MILLISECONDS.toSeconds(offsetMillis).toInt()
        val temporalDateTime = Temporal.DateTime(date, offsetSeconds)
        val item = Todo.builder()
            .name("Finish Quarterly Taxes")
            .priority(Priority.HIGH)
            .completedAt(temporalDateTime)
            .build()

        Amplify.DataStore.save(item,
            { Log.i("Tutorial", "Saved item: ${item.name}") },
            { Log.e("Tutorial", "Could not save item to DataStore", it) })
    }

    fun saveTodo3()
    {
        val item: Todo = Todo.builder()
            .name("Lorem ipsum dolor sit amet")
            .priority(Priority.LOW)
            .completedAt(Temporal.DateTime("1970-01-01T12:30:23.999Z"))
            .build()
        Amplify.DataStore.save(
            item,
            { success -> Log.i("Amplify", "Saved item: " + success.item().name) },
            { error -> Log.e("Amplify", "Could not save item to DataStore", error) }
        )
    }

    fun queryTodos()
    {
        Amplify.DataStore.query(Todo::class.java,
            {
                todos ->
                while (todos.hasNext()){
                    val todo : Todo = todos.next()
                    Log.i("Tutorial", "==== Todo ====")
                    Log.i("Tutorial", "Name: ${todo.name}")
                    todo.priority?.let { todoPriority -> Log.i("Tutorial", "Priority: $todoPriority") }
                    todo.completedAt?.let { todoCompletedAt -> Log.i("Tutorial", "CompletedAt: $todoCompletedAt") }
                }

            },
            { Log.e("Tutorial", "Could not query DataStore", it)  }
        )
    }

    fun queryTodos2()
    {
        Amplify.DataStore.query(
            Todo::class.java, Where.matches(Todo.PRIORITY.eq(Priority.HIGH)),
            { todos ->
                while (todos.hasNext()) {
                    val todo: Todo = todos.next()
                    Log.i("Tutorial", "==== Todo ====")
                    Log.i("Tutorial", "Name: ${todo.name}")
                    todo.priority?.let { todoPriority -> Log.i("Tutorial", "Priority: $todoPriority") }
                    todo.completedAt?.let { todoCompletedAt -> Log.i("Tutorial", "CompletedAt: $todoCompletedAt") }
                }
            },
            { failure -> Log.e("Tutorial", "Could not query DataStore", failure) }
        )
    }

    fun uptadeTodo()
    {
        Amplify.DataStore.query(Todo::class.java, Where.matches(Todo.NAME.eq("Finish quarterly taxes")),
            { matches ->
                if (matches.hasNext()) {
                    val todo = matches.next()
                    val updatedTodo = todo.copyOfBuilder()
                        .name("File quarterly taxes")
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

    fun deleteTodo()
    {
        Amplify.DataStore.query(Todo::class.java, Where.matches(Todo.NAME.eq("Finish quarterly taxes")),
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

    fun observe()
    {
        Amplify.DataStore.observe(Todo::class.java,
            { Log.i("Tutorial", "Observation began") },
            {
                val todo = it.item()
                Log.i("Tutorial", "Todo: $todo")
            },
            { Log.e("Tutorial", "Observation failed", it) },
            { Log.i("Tutorial", "Observation complete") }
        )
    }
}