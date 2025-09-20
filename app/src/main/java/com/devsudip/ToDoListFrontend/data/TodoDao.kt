package com.devsudip.ToDoListFrontend.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo_items ORDER BY dueDate ASC")
    fun getAllTodos(): Flow<List<TodoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(item: TodoItem)

    @Delete
    suspend fun deleteTodo(item: TodoItem)

    @Update
    suspend fun updateTodo(item: TodoItem)

    // --- NEW FUNCTION ADDED ---
    @Query("DELETE FROM todo_items WHERE isCompleted = 1 AND dueDate < :yesterdayTimestamp")
    suspend fun deleteCompletedTasksBefore(yesterdayTimestamp: Long)
}

