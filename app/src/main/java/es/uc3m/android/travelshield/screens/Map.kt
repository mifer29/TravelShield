package es.uc3m.android.travelshield.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolygonOptions
import com.google.maps.android.compose.*
import com.google.android.gms.maps.CameraUpdateFactory
import es.uc3m.android.travelshield.R
import es.uc3m.android.travelshield.viewmodel.CountryDoc
import es.uc3m.android.travelshield.viewmodel.CountryViewModel
import org.json.JSONArray
import org.json.JSONObject
import androidx.compose.ui.draw.clip
import androidx.lifecycle.viewmodel.compose.viewModel
import es.uc3m.android.travelshield.viewmodel.TripViewModel
import es.uc3m.android.travelshield.viewmodel.UserReviewsViewModel
import es.uc3m.android.travelshield.viewmodel.LikeViewModel

@Composable
fun MapScreen(
    navController: NavController,
    countryViewModel: CountryViewModel = viewModel(),
    tripViewModel: TripViewModel = viewModel(),
    userReviewsViewModel: UserReviewsViewModel = viewModel(),
    likeViewModel: LikeViewModel = viewModel()
) {
    val context = LocalContext.current
    val lang = context.resources.configuration.locales[0].language

    val countries by countryViewModel.countries.collectAsState()
    val trips by tripViewModel.trips.collectAsState()
    val reviews by userReviewsViewModel.reviews.collectAsState()
    val likedCountries by likeViewModel.likedCountries.collectAsState()
    LaunchedEffect(Unit) {
        likeViewModel.loadLikedCountries()
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.0, 0.0), 2f)
    }

    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }
    var selectedCountry by remember { mutableStateOf<CountryDoc?>(null) }

    val visitedColor = 0xAA4CAF50.toInt()
    val futureColor = 0xAA2196F3.toInt()
    val likeColor = 0xAAFBC02D.toInt()
    val defaultColor = 0xAA9E9E9E.toInt()

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            MapEffect(Unit) { map ->
                googleMap = map
                map.setOnPolygonClickListener { polygon ->
                    val countryTag = polygon.tag as? String
                    val country = countries.find { it.name.en == countryTag }

                    selectedCountry = country
                }
            }
        }

        LaunchedEffect(googleMap, countries, reviews, trips, likedCountries) {
            googleMap?.let { map ->
                map.clear()
                countries.forEach { country ->
                    Log.d("MapScreen", "💛 Liked countries: $likedCountries")

                    val displayName = if (lang == "es") country.name.es else country.name.en
                    country.geometry?.let { geoStr ->
                        try {
                            val geoJson = JSONObject(geoStr)
                            val color = when {
                                reviews.any { it.country == displayName } -> visitedColor
                                trips.any { it.country == country.name.en } -> futureColor
                                likedCountries.contains(country.name.en) -> likeColor
                                else -> defaultColor
                            }

                            if (likedCountries.contains(country.name.en)) {
                                Log.d("MapScreen", "⭐ Liked match: ${country.name.en}")
                            }

                            val drawPolygon: (JSONArray) -> Unit = { coords ->
                                val polygonOptions = PolygonOptions()
                                    .strokeColor(0xFF0077CC.toInt())
                                    .strokeWidth(2f)
                                    .fillColor(color)
                                    .clickable(true)

                                for (i in 0 until coords.length()) {
                                    val point = coords.getJSONArray(i)
                                    val latLng = LatLng(point.getDouble(1), point.getDouble(0))
                                    polygonOptions.add(latLng)
                                }
                                val polygon = map.addPolygon(polygonOptions)
                                polygon.tag = country.name.en

                            }

                            when (geoJson.getString("type")) {
                                "Polygon" -> {
                                    val rings = geoJson.getJSONArray("coordinates")
                                    if (rings.length() > 0) {
                                        drawPolygon(rings.getJSONArray(0))
                                    }
                                }

                                "MultiPolygon" -> {
                                    val polygons = geoJson.getJSONArray("coordinates")
                                    for (i in 0 until polygons.length()) {
                                        val rings = polygons.getJSONArray(i)
                                        if (rings.length() > 0) {
                                            drawPolygon(rings.getJSONArray(0))
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("MapScreen", "Error loading geometry for $displayName: ${e.message}")
                        }
                    }
                }
            }
        }

        CountrySearchBar(countries, cameraPositionState)

        LegendPanel(modifier = Modifier.align(Alignment.BottomStart))

        selectedCountry?.let { country ->
            val displayName = if (lang == "es") country.name.es else country.name.en
            val stateLabel = when {
                reviews.any { it.country == displayName } -> stringResource(R.string.visited)
                trips.any { it.country == displayName } -> stringResource(R.string.planned)
                likedCountries.contains(country.name.en) -> stringResource(R.string.liked)
                else -> stringResource(R.string.no_interaction)
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 90.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(text = displayName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(R.string.status_format, stateLabel),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    val id = selectedCountry?.id
                    if (id != null) {
                        selectedCountry = null
                        navController.navigate("country/$id")
                    }
                }) {
                    Text(stringResource(R.string.view_details))
                }


            }
        }

        LaunchedEffect(googleMap) {
            googleMap?.setOnMapClickListener {
                selectedCountry = null
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun LegendPanel(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(start = 16.dp, bottom = 50.dp)
    ) {
        LegendItem(color = Color(0xFF4CAF50), label = stringResource(R.string.visited))
        LegendItem(color = Color(0xFF2196F3), label = stringResource(R.string.planned))
        LegendItem(color = Color(0xFFFBC02D), label = stringResource(R.string.liked))
        LegendItem(color = Color(0xFF9E9E9E), label = stringResource(R.string.no_interaction))
    }
}

@Composable
fun CountrySearchBar(
    countries: List<CountryDoc>,
    cameraPositionState: CameraPositionState
) {
    val context = LocalContext.current
    val lang = context.resources.configuration.locales[0].language
    var searchQuery by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }

    val filteredSuggestions = countries.filter {
        val name = if (lang == "es") it.name.es else it.name.en
        name.contains(searchQuery.trim(), ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures { showSuggestions = false }
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
                    val displayName = if (lang == "es") country.name.es else country.name.en
                    Text(
                        text = displayName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                searchQuery = displayName
                                showSuggestions = false
                                try {
                                    val geoJson = JSONObject(country.geometry ?: return@clickable)
                                    val builder = LatLngBounds.Builder()

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
                                    Log.e("SearchBar", "Error centrando en $displayName: ${e.message}")
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
