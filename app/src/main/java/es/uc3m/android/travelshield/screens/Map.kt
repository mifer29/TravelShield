package es.uc3m.android.travelshield.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle
import es.uc3m.android.travelshield.R
import kotlinx.coroutines.*
import org.json.JSONObject

@Composable
fun MapScreen(navController: NavController) {
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.0, 0.0), 2f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Hook para obtener el mapa real
            MapEffect(Unit) { map ->
                // Creamos una corrutina para no bloquear la UI
                CoroutineScope(Dispatchers.Main).launch {
                    loadGeoJsonLayer(context, map)
                }
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MAP",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

suspend fun loadGeoJsonLayer(context: Context, map: GoogleMap) {
    try {
        // Leemos el archivo en segundo plano
        val json = withContext(Dispatchers.IO) {
            val inputStream = context.resources.openRawResource(R.raw.world)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            JSONObject(jsonString)
        }

        val layer = GeoJsonLayer(map, json)

        var count = 0
        for (feature in layer.features) {
            val type = feature.geometry.geometryType
            if (type == "Polygon" || type == "MultiPolygon") {
                val style = GeoJsonPolygonStyle().apply {
                    setFillColor(0x5534A853)  // semitransparente
                    setStrokeColor(0xFF34A853.toInt())
                    setStrokeWidth(1f)
                }
                feature.polygonStyle = style
                count++
            }
        }
        layer.addLayerToMap()

    } catch (e: Exception) {
        Log.e("GeoJSON", "Error al cargar el GeoJSON", e)
    }
}
