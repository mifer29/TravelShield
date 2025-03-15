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
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import es.uc3m.android.travelshield.R

@Composable
fun ProfileScreen(navController: NavController) {
    var profileImage by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

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
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(100.dp).clip(CircleShape).border(2.dp, Color.Gray, CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.foto_perfil),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(100.dp).clip(CircleShape).border(2.dp, Color.Gray, CircleShape)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    modifier = Modifier.size(24.dp).background(Color.White, CircleShape).padding(4.dp).clickable {
                        showDialog = true
                    }
                )
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Seleccionar foto de perfil") },
                    text = { Text("Elige una opción") },
                    confirmButton = {
                        Column {
                            Button(onClick = {
                                showDialog = false
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    launchCamera(cameraLauncher)
                                } else {
                                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }) {
                                Text("Tomar foto")
                            }
                            Button(onClick = {
                                showDialog = false
                                galleryLauncher.launch("image/*")
                            }) {
                                Text("Elegir de galería")
                            }
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre y ubicación
            Text(text = "Nombre Apellido", style = MaterialTheme.typography.headlineSmall)
            Text(text = "Ubicación", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            // Estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat("X", "Countries travelled")
                ProfileStat("Y", "Reviews written")
                ProfileStat("Z", "Likes given")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón de Logout
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
