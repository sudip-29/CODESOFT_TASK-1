package com.devsudip.ToDoListFrontend.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUser(): Flow<User?>
}
