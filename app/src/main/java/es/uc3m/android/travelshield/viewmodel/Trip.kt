package es.uc3m.android.travelshield.viewmodel

data class Trip(
    val country: String = "",
    val startDate: String = "",
    val userId: String = "", // You can also store the userId to link the trips with the user
    val timestamp: String = "" // Timestamp to order trips
)
