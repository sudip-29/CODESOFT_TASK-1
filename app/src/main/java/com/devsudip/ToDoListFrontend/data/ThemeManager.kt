package com.devsudip.ToDoListFrontend.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create a DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemeManager(context: Context) {

    private val dataStore = context.dataStore

    // Define the key for our theme setting
    companion object {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    // Expose a Flow that emits the current theme setting
    val themeFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            // Return the saved value, defaulting to false (light mode) if nothing is saved yet
            preferences[IS_DARK_MODE] ?: false
        }

    // Function to save the theme setting
    suspend fun setTheme(isDarkMode: Boolean) {
        dataStore.edit { settings ->
            settings[IS_DARK_MODE] = isDarkMode
        }
    }
}
