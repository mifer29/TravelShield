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
            abbreviation = "au",
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
                ),
                lat = -35.2809,
                long = 149.1300
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
                en = "United States of America",
                es = "Estados Unidos"
            ),
            abbreviation = "us",
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
                ),
                lat = 38.9072,
                long = -77.0369
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
            abbreviation = "ch",
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
                ),
                lat = 46.9481,
                long = 7.4474
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
        ),

        CountryDoc(
            name = LocalizedText(en = "France", es = "Francia"),
            abbreviation = "fr",
            genInfo = GenInfo(
                culture = LocalizedText(
                    en = "Known for its elegance, cuisine, and emphasis on art and fashion.",
                    es = "Conocida por su elegancia, gastronomía y énfasis en el arte y la moda."
                ),
                description = LocalizedText(
                    en = "France features the Eiffel Tower, Riviera, and charming countryside.",
                    es = "Francia ofrece la Torre Eiffel, la Riviera y encantadores paisajes rurales."
                ),
                food = LocalizedText(
                    en = "Baguettes, cheese, croissants, and wine are staples.",
                    es = "Baguettes, quesos, croissants y vino son elementos básicos."
                ),
                history = LocalizedText(
                    en = "A major European power with deep historical roots from the Roman Empire to today.",
                    es = "Una potencia europea con profundas raíces históricas desde el Imperio Romano hasta hoy."
                ),
                lat = 48.8566,
                long = 2.3522
            ),
            health = Health(
                emergency = Emergency(ambulance = 15, poisonControl = 800595595),
                tips = LocalizedText(
                    en = "Carry your EHIC card and know emergency numbers.",
                    es = "Lleva tu tarjeta sanitaria europea y conoce los números de emergencia."
                ),
                vaccines = listOf("Tetanus", "Hepatitis A")
            ),
            security = Security(
                commonScams = listOf("Pickpocketing", "Fake petitions"),
                crimeLevel = LocalizedText(
                    en = "Moderate in tourist areas.",
                    es = "Moderado en zonas turísticas."
                ),
                emergencyContacts = EmergencyContacts(police = 17, embassy = "+33 1 44 43 18 00")
            ),
            transport = Transport(
                public = LocalizedText(
                    en = "Excellent metro, RER trains, and buses.",
                    es = "Excelente metro, trenes RER y autobuses."
                ),
                apps = LocalizedText(
                    en = "Citymapper, SNCF, Uber",
                    es = "Citymapper, SNCF, Uber"
                ),
                airportToCity = LocalizedText(
                    en = "Trains and shuttle buses from CDG and Orly airports.",
                    es = "Trenes y autobuses lanzadera desde los aeropuertos CDG y Orly."
                )
            ),
            visa = Visa(
                required = false,
                duration = LocalizedText(
                    en = "90 days within the Schengen zone.",
                    es = "90 días dentro del espacio Schengen."
                ),
                embassy = LocalizedText(
                    en = "Not required for Spanish citizens.",
                    es = "No se requiere para ciudadanos españoles."
                )
            )
        ),

        CountryDoc(
            name = LocalizedText(en = "Italy", es = "Italia"),
            abbreviation = "it",
            genInfo = GenInfo(
                culture = LocalizedText(
                    en = "Famous for its art, history, and passionate way of life.",
                    es = "Famosa por su arte, historia y estilo de vida apasionado."
                ),
                description = LocalizedText(
                    en = "Home to Rome, Venice, Florence, and breathtaking coastlines.",
                    es = "Hogar de Roma, Venecia, Florencia y espectaculares costas."
                ),
                food = LocalizedText(
                    en = "Known for pizza, pasta, gelato, and regional wines.",
                    es = "Conocida por la pizza, la pasta, el helado y los vinos regionales."
                ),
                history = LocalizedText(
                    en = "Birthplace of the Roman Empire and Renaissance.",
                    es = "Cuna del Imperio Romano y del Renacimiento."
                ),
                lat = 41.9028,
                long = 12.4964
            ),
            health = Health(
                emergency = Emergency(ambulance = 118, poisonControl = 800883300),
                tips = LocalizedText(
                    en = "EHIC card is valid; pharmacies are widely available.",
                    es = "La tarjeta sanitaria europea es válida; hay muchas farmacias disponibles."
                ),
                vaccines = listOf("Tetanus", "Hepatitis A")
            ),
            security = Security(
                commonScams = listOf("Overpriced taxis", "Fake tour guides"),
                crimeLevel = LocalizedText(
                    en = "Generally safe, watch for petty theft in cities.",
                    es = "Generalmente seguro, ten cuidado con pequeños robos en las ciudades."
                ),
                emergencyContacts = EmergencyContacts(police = 112, embassy = "+39 06 6840 4001")
            ),
            transport = Transport(
                public = LocalizedText(
                    en = "Well-connected trains and city metro systems.",
                    es = "Trenes bien conectados y sistemas de metro en las ciudades."
                ),
                apps = LocalizedText(
                    en = "Trenitalia, Italo, Moovit",
                    es = "Trenitalia, Italo, Moovit"
                ),
                airportToCity = LocalizedText(
                    en = "Leonardo Express and buses serve major airports.",
                    es = "Leonardo Express y autobuses conectan los principales aeropuertos."
                )
            ),
            visa = Visa(
                required = false,
                duration = LocalizedText(
                    en = "90 days within Schengen area.",
                    es = "90 días dentro del espacio Schengen."
                ),
                embassy = LocalizedText(
                    en = "No visa needed for Spanish nationals.",
                    es = "No se necesita visado para ciudadanos españoles."
                )
            )
        ),
        CountryDoc(
            name = LocalizedText(en = "Portugal", es = "Portugal"),
            abbreviation = "pt",
            genInfo = GenInfo(
                culture = LocalizedText(
                    en = "Warm and welcoming, with a deep connection to the sea and tradition.",
                    es = "Acogedora y cálida, con una profunda conexión con el mar y la tradición."
                ),
                description = LocalizedText(
                    en = "Portugal offers stunning coastlines, historic cities, and rich culture.",
                    es = "Portugal ofrece impresionantes costas, ciudades históricas y una rica cultura."
                ),
                food = LocalizedText(
                    en = "Famous for bacalhau, pastries like pastéis de nata, and seafood.",
                    es = "Famoso por el bacalao, los pasteles como los pastéis de nata y los mariscos."
                ),
                history = LocalizedText(
                    en = "A seafaring nation with a powerful empire in the 15th and 16th centuries.",
                    es = "Una nación marinera con un poderoso imperio en los siglos XV y XVI."
                ),
                lat = 38.7169,
                long = -9.1399
            ),
            health = Health(
                emergency = Emergency(ambulance = 112, poisonControl = 800250250),
                tips = LocalizedText(
                    en = "EHIC card is valid; emergency care is accessible.",
                    es = "La tarjeta sanitaria europea es válida; la atención de emergencia es accesible."
                ),
                vaccines = listOf("Tetanus", "Hepatitis A")
            ),
            security = Security(
                commonScams = listOf("Fake charity collectors", "Overpriced tuk-tuk rides"),
                crimeLevel = LocalizedText(
                    en = "Low crime rate; pickpocketing can occur in tourist areas.",
                    es = "Baja tasa de criminalidad; puede haber carteristas en zonas turísticas."
                ),
                emergencyContacts = EmergencyContacts(police = 112, embassy = "+351 213 472 384")
            ),
            transport = Transport(
                public = LocalizedText(
                    en = "Efficient trams, metro, and buses in cities like Lisbon and Porto.",
                    es = "Tranvías, metro y autobuses eficientes en ciudades como Lisboa y Oporto."
                ),
                apps = LocalizedText(
                    en = "Carris, CP, Moovit",
                    es = "Carris, CP, Moovit"
                ),
                airportToCity = LocalizedText(
                    en = "Metro and shuttle buses connect airports to city centers.",
                    es = "El metro y autobuses lanzadera conectan los aeropuertos con el centro."
                )
            ),
            visa = Visa(
                required = false,
                duration = LocalizedText(
                    en = "90 days in Schengen area.",
                    es = "90 días dentro del espacio Schengen."
                ),
                embassy = LocalizedText(
                    en = "Visa not required for Spanish citizens.",
                    es = "No se requiere visado para ciudadanos españoles."
                )
            )
        ),
        CountryDoc(
            name = LocalizedText(en = "Germany", es = "Alemania"),
            abbreviation = "de",
            genInfo = GenInfo(
                culture = LocalizedText(
                    en = "Efficient, structured, and rich in regional traditions.",
                    es = "Eficiente, estructurada y rica en tradiciones regionales."
                ),
                description = LocalizedText(
                    en = "Germany combines modern cities with picturesque towns and forests.",
                    es = "Alemania combina ciudades modernas con pueblos pintorescos y bosques."
                ),
                food = LocalizedText(
                    en = "Bratwurst, pretzels, schnitzel, and regional beers.",
                    es = "Bratwurst, pretzels, schnitzel y cervezas regionales."
                ),
                history = LocalizedText(
                    en = "From the Holy Roman Empire to reunification after the Berlin Wall.",
                    es = "Desde el Sacro Imperio Romano hasta la reunificación tras el Muro de Berlín."
                ),
                lat = 52.5200,
                long = 13.4050
            ),
            health = Health(
                emergency = Emergency(ambulance = 112, poisonControl = 3019240),
                tips = LocalizedText(
                    en = "Carry EHIC and be aware of emergency services.",
                    es = "Lleva la tarjeta sanitaria europea y conoce los servicios de emergencia."
                ),
                vaccines = listOf("Tetanus", "Measles")
            ),
            security = Security(
                commonScams = listOf("ATM fraud", "Fake ticket inspectors"),
                crimeLevel = LocalizedText(
                    en = "Generally safe; be cautious in crowded places.",
                    es = "Generalmente seguro; ten cuidado en lugares concurridos."
                ),
                emergencyContacts = EmergencyContacts(police = 110, embassy = "+49 30 254 0070")
            ),
            transport = Transport(
                public = LocalizedText(
                    en = "Excellent U-Bahn, S-Bahn, trams, and intercity trains.",
                    es = "Excelente red de U-Bahn, S-Bahn, tranvías y trenes interurbanos."
                ),
                apps = LocalizedText(
                    en = "DB Navigator, BVG, FlixBus",
                    es = "DB Navigator, BVG, FlixBus"
                ),
                airportToCity = LocalizedText(
                    en = "Trains and express buses available in all major airports.",
                    es = "Trenes y autobuses exprés en todos los aeropuertos principales."
                )
            ),
            visa = Visa(
                required = false,
                duration = LocalizedText(
                    en = "90 days in the Schengen area.",
                    es = "90 días dentro del espacio Schengen."
                ),
                embassy = LocalizedText(
                    en = "No visa required for Spanish nationals.",
                    es = "No se requiere visado para ciudadanos españoles."
                )
            )
        ),
        CountryDoc(
            name = LocalizedText(en = "United Kingdom", es = "Reino Unido"),
            abbreviation = "gb",
            genInfo = GenInfo(
                culture = LocalizedText(
                    en = "A blend of tradition and modernity, with a strong pub and music culture.",
                    es = "Una mezcla de tradición y modernidad, con una fuerte cultura de pubs y música."
                ),
                description = LocalizedText(
                    en = "The UK features iconic landmarks, castles, and green countryside.",
                    es = "El Reino Unido cuenta con monumentos icónicos, castillos y un campo verde."
                ),
                food = LocalizedText(
                    en = "Fish and chips, English breakfast, and Indian cuisine are popular.",
                    es = "El fish and chips, el desayuno inglés y la cocina india son populares."
                ),
                history = LocalizedText(
                    en = "Once a global empire, it has shaped modern democracy and literature.",
                    es = "Antiguo imperio global que ha influido en la democracia y literatura modernas."
                ),
                lat = 51.5074,
                long = -0.1278
            ),
            health = Health(
                emergency = Emergency(ambulance = 999, poisonControl = 111),
                tips = LocalizedText(
                    en = "Healthcare is good; travel insurance recommended after Brexit.",
                    es = "Sanidad de calidad; se recomienda seguro de viaje tras el Brexit."
                ),
                vaccines = listOf("Tetanus", "Hepatitis A")
            ),
            security = Security(
                commonScams = listOf("Ticket fraud", "Fake charity collections"),
                crimeLevel = LocalizedText(
                    en = "Generally safe; common crimes include pickpocketing.",
                    es = "Generalmente seguro; los delitos comunes incluyen carterismo."
                ),
                emergencyContacts = EmergencyContacts(police = 999, embassy = "+44 207 589 8989")
            ),
            transport = Transport(
                public = LocalizedText(
                    en = "Extensive underground, trains, and buses.",
                    es = "Extensa red de metro, trenes y autobuses."
                ),
                apps = LocalizedText(
                    en = "Citymapper, Trainline, Uber",
                    es = "Citymapper, Trainline, Uber"
                ),
                airportToCity = LocalizedText(
                    en = "Heathrow and Gatwick have trains and express buses.",
                    es = "Heathrow y Gatwick tienen trenes y autobuses exprés."
                )
            ),
            visa = Visa(
                required = false,
                duration = LocalizedText(
                    en = "No visa needed for stays up to 6 months.",
                    es = "No se requiere visado para estancias de hasta 6 meses."
                ),
                embassy = LocalizedText(
                    en = "Not required for tourism visits from Spain.",
                    es = "No se requiere para visitas turísticas desde España."
                )
            )
        ),
        CountryDoc(
            name = LocalizedText(en = "Morocco", es = "Marruecos"),
            abbreviation = "ma",
            genInfo = GenInfo(
                culture = LocalizedText(
                    en = "A vibrant mix of Arab, Berber, and French influences with strong hospitality traditions.",
                    es = "Una mezcla vibrante de influencias árabes, bereberes y francesas con una fuerte tradición de hospitalidad."
                ),
                description = LocalizedText(
                    en = "Morocco offers deserts, mountains, beaches, and historic medinas.",
                    es = "Marruecos ofrece desiertos, montañas, playas y medinas históricas."
                ),
                food = LocalizedText(
                    en = "Tajine, couscous, mint tea, and traditional sweets are staples.",
                    es = "Tajín, cuscús, té de menta y dulces tradicionales son básicos."
                ),
                history = LocalizedText(
                    en = "An ancient land with a rich Islamic and pre-Islamic heritage.",
                    es = "Una tierra antigua con un rico legado islámico y preislámico."
                ),
                lat = 34.0209,
                long = -6.8416
            ),
            health = Health(
                emergency = Emergency(ambulance = 150, poisonControl = 0),
                tips = LocalizedText(
                    en = "Drink bottled water and be cautious with raw food.",
                    es = "Bebe agua embotellada y ten cuidado con los alimentos crudos."
                ),
                vaccines = listOf("Hepatitis A", "Typhoid")
            ),
            security = Security(
                commonScams = listOf("Tourist overcharging", "Fake guides"),
                crimeLevel = LocalizedText(
                    en = "Moderate in big cities; avoid isolated areas at night.",
                    es = "Moderado en grandes ciudades; evita zonas aisladas de noche."
                ),
                emergencyContacts = EmergencyContacts(police = 19, embassy = "+212 537 63 31 90")
            ),
            transport = Transport(
                public = LocalizedText(
                    en = "Taxis are common; trains connect major cities.",
                    es = "Los taxis son comunes; los trenes conectan las principales ciudades."
                ),
                apps = LocalizedText(
                    en = "Careem, ONCF, Google Maps",
                    es = "Careem, ONCF, Google Maps"
                ),
                airportToCity = LocalizedText(
                    en = "Taxis and some shuttles connect airports to cities.",
                    es = "Taxis y algunos autobuses lanzadera conectan aeropuertos con las ciudades."
                )
            ),
            visa = Visa(
                required = false,
                duration = LocalizedText(
                    en = "Up to 90 days without a visa.",
                    es = "Hasta 90 días sin visado."
                ),
                embassy = LocalizedText(
                    en = "Visa-free for Spanish citizens for short stays.",
                    es = "Sin visado para ciudadanos españoles en estancias cortas."
                )
            )
        ),
        CountryDoc(
            name = LocalizedText(en = "Greece", es = "Grecia"),
            abbreviation = "gr",
            genInfo = GenInfo(
                culture = LocalizedText(
                    en = "Rooted in ancient traditions, with strong family values and Mediterranean lifestyle.",
                    es = "Con profundas raíces en tradiciones antiguas, fuertes lazos familiares y estilo de vida mediterráneo."
                ),
                description = LocalizedText(
                    en = "Greece is famous for its islands, ancient ruins, and turquoise waters.",
                    es = "Grecia es famosa por sus islas, ruinas antiguas y aguas turquesas."
                ),
                food = LocalizedText(
                    en = "Dishes like moussaka, souvlaki, feta cheese, and olives are staples.",
                    es = "Platos como la musaca, el souvlaki, el queso feta y las aceitunas son esenciales."
                ),
                history = LocalizedText(
                    en = "Cradle of Western civilization, philosophy, and democracy.",
                    es = "Cuna de la civilización occidental, la filosofía y la democracia."
                ),
                lat = 37.9838,
                long = 23.7275
            ),
            health = Health(
                emergency = Emergency(ambulance = 166, poisonControl = 2107793777),
                tips = LocalizedText(
                    en = "EHIC is accepted; healthcare access can vary by island.",
                    es = "La tarjeta sanitaria europea es aceptada; el acceso sanitario puede variar entre islas."
                ),
                vaccines = listOf("Tetanus", "Hepatitis A")
            ),
            security = Security(
                commonScams = listOf("Overpriced restaurants", "Taxi scams"),
                crimeLevel = LocalizedText(
                    en = "Generally low; be alert in crowded tourist zones.",
                    es = "Generalmente bajo; mantente alerta en zonas turísticas concurridas."
                ),
                emergencyContacts = EmergencyContacts(police = 100, embassy = "+30 210 92 23 600")
            ),
            transport = Transport(
                public = LocalizedText(
                    en = "Athens has metro and buses; ferries connect the islands.",
                    es = "Atenas tiene metro y autobuses; los ferris conectan las islas."
                ),
                apps = LocalizedText(
                    en = "OASA Telematics, Ferryhopper, Beat",
                    es = "OASA Telematics, Ferryhopper, Beat"
                ),
                airportToCity = LocalizedText(
                    en = "Metro and buses link airports with city centers.",
                    es = "El metro y los autobuses conectan los aeropuertos con el centro."
                )
            ),
            visa = Visa(
                required = false,
                duration = LocalizedText(
                    en = "90 days in Schengen area.",
                    es = "90 días dentro del espacio Schengen."
                ),
                embassy = LocalizedText(
                    en = "No visa needed for Spanish nationals.",
                    es = "No se necesita visado para ciudadanos españoles."
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
        println("Drawable name: $drawableName → resId: $resId")

        if (resId == 0) {
            println("Drawable '$drawableName' no encontrado.")
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
        println("Subida correcta de $drawableName → $url")
        url
    } catch (e: Exception) {
        println("Error subiendo $drawableName: ${e.message}")
        null
    }
}
