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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import es.uc3m.android.travelshield.NavGraph
import es.uc3m.android.travelshield.R
import es.uc3m.android.travelshield.viewmodel.*
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ProfileScreen(
    navController: NavController,
    userInfoViewModel: UserInfoRetrieval = viewModel(),
    likeCountViewModel: LikeCountViewModel = viewModel(),
    tripViewModel: TripViewModel = viewModel(),
    userReviewsViewModel: UserReviewsViewModel = viewModel()
) {
    var profileImage by remember { mutableStateOf<Bitmap?>(null) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(profileImage) {
        if (profileImage != null) {
            userInfoViewModel.uploadProfileImageAndSaveUrl(profileImage!!)
        }
    }

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val userInfo by userInfoViewModel.userInfo.collectAsState()
    val likeCount by likeCountViewModel.likeCount.collectAsState()
    val reviews by userReviewsViewModel.reviews.collectAsState()
    val reviewCount by userReviewsViewModel.reviewCount.collectAsState()
    val trips by tripViewModel.trips.collectAsState()
    var selectedReviewForEdit by remember { mutableStateOf<Review?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        profileImage = handleCameraResult(result)
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        profileImage = handleGalleryResult(uri, context)
    }
    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) launchCamera(cameraLauncher)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = { navController.navigate(NavGraph.SettingsScreen.route) }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
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
                    contentDescription = stringResource(R.string.edit_picture),
                    modifier = Modifier
                        .size(26.dp)
                        .background(Color.White, CircleShape)
                        .padding(4.dp)
                        .clickable { showDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = userInfo?.let { "${it.name} ${it.surname}" } ?: "Loading...",
                style = MaterialTheme.typography.headlineSmall
            )
            if (!userInfo?.location.isNullOrBlank()) {
                Text(
                    text = userInfo!!.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            } else {
                Text(
                    text = stringResource(R.string.no_location),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            ProfileStat(reviewCount.toString(), stringResource(R.string.countries_traveled))
            ProfileStat(trips.size.toString(), stringResource(R.string.future_travels))
            ProfileStat(likeCount.toString(), stringResource(R.string.likes_given))
        }

        Spacer(modifier = Modifier.height(24.dp))
        ProfileCategoryGrid(navController = navController)

        Spacer(modifier = Modifier.height(24.dp))
        Column {
            Text(stringResource(R.string.my_reviews), style = MaterialTheme.typography.titleMedium)
            if (reviews.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_reviews_yet),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                reviews.forEach { review ->
                    ReviewItem(
                        review = review,
                        onDeleteClick = { userReviewsViewModel.deleteReview(it) },
                        onEditClick = { selectedReviewForEdit = it }
                    )
                    Divider()
                }
            }
        }

        selectedReviewForEdit?.let { review ->
            EditReviewDialog(
                review = review,
                onDismiss = { selectedReviewForEdit = null },
                onConfirm = { newComment, newRating ->
                    userReviewsViewModel.updateReview(review.reviewId, newComment, newRating.toDouble())
                    selectedReviewForEdit = null
                }
            )
        }

        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        if (userEmail == "admin@travelshield.com") {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { navController.navigate(NavGraph.UploadCountries.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.upload_countries))
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.select_profile_picture)) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
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
                        Text(stringResource(R.string.take_photo))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        showDialog = false
                        galleryLauncher.launch("image/*")
                    }) {
                        Text(stringResource(R.string.choose_from_gallery))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = { showDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
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
fun ReviewItem(review: Review, onDeleteClick: (String) -> Unit, onEditClick: (Review) -> Unit) {
    val formattedDate = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(review.timestamp))

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = review.country, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            Row {
                IconButton(onClick = { onEditClick(review) }) { Icon(Icons.Default.Edit, contentDescription = "Edit Review") }
                IconButton(onClick = { onDeleteClick(review.reviewId) }) { Icon(Icons.Default.Delete, contentDescription = "Delete Review") }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (review.rating > 0) {
                Row {
                    repeat(review.rating.toInt()) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                    }
                }
            } else {
                Text(stringResource(R.string.no_rating))
            }
            Text(
                text = stringResource(R.string.posted_on, formattedDate),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = review.comment)
    }
}

@Composable
fun EditReviewDialog(review: Review, onDismiss: () -> Unit, onConfirm: (String, Float) -> Unit) {
    var updatedComment by remember { mutableStateOf(review.comment) }
    var updatedRating by remember { mutableFloatStateOf(review.rating.toFloat()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_review)) },
        text = {
            Column {
                OutlinedTextField(
                    value = updatedComment,
                    onValueChange = { updatedComment = it },
                    label = { Text(stringResource(R.string.comment)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.rating, updatedRating.toInt()))
                Slider(
                    value = updatedRating,
                    onValueChange = { updatedRating = it },
                    valueRange = 0f..5f,
                    steps = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(updatedComment, updatedRating) }) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

@Composable
fun ProfileCategoryGrid(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
    ) {
        CategoryButton(
            label = stringResource(R.string.search_users),
            icon = Icons.Default.Search,
            onClick = { navController.navigate("find_users") }
        )
        CategoryButton(
            label = stringResource(R.string.my_trips),
            icon = Icons.Default.Public,
            onClick = { navController.navigate("trips") }
        )
        CategoryButton(
            label = stringResource(R.string.edit_profile_info),
            icon = Icons.Default.Edit,
            onClick = { navController.navigate("edit_profile") }
        )
        CategoryButton(
            label = stringResource(R.string.log_out),
            icon = Icons.Default.ExitToApp,
            onClick = {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }
}

@Composable
fun CategoryButton(label: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.size(85.dp, 85.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.LightGray,
            contentColor = Color.Black
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

fun launchCamera(cameraLauncher: androidx.activity.result.ActivityResultLauncher<Intent>) {
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    cameraLauncher.launch(cameraIntent)
}

fun handleCameraResult(result: androidx.activity.result.ActivityResult): Bitmap? {
    return if (result.resultCode == Activity.RESULT_OK) {
        result.data?.extras?.get("data") as? Bitmap
    } else null
}

fun handleGalleryResult(uri: Uri?, context: android.content.Context): Bitmap? {
    return uri?.let {
        MediaStore.Images.Media.getBitmap(context.contentResolver, it)
    }
}
