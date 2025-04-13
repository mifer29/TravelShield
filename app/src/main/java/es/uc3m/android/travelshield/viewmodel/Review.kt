package es.uc3m.android.travelshield.viewmodel

data class Review(
    val userId: String = "",
    val country: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val timestamp: String = ""
)
