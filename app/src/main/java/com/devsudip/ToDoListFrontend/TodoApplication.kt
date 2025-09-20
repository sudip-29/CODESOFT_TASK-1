package com.devsudip.ToDoListFrontend

import android.app.Application
import com.devsudip.ToDoListFrontend.data.AppDatabase
import com.devsudip.ToDoListFrontend.data.TodoRepository

class TodoApplication : Application() {
    // Using by lazy so the database and repository are only created when they're needed
    // for the first time.
    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { TodoRepository(database.todoDao(), database.userDao()) }
}

