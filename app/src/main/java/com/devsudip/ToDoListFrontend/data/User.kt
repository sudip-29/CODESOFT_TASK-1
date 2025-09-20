package com.devsudip.ToDoListFrontend.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity to store a single user profile. We use a fixed ID (1)
 * because there is only one user for this offline app.
 */
@Entity(tableName = "user_profile")
data class User(
    @PrimaryKey val id: Int = 1, // Fixed ID for single user
    val name: String
)
