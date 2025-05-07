package es.uc3m.android.travelshield.viewmodel

import com.google.firebase.firestore.Exclude


// Country data model

data class CountryDoc(
    @get:Exclude var id: String? = null,
    val name: LocalizedText = LocalizedText(),
    val abbreviation: String? = null,
    val genInfo: GenInfo = GenInfo(),
    val health: Health = Health(),
    val security: Security = Security(),
    val transport: Transport = Transport(),
    val visa: Visa = Visa(),
    val geometry: String? = null,
    val imageUrl: String? = null
)


data class GenInfo(
    val culture: LocalizedText = LocalizedText(),
    val description: LocalizedText = LocalizedText(),
    val food: LocalizedText = LocalizedText(),
    val history: LocalizedText = LocalizedText(),
    val lat: Double = 0.0,
    val long: Double = 0.0
)

data class Health(
    val emergency: Emergency = Emergency(),
    val tips: LocalizedText = LocalizedText(),
    val vaccines: List<String> = emptyList()
)

data class Emergency(
    val ambulance: Long = 0,
    val poisonControl: Long = 0
)

data class Security(
    val commonScams: List<String> = emptyList(),
    val crimeLevel: LocalizedText = LocalizedText(),
    val emergencyContacts: EmergencyContacts = EmergencyContacts()
)

data class EmergencyContacts(
    val police: Long = 0,
    val embassy: String = ""
)

data class Transport(
    val public: LocalizedText = LocalizedText(),
    val apps: LocalizedText = LocalizedText(),
    val airportToCity: LocalizedText = LocalizedText()
)

data class Visa(
    val required: Boolean = false,
    val duration: LocalizedText = LocalizedText(),
    val embassy: LocalizedText = LocalizedText()
)

data class LocalizedText(
    val en: String = "",
    val es: String = ""
)
