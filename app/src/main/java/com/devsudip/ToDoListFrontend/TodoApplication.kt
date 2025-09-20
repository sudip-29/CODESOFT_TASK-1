package com.devsudip.ToDoListFrontend

import android.app.Application
import com.devsudip.ToDoListFrontend.data.AppDatabase
import com.devsudip.ToDoListFrontend.data.TodoRepository

class TodoApplication : Application() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { TodoRepository(database.todoDao(), database.userDao()) }
}

