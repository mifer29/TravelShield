package es.uc3m.android.travelshield.screens

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.uc3m.android.travelshield.R
import java.util.*
import androidx.compose.ui.res.stringResource

@Composable
fun SettingsScreen(navController: NavController) {
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
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(R.string.settings_language),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

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

    // Reinicia la actividad actual para aplicar el idioma
    (context as? Activity)?.recreate()
}
