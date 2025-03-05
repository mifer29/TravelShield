package es.uc3m.android.travelshield.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.uc3m.android.travelshield.R
import androidx.compose.foundation.Image

import es.uc3m.android.travelshield.NavGraph

@Composable
fun LoginScreen(navController: NavController) {
    var isLoggedIn by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Navegar a la pantalla de perfil cuando el usuario inicia sesión
    if (isLoggedIn) {
        LaunchedEffect(Unit) {
            navController.navigate(NavGraph.Profile.route) {
                // Limpiar la pila de navegación para que no se pueda volver a la pantalla de login
                popUpTo(NavGraph.Login.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            val logo: Painter = painterResource(id = R.drawable.logo_travelshield)
            Image(painter = logo, contentDescription = "App Logo", modifier = Modifier.size(100.dp))

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Login", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Please log in or sign up first to view your profile.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { isLoggedIn = true }, // Simula el login
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Log In")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
