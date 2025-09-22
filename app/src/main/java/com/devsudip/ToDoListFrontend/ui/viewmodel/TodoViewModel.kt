package com.devsudip.ToDoListFrontend.ui.viewmodel

import androidx.lifecycle.*
import com.devsudip.ToDoListFrontend.data.*
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class TodoViewModel(private val repository: TodoRepository, private val themeManager: ThemeManager) : ViewModel() {

    val allTodos: LiveData<List<TodoItem>> = repository.allTodos.asLiveData()
    val user: LiveData<User?> = repository.user.asLiveData()

    // Expose the theme flow from the manager
    val isDarkMode = themeManager.themeFlow.asLiveData()

    init {
        // Run cleanup tasks when the ViewModel is created
        cleanupOldTasks()
    }

    fun addTodo(item: TodoItem) = viewModelScope.launch {
        repository.insertTodo(item)
    }

    fun updateTodo(item: TodoItem) = viewModelScope.launch {
        // If an overdue task is completed, delete it. Otherwise, just update it.
        if (isTaskOverdue(item.dueDate) && item.isCompleted) {
            repository.deleteTodo(item)
        } else {
            repository.updateTodo(item)
        }
    }

    fun deleteTodo(item: TodoItem) = viewModelScope.launch {
        repository.deleteTodo(item)
    }

    fun saveUserName(name: String) = viewModelScope.launch {
        repository.insertUser(User(name = name))
    }

    // Function to set the theme, which calls the manager
    fun setTheme(isDarkMode: Boolean) = viewModelScope.launch {
        themeManager.setTheme(isDarkMode)
    }

    private fun cleanupOldTasks() = viewModelScope.launch {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = calendar.timeInMillis
        repository.deleteCompletedTasksFromYesterday(yesterday)
    }

    private fun isTaskOverdue(dueDateMillis: Long): Boolean {
        val dueDate = Calendar.getInstance().apply {
            timeInMillis = dueDateMillis
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        return dueDate.before(today)
    }


    class TodoViewModelFactory(private val repository: TodoRepository, private val themeManager: ThemeManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TodoViewModel(repository, themeManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

