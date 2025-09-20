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

    suspend fun deleteCompletedTasksFromYesterday() {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val yesterdayTimestamp = calendar.timeInMillis
        todoDao.deleteCompletedTasksBefore(yesterdayTimestamp)
    }
}

