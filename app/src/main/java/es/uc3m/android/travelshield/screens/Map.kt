package es.uc3m.android.travelshield.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.uc3m.android.travelshield.viewmodel.CountryViewModel
import es.uc3m.android.travelshield.viewmodel.CountryDoc
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import es.uc3m.android.travelshield.R



@Composable
fun MapScreen(navController: NavController, viewModel: CountryViewModel = viewModel()) {
    var showAddCountryDialog by remember { mutableStateOf(false) }
    var countryToEdit by remember { mutableStateOf<CountryDoc?>(null) }
    val context = LocalContext.current
    val countries by viewModel.countries.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddCountryDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_country))
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn {
                items(countries) { country ->
                    CountryItem(
                        country = country,
                        onCountryClick = { countryToEdit = it },
                        onDeleteClick = { viewModel.deleteCountry(it.id!!) }
                    )
                }
            }
        }
    }

    if (showAddCountryDialog) {
        AddCountryDialog(
            onDismiss = { showAddCountryDialog = false },
            onAddCountry = { title, body ->
                viewModel.addCountry(title, body)
                showAddCountryDialog = false
            }
        )
    }

    countryToEdit?.let { country ->
        EditCountryDialog(
            country = country,
            onDismiss = { countryToEdit = null },
            onUpdateCountry = { name, vaccine ->
                viewModel.updateCountry(country.id!!, name, vaccine)
                countryToEdit = null
            }
        )
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            // Reset message to avoid showing it repeatedly (e.g., on configuration changes)
            viewModel.showToast(null)
        }
    }

}

@Composable
fun CountryItem(
    country: CountryDoc,
    onCountryClick: (CountryDoc) -> Unit,
    onDeleteClick: (CountryDoc) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onCountryClick(country) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = country.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = country.vaccine, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { onDeleteClick(country) }) {
                Text(stringResource(R.string.delete))
            }
        }
    }
}

@Composable
fun AddCountryDialog(
    onDismiss: () -> Unit,
    onAddCountry: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var vaccine by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank() && vaccine.isNotBlank()) {
                    onAddCountry(name, vaccine)
                }
            }) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        title = { Text(stringResource(R.string.add_country)) },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name)) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = vaccine,
                    onValueChange = { vaccine = it },
                    label = { Text(stringResource(R.string.vaccine)) }
                )
            }
        }
    )
}

@Composable
fun EditCountryDialog(
    country: CountryDoc,
    onDismiss: () -> Unit,
    onUpdateCountry: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(country.name) }
    var body by remember { mutableStateOf(country.vaccine) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                if (title.isNotBlank() && body.isNotBlank()) {
                    onUpdateCountry(title, body)
                }
            }) {
                Text(stringResource(R.string.update))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        title = { Text(stringResource(R.string.edit_country)) },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.name)) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text(stringResource(R.string.vaccine)) }
                )
            }
        }
    )
}

