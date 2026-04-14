package com.example.cleancityapp.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cleancityapp.data.remote.CityDto
import com.example.cleancityapp.presentation.components.InputField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUp: (String, String, String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onFetchCities: () -> Unit,
    cities: List<CityDto>,
    isLoading: Boolean = false,
    error: String? = null
) {
    var fullName by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var city by remember { mutableStateOf("") }
    var citySearch by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // ✅ API trigger
    LaunchedEffect(expanded) {
        if (expanded && cities.isEmpty()) {
            onFetchCities()
        }
    }

    val filteredCities = remember(citySearch, cities) {
        if (citySearch.isEmpty()) cities
        else cities.filter { it.name.contains(citySearch, true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Clean City", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
        Text("Create your account", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        error?.let {
            Text(it, color = Color.Red, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        InputField(
            value = fullName,
            onValueChange = { fullName = it },
            label = "Full Name",
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(Modifier.height(16.dp))

        InputField(
            value = mobile,
            onValueChange = { mobile = it },
            label = "Mobile Number",
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(Modifier.height(16.dp))

        // ✅ Dropdown with InputField
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {

            InputField(
                value = citySearch,
                onValueChange = {
                    citySearch = it
                    expanded = true
                },
                label = "Select City",
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true),
                enabled = !isLoading,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                filteredCities.forEach { cityDto ->
                    DropdownMenuItem(
                        text = { Text(cityDto.name) },
                        onClick = {
                            city = cityDto.name
                            citySearch = cityDto.name
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        InputField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(Modifier.height(16.dp))

        InputField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            enabled = !isLoading
        )

        Spacer(Modifier.height(16.dp))

        InputField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            enabled = !isLoading
        )

        Spacer(Modifier.height(32.dp))

        val isValid = fullName.isNotBlank() &&
                mobile.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                city.isNotBlank() &&
                password == confirmPassword

        Button(
            onClick = { onSignUp(fullName, mobile, email, password, city) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading && isValid,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Sign Up", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(16.dp))

        Row {
            Text("Already have an account? ", color = Color.Gray)
            TextButton(onClick = onNavigateToLogin) {
                Text("Login", color = Color(0xFF1565C0))
            }
        }
    }
}