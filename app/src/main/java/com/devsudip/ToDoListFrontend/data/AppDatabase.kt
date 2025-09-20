package com.devsudip.ToDoListFrontend.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Database version is now 3 to include the User table and the new field in TodoItem.
@Database(entities = [TodoItem::class, User::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao
    abstract fun userDao(): UserDao // Add the new DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    // We keep using destructive migration for simplicity during development.
                    // This will clear all data when the app is upgraded.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

