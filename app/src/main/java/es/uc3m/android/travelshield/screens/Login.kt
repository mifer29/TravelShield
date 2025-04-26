package es.uc3m.android.travelshield.screens

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import es.uc3m.android.travelshield.NavGraph
import es.uc3m.android.travelshield.R
import es.uc3m.android.travelshield.viewmodel.AuthViewModel
import android.util.Log

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val route by authViewModel.route
    val toastMessage by authViewModel.toastMessage

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    Log.d("LoginScreen", "Google Sign-In Client ID: ${context.getString(R.string.default_web_client_id)}")

    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val TAG = "LoginScreen"

        if (result.resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Result OK from Google Sign-In")
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.result
                if (account != null) {
                    Log.d(TAG, "Google account selected: ${account.email}")
                    Toast.makeText(context, "Google account: ${account.email}", Toast.LENGTH_SHORT).show()
                    val idToken = account.idToken
                    if (idToken != null) {
                        Log.d(TAG, "ID Token obtained")
                        val credential = GoogleAuthProvider.getCredential(idToken, null)

                        FirebaseAuth.getInstance().signInWithCredential(credential)
                            .addOnCompleteListener { authTask ->
                                if (authTask.isSuccessful) {
                                    Log.d(TAG, "Firebase sign-in SUCCESS")
                                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                                    Log.d(TAG, "Firebase UserID: $userId")
                                    if (userId != null) {
                                        FirebaseFirestore.getInstance().collection("users").document(userId).get()
                                            .addOnSuccessListener { document ->
                                                if (document.exists()) {
                                                    Log.d(TAG, "User document exists in Firestore")
                                                    Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show()
                                                    navController.navigate(NavGraph.Home.route) {
                                                        popUpTo(NavGraph.Login.route) { inclusive = true }
                                                    }
                                                } else {
                                                    Log.d(TAG, "New user, needs to complete profile")
                                                    Toast.makeText(context, "Please complete your profile", Toast.LENGTH_SHORT).show()
                                                    navController.navigate(NavGraph.CompleteProfile.route) {
                                                        popUpTo(NavGraph.Login.route) { inclusive = true }
                                                    }
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e(TAG, "Error fetching user document: ${e.localizedMessage}")
                                                Toast.makeText(context, "Error checking profile", Toast.LENGTH_SHORT).show()
                                            }
                                    } else {
                                        Log.e(TAG, "Firebase currentUser is NULL")
                                        Toast.makeText(context, "Error: user not found", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Log.e(TAG, "Firebase sign-in FAILED: ${authTask.exception?.localizedMessage}")
                                    Toast.makeText(context, "Firebase sign-in failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Log.e(TAG, "ID Token is null")
                        Toast.makeText(context, "No ID token", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e(TAG, "Google account is null")
                    Toast.makeText(context, "Failed to get account", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during Google sign-in: ${e.localizedMessage}")
                Toast.makeText(context, "Sign-in error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d(TAG, "Google sign-in cancelled or failed with resultCode: ${result.resultCode}")
            Toast.makeText(context, "Google sign-in cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    route?.let { destination ->
        LaunchedEffect(destination) {
            navController.navigate(destination) {
                popUpTo(NavGraph.Login.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val logo: Painter = painterResource(id = R.drawable.logo_travelshield)
            Image(
                painter = logo,
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Login", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.please_log_in),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email_address)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { authViewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.log_in))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate(NavGraph.SignUp.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.sign_up))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("or", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val signInIntent: Intent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
            ) {
                Text("Sign in with Google", color = Color.White)
            }
        }
    }

    toastMessage?.let { message ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
