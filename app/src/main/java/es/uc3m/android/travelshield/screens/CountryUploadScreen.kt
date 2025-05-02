package es.uc3m.android.travelshield.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.FirebaseFirestore
import es.uc3m.android.travelshield.R
import es.uc3m.android.travelshield.viewmodel.*
import org.json.JSONObject
import android.graphics.BitmapFactory
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.graphics.Bitmap

@Composable
fun CountryUploadScreen(viewModel: CountryViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UploadCountriesButton(viewModel)
    }
}

@Composable
fun UploadCountriesButton(viewModel: CountryViewModel) {
    val context = LocalContext.current

    val geoJsonString = context.resources.openRawResource(R.raw.ne_50m_admin_0_countries)
        .bufferedReader().use { it.readText() }
    val geoJson = JSONObject(geoJsonString)
    val features = geoJson.getJSONArray("features")

    fun findGeometryByName(name: String): String? {
        for (i in 0 until features.length()) {
            val feature = features.getJSONObject(i)
            val props = feature.getJSONObject("properties")
            if (props.getString("NAME").equals(name, ignoreCase = true)) {
                return feature.getJSONObject("geometry").toString()
            }
        }
        return null
    }

    val countriesToAdd = listOf(
        CountryDoc(
            name = LocalizedText(
                en = "Australia",
                es = "Australia"
            ),
            genInfo = GenInfo(
                culture = LocalizedText(
                    en = "Australia has a laid-back culture that values outdoor activities, multiculturalism, and egalitarianism.",
                    es = "Australia tiene una cultura relajada que valora las actividades al aire libre, el multiculturalismo y el igualitarismo."
                ),
                description = LocalizedText(
                    en = "A vast island nation known for its beaches, deserts, and unique wildlife.",
                    es = "Una vasta nación insular conocida por sus playas, desiertos y vida silvestre única."
                ),
                food = LocalizedText(
                    en = "Popular dishes include meat pies, barbecued meats, and seafood.",
                    es = "Los platos populares incluyen pasteles de carne, carnes a la parrilla y mariscos."
                ),
                history = LocalizedText(
                    en = "Originally inhabited by Aboriginal peoples, colonized by the British in 1788.",
                    es = "Originalmente habitada por pueblos aborígenes, colonizada por los británicos en 1788."
                )
            ),
            health = Health(
                emergency = Emergency(ambulance = 0, poisonControl = 131126),
                tips = LocalizedText(
                    en = "Wear sunscreen, stay hydrated, and be cautious in rural areas.",
                    es = "Usa protector solar, mantente hidratado y ten precaución en zonas rurales."
                ),
                vaccines = listOf("Hepatitis A", "Tetanus")
            ),
            security = Security(
                commonScams = listOf("Charity scams", "Rental scams"),
                crimeLevel = LocalizedText(
                    en = "Very low in most urban areas",
                    es = "Muy bajo en la mayoría de áreas urbanas"
                ),
                emergencyContacts = EmergencyContacts(police = 0, embassy = "+61-2-6214-5600")
            ),
            transport = Transport(
                public = LocalizedText(
                    en = "Buses, trains, ferries, and light rail in most cities.",
                    es = "Autobuses, trenes, ferris y tranvías en la mayoría de las ciudades."
                ),
                apps = LocalizedText(
                    en = "TripView (Sydney), Metro Trains, Uber",
                    es = "TripView (Sídney), Metro Trains, Uber"
                ),
                airportToCity = LocalizedText(
                    en = "Airport link trains and shuttle buses operate in major cities.",
                    es = "Trenes y autobuses lanzadera conectan el aeropuerto con las ciudades principales."
                )
            ),
            visa = Visa(
                required = true,
                duration = LocalizedText(
                    en = "Visitor visas are usually valid for 3 months per entry.",
                    es = "Las visas de visitante suelen ser válidas por 3 meses por entrada."
                ),
                embassy = LocalizedText(
                    en = "Apply through the Australian Embassy or Consulate.",
                    es = "Solicítala en la embajada o consulado de Australia."
                )
            )
        ),

        CountryDoc(
            name = LocalizedText(
                en = "United States",
                es = "Estados Unidos"
            ),
            genInfo = GenInfo(
                culture = LocalizedText(
                    en = "Diverse and multicultural, with strong emphasis on individualism and innovation.",
                    es = "Diversa y multicultural, con fuerte énfasis en el individualismo y la innovación."
                ),
                description = LocalizedText(
                    en = "The USA offers everything from skyscrapers to natural wonders.",
                    es = "EE. UU. ofrece desde rascacielos hasta maravillas naturales."
                ),
                food = LocalizedText(
                    en = "Famous for burgers, BBQ, and international cuisines.",
                    es = "Famosa por las hamburguesas, la barbacoa y una amplia variedad de cocinas internacionales."
                ),
                history = LocalizedText(
                    en = "Founded in 1776 after independence from Britain.",
                    es = "Fundado en 1776 tras la independencia de Gran Bretaña."
                )
            ),
            health = Health(
                emergency = Emergency(ambulance = 911, poisonControl = 18002221222),
                tips = LocalizedText(
                    en = "Have travel insurance due to high medical costs.",
                    es = "Contrata un seguro de viaje por los altos costes médicos."
                ),
                vaccines = listOf("Hepatitis A", "Influenza")
            ),
            security = Security(
                commonScams = listOf("ATM skimming", "Fake ticket sales"),
                crimeLevel = LocalizedText(
                    en = "Varies by location; generally safe.",
                    es = "Varía por ubicación; generalmente seguro."
                ),
                emergencyContacts = EmergencyContacts(police = 911, embassy = "+1 202-467-9300")
            ),
            transport = Transport(
                public = LocalizedText(
                    en = "Transit varies by city; metro in big hubs.",
                    es = "El transporte varía según la ciudad; metro en grandes núcleos."
                ),
                apps = LocalizedText(
                    en = "Google Maps, Uber, Lyft",
                    es = "Google Maps, Uber, Lyft"
                ),
                airportToCity = LocalizedText(
                    en = "City buses, metro, and airport express available.",
                    es = "Autobuses urbanos, metro y expresos de aeropuerto disponibles."
                )
            ),
            visa = Visa(
                required = true,
                duration = LocalizedText(
                    en = "ESTA visa waiver allows 90 days.",
                    es = "El programa ESTA permite 90 días de estancia."
                ),
                embassy = LocalizedText(
                    en = "Apply at the US Embassy or Consulate.",
                    es = "Solicítala en la embajada o consulado de EE. UU."
                )
            )
        ),

        CountryDoc(
            name = LocalizedText(
                en = "Switzerland",
                es = "Suiza"
            ),
            genInfo = GenInfo(
                culture = LocalizedText(
                    en = "Values punctuality, cleanliness, and privacy.",
                    es = "Valora la puntualidad, la limpieza y la privacidad."
                ),
                description = LocalizedText(
                    en = "Known for its alpine scenery, neutrality, and finance.",
                    es = "Conocida por sus paisajes alpinos, neutralidad y finanzas."
                ),
                food = LocalizedText(
                    en = "Cheese fondue, raclette, chocolate, Rösti.",
                    es = "Fondue de queso, raclette, chocolate y Rösti."
                ),
                history = LocalizedText(
                    en = "Confederation since 1291. Neutral in wars.",
                    es = "Confederación desde 1291. Neutral en guerras."
                )
            ),
            health = Health(
                emergency = Emergency(ambulance = 144, poisonControl = 145),
                tips = LocalizedText(
                    en = "Stay hydrated at high altitudes and pack warm clothes.",
                    es = "Hidrátate en altitud y lleva ropa de abrigo."
                ),
                vaccines = listOf("Hepatitis A", "Tick-borne encephalitis")
            ),
            security = Security(
                commonScams = listOf("Pickpocketing in train stations"),
                crimeLevel = LocalizedText(
                    en = "Very low; one of the safest countries globally.",
                    es = "Muy bajo; uno de los países más seguros del mundo."
                ),
                emergencyContacts = EmergencyContacts(police = 117, embassy = "+41 31 357 70 11")
            ),
            transport = Transport(
                public = LocalizedText(
                    en = "Trains and buses cover the country reliably.",
                    es = "Los trenes y autobuses cubren el país de forma fiable."
                ),
                apps = LocalizedText(
                    en = "SBB Mobile, Google Maps, Uber",
                    es = "SBB Mobile, Google Maps, Uber"
                ),
                airportToCity = LocalizedText(
                    en = "Train connections from major airports are fast.",
                    es = "Las conexiones en tren desde aeropuertos son rápidas."
                )
            ),
            visa = Visa(
                required = true,
                duration = LocalizedText(
                    en = "Schengen visa allows up to 90 days in 180 days.",
                    es = "El visado Schengen permite hasta 90 días en 180."
                ),
                embassy = LocalizedText(
                    en = "Apply through the Swiss Embassy or Consulate.",
                    es = "Solicítala en la embajada o consulado suizo."
                )
            )
        )

    ).map { country ->
        country.copy(geometry = findGeometryByName(country.name.en))
    }

    Button(onClick = {
        val contextCopy = context // evitar captura implícita de `context`
        CoroutineScope(Dispatchers.IO).launch {
            val enrichedCountries = countriesToAdd.map { country ->
                val imageName = "country_" + country.name.en.lowercase().replace(" ", "_")
                val imageUrl = uploadImageFromDrawable(contextCopy, imageName)
                country.copy(imageUrl = imageUrl)
            }
            viewModel.addCountry(*enrichedCountries.toTypedArray())
        }
    }) {
        Text("Upload Countries")
    }


}

suspend fun uploadImageFromDrawable(context: Context, drawableName: String): String? {
    return try {
        val resId = context.resources.getIdentifier(drawableName, "drawable", context.packageName)
        println("→ Drawable name: $drawableName → resId: $resId")

        if (resId == 0) {
            println("⚠️ Drawable '$drawableName' no encontrado.")
            return null
        }

        val bitmap = BitmapFactory.decodeResource(context.resources, resId)
        val baos = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, baos)
        val data = baos.toByteArray()

        val storageRef = FirebaseStorage.getInstance().reference
            .child("countryImages/$drawableName.webp") // ← aquí va la carpeta deseada


        storageRef.putBytes(data).await()
        val url = storageRef.downloadUrl.await().toString()
        println("✅ Subida correcta de $drawableName → $url")
        url
    } catch (e: Exception) {
        println("❌ Error subiendo $drawableName: ${e.message}")
        null
    }
}
