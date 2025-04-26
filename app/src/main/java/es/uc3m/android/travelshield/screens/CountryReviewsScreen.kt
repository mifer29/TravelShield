package es.uc3m.android.travelshield.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import es.uc3m.android.travelshield.NavGraph
import es.uc3m.android.travelshield.viewmodel.CountryReviewsViewModel
import es.uc3m.android.travelshield.viewmodel.Review
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CountryReviewsScreen(navController: NavController, countryName: String) {
    val countryReviewsViewModel: CountryReviewsViewModel = viewModel()
    val countryReviews by countryReviewsViewModel.reviews.collectAsState()

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    val userReview = countryReviews.find { it.userId == currentUserId }
    val otherReviews by remember(countryReviews) {
        mutableStateOf(countryReviews.filter { it.userId != currentUserId })
    }

    var ratingFilter by remember { mutableStateOf(0) }
    var sortDescending by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        countryReviewsViewModel.fetchReviewsByCountry(countryName)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Reviews for $countryName",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- User Review ---
        userReview?.let {
            Text(
                text = "Tu Review",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ReviewItemCountry(
                review = it,
                isUserReview = true,
                onEditClick = {
                    navController.navigate(NavGraph.WriteReview.createRoute(countryName))
                },
                onDeleteClick = {
                    it.reviewId?.let { id -> countryReviewsViewModel.deleteReview(id) }
                }
            )
        } ?: AddReviewPrompt(countryName = countryName, navController = navController)

        Spacer(modifier = Modifier.height(16.dp))

        // --- Filters and Sort ---
        Text(
            text = "Otras Reviews",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        FiltersSection(ratingFilter, onRatingChange = { ratingFilter = it }, sortDescending, onSortToggle = { sortDescending = !sortDescending })

        // --- Other Reviews ---
        val filteredReviews = otherReviews
            .filter { it.rating.toInt() >= ratingFilter }
            .sortedBy { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).parse(it.timestamp)?.time }
            .let { if (sortDescending) it.reversed() else it }

        if (filteredReviews.isNotEmpty()) {
            filteredReviews.forEach { review ->
                ReviewItemCountry(
                    review = review,
                    isUserReview = false,
                    onEditClick = {},
                    onDeleteClick = {}
                )
            }
        } else {
            Text(
                text = "No hay otras reviews disponibles.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun AddReviewPrompt(countryName: String, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Has viajado a $countryName?",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navController.navigate(NavGraph.WriteReview.createRoute(countryName)) }) {
                Text("Añadir tu opinión")
            }
        }
    }
}

@Composable
fun FiltersSection(
    ratingFilter: Int,
    onRatingChange: (Int) -> Unit,
    sortDescending: Boolean,
    onSortToggle: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Rating mínimo: ")
        Slider(
            value = ratingFilter.toFloat(),
            onValueChange = { onRatingChange(it.toInt()) },
            valueRange = 0f..5f,
            steps = 4,
            modifier = Modifier.weight(1f)
        )
        Text("$ratingFilter★")
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
        Text("Orden:")
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { onSortToggle() }) {
            Text(if (sortDescending) "Más reciente" else "Más antiguo")
        }
    }
}

@Composable
fun ReviewItemCountry(
    review: Review,
    isUserReview: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Eliminar Review") },
            text = { Text("¿Seguro que quieres eliminar tu review? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onDeleteClick()
                }) {
                    Text("Borrar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = if (isUserReview) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = review.userName + if (isUserReview) " (Tú)" else "",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                if (isUserReview) {
                    Row {
                        IconButton(onClick = { onEditClick() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar Review")
                        }
                        IconButton(onClick = { showDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Borrar Review", tint = Color.Red)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(review.rating.toInt()) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Yellow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = "Posted on ${review.timestamp}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
