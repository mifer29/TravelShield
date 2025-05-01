package es.uc3m.android.travelshield.viewmodel

import com.google.firebase.firestore.Exclude

data class CountryDoc(
    @get:Exclude var id: String? = null,
    val name: String = "",

    // General information about the country
    val genInfo: GenInfo = GenInfo(),

    // Health information
    val health: Health = Health(),

    // Security information
    val security: Security = Security(),

    // News related to the country
    val news: News = News(),

    // Transport information
    val transport: Transport = Transport(),

    // Visa requirements
    val visa: Visa = Visa(),

    // Geometry for map
    val geometry: String? = null

)

// Data classes for each section of the country information

data class GenInfo(
    val culture: String = "",
    val description: String = "",
    val food: String = "",
    val history: String = "",
    val lat: Double = 0.0,
    val long: Double = 0.0
)


data class Health(
    val emergency: Emergency = Emergency(),
    val tips: String = "",
    val vaccines: List<String> = emptyList()
)

data class Emergency(
    val ambulance: Long = 0,
    val poisonControl: Long = 0
)

data class Security(
    val commonScams: List<String> = emptyList(),
    val crimeLevel: String = "",
    val emergencyContacts: EmergencyContacts = EmergencyContacts()
)

data class EmergencyContacts(
    val police: Long = 0,
    val embassy: String = ""
)

data class News(
    val localNews: String = "",
    val weather: String = ""
)

data class Transport(
    val public: String = "",
    val apps: String = "",
    val airportToCity: String = ""
)

data class Visa(
    val required: Boolean = false,
    val duration: String = "",
    val embassy: String = ""
)
