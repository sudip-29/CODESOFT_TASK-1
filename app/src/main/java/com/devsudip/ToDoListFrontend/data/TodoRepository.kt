package com.devsudip.ToDoListFrontend.data

import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class TodoRepository(private val todoDao: TodoDao, private val userDao: UserDao) {

    val allTodos: Flow<List<TodoItem>> = todoDao.getAllTodos()
    val user: Flow<User?> = userDao.getUser()

    suspend fun insertTodo(item: TodoItem) {
        todoDao.insertTodo(item)
    }

    suspend fun updateTodo(item: TodoItem) {
        todoDao.updateTodo(item)
    }

    suspend fun deleteTodo(item: TodoItem) {
        todoDao.deleteTodo(item)
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    // --- NEW FUNCTION ADDED ---
    suspend fun deleteCompletedTasksFromYesterday() {
        val calendar = Calendar.getInstance()
        // Set calendar to the beginning of today
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        // This timestamp represents the very first millisecond of today.
        // Any completed task with a due date before this timestamp is from yesterday or earlier.
        val yesterdayTimestamp = calendar.timeInMillis
        todoDao.deleteCompletedTasksBefore(yesterdayTimestamp)
    }
}

