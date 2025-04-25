package es.uc3m.android.travelshield.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import es.uc3m.android.travelshield.viewmodel.CountryViewModel
import androidx.compose.material3.OutlinedTextField
import com.google.android.gms.maps.CameraUpdateFactory
import es.uc3m.android.travelshield.viewmodel.CountryDoc
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.stringResource
import es.uc3m.android.travelshield.R

@Composable
fun MapScreen(navController: NavController, countryViewModel: CountryViewModel) {
    val countries by countryViewModel.countries.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.0, 0.0), 2f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            countries.forEach { country ->
                Marker(
                    state = MarkerState(
                        position = LatLng(country.genInfo.lat, country.genInfo.long)
                    ),
                    title = country.name
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .fillMaxWidth(0.9f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CountrySearchBar(countries, cameraPositionState)
        }
    }
}

@Composable
fun CountrySearchBar(
    countries: List<CountryDoc>,
    cameraPositionState: CameraPositionState
) {
    var searchQuery by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }

    val filteredSuggestions = countries.filter {
        it.name.contains(searchQuery.trim(), ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    showSuggestions = false
                })
            }
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                showSuggestions = it.isNotBlank() && filteredSuggestions.isNotEmpty()
            },
            placeholder = { Text(stringResource(R.string.search_country)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            singleLine = true
        )

        if (showSuggestions) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                    .clip(RoundedCornerShape(4.dp))
            ) {
                filteredSuggestions.forEach { country ->
                    Text(
                        text = country.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                searchQuery = country.name
                                showSuggestions = false
                                val latLng = LatLng(country.genInfo.lat, country.genInfo.long)
                                cameraPositionState.move(
                                    CameraUpdateFactory.newLatLngZoom(latLng, 5f)
                                )
                            }
                            .padding(12.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
