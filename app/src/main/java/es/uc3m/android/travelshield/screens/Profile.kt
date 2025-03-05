package es.uc3m.android.travelshield.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

import es.uc3m.android.travelshield.NavGraph

@Composable
fun ProfileScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto de perfil con icono de edición
            Box(contentAlignment = Alignment.TopEnd) {
                Image(
                    painter = painterResource(id = es.uc3m.android.travelshield.R.drawable.foto_perfil),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.White, CircleShape)
                        .padding(4.dp)
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
                    // Realizar el logout y navegar a la pantalla de login
                    navController.navigate("login") {
                        // Limpiar la pila de navegación para que no se pueda volver a la pantalla anterior
                        popUpTo(NavGraph.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            ) {
                Text("Log Out")
            }
        }
    }
}

@Composable
fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}
