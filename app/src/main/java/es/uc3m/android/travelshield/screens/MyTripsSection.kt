package es.uc3m.android.travelshield.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import es.uc3m.android.travelshield.viewmodel.Trip
import es.uc3m.android.travelshield.viewmodel.TripViewModel
import java.util.*

@Composable
fun MyTripsSection(
    tripViewModel: TripViewModel
) {
    val trips by tripViewModel.trips.collectAsState()
    var isDialogOpen by remember { mutableStateOf(false) }
    var newTripCountry by remember { mutableStateOf(TextFieldValue("")) }
    var newTripDate by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Text("My Trips", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        trips.forEach { trip ->
            TripItem(trip)
            Spacer(modifier = Modifier.height(8.dp))
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
fun TripItem(trip: Trip) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
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

    // Calendar variables
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    // Show DatePickerDialog when clicked
    val datePickerDialog = remember {
        DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val selectedDate = "${selectedDayOfMonth}/${selectedMonth + 1}/$selectedYear"
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
                    onValueChange = {}, // Make this read-only
                    label = { Text("Start Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    enabled = false // Optional: prevent typing
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

