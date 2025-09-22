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

    // This function now accepts the timestamp and passes it to the DAO
    suspend fun deleteCompletedTasksFromYesterday(yesterdayTimestamp: Long) {
        todoDao.deleteCompletedTasksFromYesterday(yesterdayTimestamp)
    }
}

