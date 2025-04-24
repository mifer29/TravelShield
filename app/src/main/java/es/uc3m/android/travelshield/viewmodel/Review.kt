package es.uc3m.android.travelshield.viewmodel

data class Review(
    val reviewId: String = "",
    val userId: String = "",
    val userName: String = "",  // Add this line for the user's name
    val country: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val timestamp: String = ""
)
