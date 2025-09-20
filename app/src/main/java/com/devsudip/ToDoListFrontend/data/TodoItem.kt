package com.devsudip.ToDoListFrontend.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val category: String,
    val isCompleted: Boolean = false,
    val dueDate: Long = System.currentTimeMillis() + 86400000 * 5,
    val progress: Int = 0,
    val isHighPriority: Boolean = false,
    // NEW: We add a creation date to know when a task was made.
    // This is crucial for grouping and cleanup.
    val creationDate: Long = System.currentTimeMillis()
)

