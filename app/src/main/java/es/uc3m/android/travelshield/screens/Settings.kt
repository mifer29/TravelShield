package es.uc3m.android.travelshield.screens

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import es.uc3m.android.travelshield.R
import es.uc3m.android.travelshield.viewmodel.SettingsViewModel
import java.util.*
import androidx.compose.ui.res.stringResource

@Composable
fun SettingsScreen(navController: NavController) {
    // Access the SettingsViewModel
    val settingsViewModel: SettingsViewModel = viewModel()

    // Collect the current dark mode setting from the ViewModel
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

    // Define the current language and dark mode options
    val context = LocalContext.current
    val currentLang = remember { getCurrentLanguage() }
    var selectedLanguage by remember { mutableStateOf(currentLang) }

    val languages = listOf(
        LanguageOption("es", stringResource(R.string.spanish)),
        LanguageOption("en", stringResource(R.string.english))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(R.string.settings_language),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Language Options
        languages.forEach { lang ->
            LanguageOptionRow(
                label = lang.displayName,
                selected = selectedLanguage == lang.code,
                onSelect = {
                    selectedLanguage = lang.code
                    setLocaleAndRestart(context, lang.code)
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dark Mode Toggle
        Text(
            text = stringResource(R.string.settings_dark_mode),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // Toggle dark mode on click
                    settingsViewModel.setDarkMode(!isDarkMode)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = isDarkMode,
                onCheckedChange = { settingsViewModel.setDarkMode(it) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = if (isDarkMode) stringResource(R.string.enabled) else stringResource(R.string.disabled))
        }
    }
}

@Composable
fun LanguageOptionRow(label: String, selected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
    }
}

data class LanguageOption(val code: String, val displayName: String)

fun getCurrentLanguage(): String {
    return Locale.getDefault().language
}

fun setLocaleAndRestart(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)

    // Restart the activity to apply the language change
    (context as? Activity)?.recreate()
}
