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
import com.google.firebase.firestore.FirebaseFirestore
import es.uc3m.android.travelshield.viewmodel.Trip
import es.uc3m.android.travelshield.viewmodel.TripViewModel
import java.util.*
import kotlinx.coroutines.tasks.await

@Composable
fun TripsScreen(navController: NavController) {
    val tripViewModel: TripViewModel = viewModel()
    val trips by tripViewModel.trips.collectAsState()

    var isDialogOpen by remember { mutableStateOf(false) }
    var newTripCountry by remember { mutableStateOf(TextFieldValue("")) }
    var newTripDate by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "My Trips",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (trips.isNotEmpty()) {
            trips.forEach { trip ->
                TripItem(trip)
                Spacer(modifier = Modifier.height(12.dp))
            }
        } else {
            Text(
                text = "No trips found",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = { isDialogOpen = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Trip to: ${trip.country}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Start Date: ${trip.startDate}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
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
    var allCountries by remember { mutableStateOf<List<String>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }

    // 🔥 Fetch countries from Firebase
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance("travelshield-db")
        try {
            val snapshot = db.collection("countries").get().await()
            allCountries = snapshot.documents.mapNotNull { it.getString("name") }
        } catch (e: Exception) {
            // Handle error if needed
        }
    }

    val filteredCountries = allCountries.filter {
        it.contains(country.text, ignoreCase = true)
    }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

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
                Box {
                    OutlinedTextField(
                        value = country,
                        onValueChange = {
                            setCountry(it)
                            expanded = true
                        },
                        label = { Text("Country") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = expanded && filteredCountries.isNotEmpty(),
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        filteredCountries.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    setCountry(TextFieldValue(selectionOption))
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = startDate,
                    onValueChange = {},
                    label = { Text("Start Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    enabled = false
                )
            }
        },
        confirmButton = {
            Button(onClick = { onAddTrip(country.text, startDate) }) {
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
