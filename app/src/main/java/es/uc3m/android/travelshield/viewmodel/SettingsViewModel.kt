package es.uc3m.android.travelshield.viewmodel

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.content.Context
import androidx.datastore.preferences.core.edit

val Context.dataStore by preferencesDataStore(name = "settings")

// Viewmodel to keep track of the dark mode in the settings
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val darkModeKey = booleanPreferencesKey("dark_mode_enabled")

    val isDarkMode = application.dataStore.data
        .map { preferences -> preferences[darkModeKey] ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { settings ->
                settings[darkModeKey] = enabled
            }
        }
    }
}
