package es.uc3m.android.travelshield.viewmodel


// Review data model

data class Review(
    val reviewId: String = "",
    val userId: String = "",
    val userName: String = "",
    val country: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val timestamp: String = ""
)
