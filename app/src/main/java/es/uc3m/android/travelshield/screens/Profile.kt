package es.uc3m.android.travelshield.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import es.uc3m.android.travelshield.R
import es.uc3m.android.travelshield.viewmodel.LikeCountViewModel
import es.uc3m.android.travelshield.viewmodel.Review
import es.uc3m.android.travelshield.viewmodel.UserInfoRetrieval
import es.uc3m.android.travelshield.viewmodel.UserReviewsViewModel
import es.uc3m.android.travelshield.viewmodel.TripViewModel
import es.uc3m.android.travelshield.viewmodel.Trip
import androidx.compose.material.icons.filled.Settings
import es.uc3m.android.travelshield.NavGraph
import es.uc3m.android.travelshield.screens.SettingsScreen
import android.util.Log
import androidx.compose.ui.zIndex

@Composable
fun ProfileScreen(
    navController: NavController,
    userInfoViewModel: UserInfoRetrieval = viewModel(),
    likeCountViewModel: LikeCountViewModel = viewModel(),
    tripViewModel: TripViewModel = viewModel()
) {
    var profileImage by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val userInfo by userInfoViewModel.userInfo.collectAsState()
    val likeCount by likeCountViewModel.likeCount.collectAsState()
    val trips by tripViewModel.trips.collectAsState()

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        profileImage = handleCameraResult(result)
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        profileImage = handleGalleryResult(uri, context)
    }
    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) launchCamera(cameraLauncher)
    }

    val userReviewsViewModel: UserReviewsViewModel = viewModel()
    val reviews by userReviewsViewModel.reviews.collectAsState()

    var showAddTripDialog by remember { mutableStateOf(false) }
    var newTripCountry by remember { mutableStateOf(TextFieldValue("")) }
    var newTripDate by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {


        // BOTÓN DE AJUSTES ARRIBA A LA DERECHA
        IconButton(
            onClick = {
                Log.d("SettingsButton", "Clicked")
                navController.navigate(NavGraph.SettingsScreen.route)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .zIndex(1f) // Añadir esto
        )
        {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings"
            )
        }



        ScrollableColumn(modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                // Imagen de perfil + botón de editar
                Box(contentAlignment = Alignment.TopEnd) {
                    if (profileImage != null) {
                        Image(
                            bitmap = profileImage!!.asImageBitmap(),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(100.dp).clip(CircleShape).border(2.dp, Color.Gray, CircleShape)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.profile_default),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(100.dp).clip(CircleShape).border(2.dp, Color.Gray, CircleShape)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.White, CircleShape)
                            .padding(4.dp)
                            .clickable { showDialog = true }
                            .align(Alignment.TopEnd)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = userInfo?.let { "${it.name} ${it.surname}" } ?: "Loading...", style = MaterialTheme.typography.headlineSmall)
                Text(text = "Location", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStat("0", "Countries traveled")
                    ProfileStat("0", "Reviews written")
                    ProfileStat(likeCount.toString(), "Likes given")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("My Reviews", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    reviews.forEach { review ->
                        ReviewItem(review)
                        Divider()
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("My Trips", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            navController.navigate("trips")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Go to My Trips")
                    }
                }


                Button(
                    onClick = {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                ) {
                    Text("Log Out")
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select Profile Picture") },
            text = { Text("Choose an option") },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = {
                        showDialog = false
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            launchCamera(cameraLauncher)
                        } else {
                            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }) {
                        Text("Take photo")
                    }
                    Button(onClick = {
                        showDialog = false
                        galleryLauncher.launch("image/*")
                    }) {
                        Text("Choose from gallery")
                    }
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    if (showAddTripDialog) {
        AddTripDialog(
            onDismiss = { showAddTripDialog = false },
            onAddTrip = { country, startDate ->
                tripViewModel.addTrip(Trip(country = country, startDate = startDate))
                newTripCountry = TextFieldValue("")
                newTripDate = ""
                showAddTripDialog = false
            },
            country = newTripCountry,
            setCountry = { newTripCountry = it },
            startDate = newTripDate,
            setStartDate = { newTripDate = it }
        )
    }
}

@Composable
fun ScrollableColumn(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = modifier.verticalScroll(rememberScrollState()), content = content)
}

@Composable
fun ProfileTripItem(trip: Trip) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = "Trip to: ${trip.country}", style = MaterialTheme.typography.titleSmall)
        Text(text = "Start Date: ${trip.startDate}", style = MaterialTheme.typography.bodyMedium)
    }
}

fun launchCamera(cameraLauncher: androidx.activity.result.ActivityResultLauncher<Intent>) {
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    cameraLauncher.launch(cameraIntent)
}

fun handleCameraResult(result: androidx.activity.result.ActivityResult): Bitmap? {
    return if (result.resultCode == Activity.RESULT_OK) {
        result.data?.extras?.get("data") as? Bitmap
    } else {
        null
    }
}

fun handleGalleryResult(uri: Uri?, context: android.content.Context): Bitmap? {
    return uri?.let {
        MediaStore.Images.Media.getBitmap(context.contentResolver, it)
    }
}

@Composable
fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
fun ReviewItem(review: Review) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = review.country, style = MaterialTheme.typography.titleSmall)
        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(review.rating.toInt()) {
                Icon(Icons.Default.Star, contentDescription = "Star", tint = Color.LightGray)
            }
            if (review.rating - review.rating.toInt() >= 0.5) {
                Icon(Icons.Default.Star, contentDescription = "Half Star", tint = Color.Blue)
            }
        }
        Text(text = review.comment, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()
    ProfileScreen(navController = navController)
}
