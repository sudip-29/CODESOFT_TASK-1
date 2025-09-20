package com.devsudip.ToDoListFrontend.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.devsudip.ToDoListFrontend.data.TodoItem
import com.devsudip.ToDoListFrontend.data.TodoRepository
import com.devsudip.ToDoListFrontend.data.User
import kotlinx.coroutines.launch
import java.util.Calendar

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    val allTodos: LiveData<List<TodoItem>> = repository.allTodos.asLiveData()
    val user: LiveData<User?> = repository.user.asLiveData()

    init {
        cleanupOldTasks()
    }

    fun addTodo(item: TodoItem) = viewModelScope.launch {
        repository.insertTodo(item)
    }

    fun updateTodo(item: TodoItem) = viewModelScope.launch {
        val isOverdue = isTaskOverdue(item.dueDate)

        if (isOverdue && item.isCompleted) {
            repository.deleteTodo(item)
        } else {
            repository.updateTodo(item)
        }
    }

    fun deleteTodo(item: TodoItem) = viewModelScope.launch {
        repository.deleteTodo(item)
    }

    fun saveUserName(name: String) = viewModelScope.launch {
        repository.insertUser(User(id = 1, name = name))
    }

    private fun cleanupOldTasks() = viewModelScope.launch {
        repository.deleteCompletedTasksFromYesterday()
    }

    private fun isTaskOverdue(dueDateMillis: Long): Boolean {
        val dueDate = Calendar.getInstance().apply {
            timeInMillis = dueDateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return dueDate.before(today)
    }
}

class TodoViewModelFactory(private val repository: TodoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

