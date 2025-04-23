package es.uc3m.android.travelshield.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import es.uc3m.android.travelshield.NavGraph
import es.uc3m.android.travelshield.R
import es.uc3m.android.travelshield.viewmodel.*
import java.io.ByteArrayOutputStream
import androidx.core.content.ContextCompat
import androidx.compose.ui.layout.ContentScale

@Composable
fun ProfileScreen(
    navController: NavController,
    userInfoViewModel: UserInfoRetrieval = viewModel(),
    likeCountViewModel: LikeCountViewModel = viewModel(),
    tripViewModel: TripViewModel = viewModel(),
    userReviewsViewModel: UserReviewsViewModel = viewModel()
) {
    var profileImage by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val userInfo by userInfoViewModel.userInfo.collectAsState()
    val likeCount by likeCountViewModel.likeCount.collectAsState()
    val reviews by userReviewsViewModel.reviews.collectAsState()

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        profileImage = handleCameraResult(result)
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        profileImage = handleGalleryResult(uri, context)
    }
    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) launchCamera(cameraLauncher)
    }

    // 🔁 Refrescar perfil al volver desde edit_profile
    LaunchedEffect(Unit) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            if (backStackEntry.destination.route == NavGraph.Profile.route) {
                userInfoViewModel::class.java.getDeclaredMethod("fetchUserInfo").apply {
                    isAccessible = true
                    invoke(userInfoViewModel)
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                navController.navigate(NavGraph.SettingsScreen.route)
            }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(120.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = Color.Transparent
            ) {
                when {
                    profileImage != null -> Image(
                        bitmap = profileImage!!.asImageBitmap(),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    !userInfo?.profileImageUrl.isNullOrEmpty() -> Image(
                        painter = rememberAsyncImagePainter(userInfo!!.profileImageUrl),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    else -> Image(
                        painter = painterResource(id = R.drawable.profile_default),
                        contentDescription = "Default Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Picture",
                modifier = Modifier
                    .size(26.dp)
                    .background(Color.White, CircleShape)
                    .padding(4.dp)
                    .clickable { showDialog = true }
                    .align(Alignment.TopEnd)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(modifier = Modifier.align(Alignment.CenterHorizontally), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = userInfo?.let { "${it.name} ${it.surname}" } ?: "Loading...", style = MaterialTheme.typography.headlineSmall)
            Text(text = "Location", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
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

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate("edit_profile") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit Profile Info")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
                launchSingleTop = true
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Log Out")
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