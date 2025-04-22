package es.uc3m.android.travelshield.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.uc3m.android.travelshield.viewmodel.Trip
import es.uc3m.android.travelshield.viewmodel.TripViewModel
import java.util.*

@Composable
fun TripsScreen(
    navController: NavController
) {
    val tripViewModel: TripViewModel = viewModel()
    val trips by tripViewModel.trips.collectAsState() // Asegúrate de que `trips` es un Flow/List
    var isDialogOpen by remember { mutableStateOf(false) }
    var newTripCountry by remember { mutableStateOf(TextFieldValue("")) }
    var newTripDate by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)) {
        Text("My Trips", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Render trips only if they are not empty
        if (trips.isNotEmpty()) {
            trips.forEach { trip ->
                TripItem(trip) {

                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            Text("No trips found", style = MaterialTheme.typography.bodyMedium)
        }

        Button(
            onClick = { isDialogOpen = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add New Trip")
        }

        if (isDialogOpen) {
            AddTripDialog(
                onDismiss = { isDialogOpen = false },
                onAddTrip = { country, startDate ->
                    // No timestamp logic here anymore
                    tripViewModel.addTrip(Trip(country = country, startDate = startDate))
                    newTripCountry = TextFieldValue("")
                    newTripDate = ""
                    isDialogOpen = false


                },
                country = newTripCountry,
                setCountry = { newTripCountry = it },
                startDate = newTripDate,
                setStartDate = { newTripDate = it }
            )
        }
    }
}

@Composable
fun TripItem(trip: Trip, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Text(text = "Trip to: ${trip.country}", style = MaterialTheme.typography.titleSmall)
        Text(text = "Start Date: ${trip.startDate}", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun AddTripDialog(
    onDismiss: () -> Unit,
    onAddTrip: (String, String) -> Unit,
    country: TextFieldValue,
    setCountry: (TextFieldValue) -> Unit,
    startDate: String,
    setStartDate: (String) -> Unit
) {
    val context = LocalContext.current

    // Calendar setup
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    // Create the DatePickerDialog once using remember
    val datePickerDialog = remember {
        DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            setStartDate(selectedDate)
        }, year, month, day)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Trip") },
        text = {
            Column {
                OutlinedTextField(
                    value = country,
                    onValueChange = setCountry,
                    label = { Text("Country") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = startDate,
                    onValueChange = {}, // No need to change startDate here
                    label = { Text("Start Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    enabled = false // Keep it non-editable, just show the selected date
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onAddTrip(country.text, startDate)
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
