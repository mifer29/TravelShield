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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import es.uc3m.android.travelshield.R
import androidx.compose.ui.res.stringResource

@Composable
fun ProfileScreen(navController: NavController) {

    var profileImage by remember { mutableStateOf<Bitmap?>(null) }
    // Android context (permissions and gallery)
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // Launchers for camera and gallery
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        profileImage = handleCameraResult(result)
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        profileImage = handleGalleryResult(uri, context)
    }

    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            launchCamera(cameraLauncher)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                if (profileImage != null) {
                    Image(
                        bitmap = profileImage!!.asImageBitmap(),
                        contentDescription = stringResource(id = R.string.profile),
                        modifier = Modifier.size(100.dp).clip(CircleShape).border(2.dp, Color.Gray, CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.profile_default),
                        contentDescription = stringResource(id = R.string.profile),
                        modifier = Modifier.size(100.dp).clip(CircleShape).border(2.dp, Color.Gray, CircleShape)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(id = R.string.profile),
                    modifier = Modifier.size(24.dp).background(Color.White, CircleShape).padding(4.dp).clickable {
                        showDialog = true
                    }
                )
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(stringResource(id = R.string.profile)) },
                    text = { Text(stringResource(id = R.string.login_message)) },
                    confirmButton = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally // Alinea los botones en el centro
                        ) {
                            Button(onClick = {
                                showDialog = false
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    launchCamera(cameraLauncher)
                                } else {
                                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }) {
                                Text(stringResource(id = R.string.profile_stats_countries))
                            }
                            Button(onClick = {
                                showDialog = false
                                galleryLauncher.launch("image/*")
                            }) {
                                Text(stringResource(id = R.string.profile_stats_reviews))
                            }
                            Button(onClick = { showDialog = false }) {
                                Text(stringResource(id = R.string.log_out))
                            }
                        }
                    }
                )
            }


            Spacer(modifier = Modifier.height(8.dp))

            // Name and location
            Text(text = stringResource(id = R.string.provisional_profile_name), style = MaterialTheme.typography.headlineSmall)
            Text(text = stringResource(id = R.string.provisional_profile_location), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            // Statistics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat("0", stringResource(id = R.string.profile_stats_countries))
                ProfileStat("0", stringResource(id = R.string.profile_stats_reviews))
                ProfileStat("0", stringResource(id = R.string.profile_stats_likes))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Log out button
            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            ) {
                Text(stringResource(id = R.string.log_out))
            }
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

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()
    ProfileScreen(navController)
}
