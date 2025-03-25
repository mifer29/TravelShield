package es.uc3m.android.travelshield.viewmodel

import com.google.firebase.firestore.Exclude

data class CountryDoc(
    @get:Exclude var id: String? = null,
    val name: String = "",
    val vaccine: String = ""

)
