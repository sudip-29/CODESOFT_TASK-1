package com.devsudip.ToDoListFrontend.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // We use REPLACE so that saving a user profile just updates the
    // existing one, since we only ever have one user.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // A flow to get the current user. It will emit null if no user is set yet.
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUser(): Flow<User?>
}
