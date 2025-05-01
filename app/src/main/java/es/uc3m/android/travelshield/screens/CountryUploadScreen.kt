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

    // Carga y búsqueda de geometría desde el archivo GeoJSON
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

    // Lista de países con geometría añadida
    val countriesToAdd = listOf(
        CountryDoc(
            name = "Australia",
            genInfo = GenInfo(
                culture = "Australia has a laid-back culture that values outdoor activities, multiculturalism, and egalitarianism. Sports and barbecues are popular social activities.",
                description = "A vast island nation known for its beaches, deserts, and unique wildlife. Major cities include Sydney, Melbourne, and Brisbane.",
                food = "Popular dishes include meat pies, barbecued meats, and seafood. Vegemite is a cultural staple.",
                history = "Originally inhabited by Aboriginal peoples, colonized by the British in 1788. Became a federation in 1901."
            ),
            health = Health(
                emergency = Emergency(ambulance = 0, poisonControl = 131126),
                tips = "Wear sunscreen, stay hydrated, and be cautious in rural areas where medical facilities may be distant.",
                vaccines = listOf("Hepatitis A", "Tetanus")
            ),
            security = Security(
                commonScams = listOf("Charity scams", "Rental scams in cities"),
                crimeLevel = "Very low in most urban areas",
                emergencyContacts = EmergencyContacts(police = 0, embassy = "+61-2-6214-5600")
            ),
            news = News(
                localNews = "ABC News, The Sydney Morning Herald, and The Age are popular sources.",
                weather = "Ranges from tropical in the north to temperate in the south. Summer is December to February."
            ),
            transport = Transport(
                public = "Buses, trains, ferries, and light rail in most cities.",
                apps = "TripView (Sydney), Metro Trains, Uber",
                airportToCity = "Airport link trains and shuttle buses operate in major cities."
            ),
            visa = Visa(
                required = true,
                duration = "Visitor visas are usually valid for 3 months per entry.",
                embassy = "You can apply through the Australian Embassy or Consulate in your country. They provide visa types and application instructions."
            )
        ),
        CountryDoc(
            name = "United States of America",
            genInfo = GenInfo(
                culture = "Diverse and multicultural, with strong emphasis on individualism and innovation. Major cultural exports include music, cinema, and technology.",
                description = "Spanning six time zones, the USA offers everything from skyscrapers in New York to natural wonders like the Grand Canyon and Yellowstone.",
                food = "Famous for burgers, BBQ, and a wide range of international cuisines due to immigration.",
                history = "Founded in 1776 after independence from Britain. Played major roles in both World Wars and remains a global power."
            ),
            health = Health(
                emergency = Emergency(ambulance = 911, poisonControl = 18002221222),
                tips = "Travelers should have travel insurance due to high medical costs. Tap water is safe to drink.",
                vaccines = listOf("Hepatitis A", "Influenza")
            ),
            security = Security(
                commonScams = listOf("Online booking fraud", "ATM skimming", "Fake ticket sales"),
                crimeLevel = "Varies by city and state. Petty crime in urban areas; generally safe.",
                emergencyContacts = EmergencyContacts(police = 911, embassy = "+1 202-467-9300")
            ),
            news = News(
                localNews = "CNN, NBC News, Fox News, and The New York Times are major outlets.",
                weather = "Highly variable: tropical in Florida, arid in Arizona, and snowy in the north in winter."
            ),
            transport = Transport(
                public = "Public transit varies; available in big cities like NYC, Chicago, San Francisco.",
                apps = "Google Maps, Uber, Lyft",
                airportToCity = "City buses, metro systems, and airport express trains operate in major hubs."
            ),
            visa = Visa(
                required = true,
                duration = "ESTA visa waiver allows 90 days. Tourist visa (B2) can be longer.",
                embassy = "You can apply for a visa through the US Embassy or Consulate in your home country. They provide the required documentation and appointments."
            )
        ),
        CountryDoc(
            name = "Switzerland",
            genInfo = GenInfo(
                culture = "Swiss culture values punctuality, cleanliness, and privacy. It is influenced by German, French, and Italian regions, reflected in languages and customs.",
                description = "Switzerland is known for its alpine scenery, lakes, precision manufacturing, and political neutrality.",
                food = "Famous for cheese (fondue, raclette), chocolate, and Rösti (fried potato dish).",
                history = "A confederation since 1291. Known for neutrality in wars and home to many international organizations."
            ),
            health = Health(
                emergency = Emergency(ambulance = 144, poisonControl = 145),
                tips = "Healthcare is excellent. Stay hydrated at high altitudes and pack warm clothes for alpine areas.",
                vaccines = listOf("Hepatitis A", "Tick-borne encephalitis for outdoor travelers")
            ),
            security = Security(
                commonScams = listOf("Pickpocketing in train stations", "Rental deposit fraud"),
                crimeLevel = "Very low; consistently ranks among the safest countries globally.",
                emergencyContacts = EmergencyContacts(police = 117, embassy = "+41 31 357 70 11")
            ),
            news = News(
                localNews = "SwissInfo, Neue Zürcher Zeitung (NZZ), Le Temps.",
                weather = "Mild summers and cold winters. Heavy snowfall in mountain regions during winter."
            ),
            transport = Transport(
                public = "Highly reliable train and bus networks cover the entire country.",
                apps = "SBB Mobile, Google Maps, Uber",
                airportToCity = "Train connections from Zurich, Geneva, and Basel airports are fast and efficient."
            ),
            visa = Visa(
                required = true,
                duration = "Schengen visa allows up to 90 days in a 180-day period.",
                embassy = "You can apply for a visa through the Swiss Embassy or Consulate in your country. Check local procedures and required documents."
            )
        )
    ).map { country ->
        country.copy(geometry = findGeometryByName(country.name))
    }

    Button(onClick = {
        viewModel.addCountry(*countriesToAdd.toTypedArray())
    }) {
        Text("Upload Countries")
    }
}
