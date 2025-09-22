package com.devsudip.ToDoListFrontend

import android.app.Application
import com.devsudip.ToDoListFrontend.data.AppDatabase
import com.devsudip.ToDoListFrontend.data.ThemeManager
import com.devsudip.ToDoListFrontend.data.TodoRepository

class TodoApplication : Application() {

    // Lazily initialize the database
    private val database by lazy { AppDatabase.getDatabase(this) }

    // Lazily initialize the ThemeManager, providing the application context
    val themeManager by lazy { ThemeManager(this) }

    // Lazily initialize the repository, providing the DAOs and the new themeManager
    val repository by lazy { TodoRepository(database.todoDao(), database.userDao()) }
}

