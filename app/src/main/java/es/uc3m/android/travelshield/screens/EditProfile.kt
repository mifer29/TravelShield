package es.uc3m.android.travelshield.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.uc3m.android.travelshield.R
import es.uc3m.android.travelshield.viewmodel.UserInfoRetrieval

@Composable
fun EditProfileScreen(
    navController: NavController,
    userInfoViewModel: UserInfoRetrieval = viewModel()
) {
    val context = LocalContext.current
    val userInfoState = userInfoViewModel.userInfo.collectAsState()
    val userInfo = userInfoState.value

    var name by remember(userInfo) { mutableStateOf(TextFieldValue(userInfo?.name ?: "")) }
    var surname by remember(userInfo) { mutableStateOf(TextFieldValue(userInfo?.surname ?: "")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.edit_profile), style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.name_profile)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text(stringResource(R.string.surname_profile)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                val updatedName = if (name.text.isNotBlank()) name.text else userInfo?.name ?: ""
                val updatedSurname = if (surname.text.isNotBlank()) surname.text else userInfo?.surname ?: ""

                userInfoViewModel.updateUserInfo(updatedName, updatedSurname)
                Toast.makeText(context,
                    context.getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.save_changes))
        }

        TextButton(onClick = { navController.popBackStack() }) {
            Text(stringResource(R.string.cancel))
        }
    }
}