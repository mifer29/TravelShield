package es.uc3m.android.travelshield.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.data.geojson.GeoJsonLayer
import es.uc3m.android.travelshield.R
import es.uc3m.android.travelshield.viewmodel.CountryDoc
import es.uc3m.android.travelshield.viewmodel.CountryViewModel
import org.json.JSONObject
import androidx.compose.ui.draw.clip

@Composable
fun MapScreen(navController: NavController, countryViewModel: CountryViewModel) {
    val countries by countryViewModel.countries.collectAsState()
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.0, 0.0), 2f)
    }

    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Mapa base
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            MapEffect(Unit) { map ->
                googleMap = map
            }
        }

        // Dibujar geometrías al tener acceso al mapa
        LaunchedEffect(googleMap, countries) {
            googleMap?.let { map ->
                countries.forEach { country ->
                    country.geometry?.let { geoStr ->
                        try {
                            val geoJson = JSONObject(geoStr)
                            val layer = GeoJsonLayer(map, geoJson)
                            layer.defaultPolygonStyle.fillColor = 0x5533B5E5
                            layer.defaultPolygonStyle.strokeColor = 0xFF0077CC.toInt()
                            layer.defaultPolygonStyle.strokeWidth = 2f
                            layer.addLayerToMap()
                        } catch (e: Exception) {
                            Log.e("MapScreen", "Error loading geometry for ${country.name}: ${e.message}")
                        }
                    }
                }
            }
        }

        // Buscador de países
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

                                try {
                                    val geoJson = JSONObject(country.geometry ?: return@clickable)
                                    val builder = com.google.android.gms.maps.model.LatLngBounds.Builder()

                                    when (geoJson.getString("type")) {
                                        "Polygon" -> {
                                            val rings = geoJson.getJSONArray("coordinates")
                                            for (i in 0 until rings.length()) {
                                                val ring = rings.getJSONArray(i)
                                                for (j in 0 until ring.length()) {
                                                    val point = ring.getJSONArray(j)
                                                    val latLng = LatLng(point.getDouble(1), point.getDouble(0))
                                                    builder.include(latLng)
                                                }
                                            }
                                        }

                                        "MultiPolygon" -> {
                                            val polygons = geoJson.getJSONArray("coordinates")
                                            for (i in 0 until polygons.length()) {
                                                val rings = polygons.getJSONArray(i)
                                                for (j in 0 until rings.length()) {
                                                    val ring = rings.getJSONArray(j)
                                                    for (k in 0 until ring.length()) {
                                                        val point = ring.getJSONArray(k)
                                                        val latLng = LatLng(point.getDouble(1), point.getDouble(0))
                                                        builder.include(latLng)
                                                    }
                                                }
                                            }
                                        }

                                        else -> {
                                            Log.w("SearchBar", "Tipo de geometría no soportado: ${geoJson.getString("type")}")
                                            return@clickable
                                        }
                                    }

                                    val bounds = builder.build()
                                    cameraPositionState.move(
                                        CameraUpdateFactory.newLatLngBounds(bounds, 100)
                                    )
                                } catch (e: Exception) {
                                    Log.e("SearchBar", "Error centrando en ${country.name}: ${e.message}")
                                }

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
