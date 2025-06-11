package com.example.app

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.filledTonalButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.tv.material3.OutlinedButtonDefaults
import com.example.app.ui.theme.AppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import androidx.navigation.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RootApp()
        }
    }
}

private lateinit var auth: FirebaseAuth

@Composable
fun RootApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("dashboard") {
            DashboardTApp(navController)
        }
        composable("login") {
            LoginApp(navController)
        }
    }
}

@Composable
fun LoginApp(navController: NavController) {
    auth = Firebase.auth

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val isValidEmail = Patterns.EMAIL_ADDRESS.matcher(username).matches()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(colourBackground),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Email") },
            singleLine = true,
            isError = username.isNotEmpty() && !isValidEmail,
            supportingText = {
                if (username.isNotEmpty() && !isValidEmail) {
                    Text("Invalid email address", color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier
                .padding(8.dp),
            textStyle = TextStyle(fontSize = 18.sp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colourSecondary,
                unfocusedContainerColor = colourSecondary,
                focusedTextColor = colourSecondaryText,
                unfocusedTextColor = colourSecondaryText,
                focusedBorderColor = colourSecondaryText,
                unfocusedBorderColor = colourSecondaryText,
                focusedLabelColor = colourSecondaryText,
                unfocusedLabelColor = colourSecondaryText,
                cursorColor = colourSecondaryText,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
                    .padding(8.dp),
            textStyle = TextStyle(fontSize = 18.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colourSecondary,
                unfocusedContainerColor = colourSecondary,
                focusedTextColor = colourSecondaryText,
                unfocusedTextColor = colourSecondaryText,
                focusedBorderColor = colourSecondaryText,
                unfocusedBorderColor = colourSecondaryText,
                focusedLabelColor = colourSecondaryText,
                unfocusedLabelColor = colourSecondaryText,
                cursorColor = colourSecondaryText
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        FilledTonalButton(
            onClick = {
                if (isValidEmail) {
                    isLoading = true
                    auth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT)
                                    .show()
                                navController.navigate("dashboard")
                            } else {
                                Toast.makeText(
                                    context,
                                    task.exception?.message ?: "Authentication failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            },
            enabled = !isLoading,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = colourButton,
                contentColor = colourBackground
            ),
            modifier = Modifier
                .padding(14.dp)
                .clip(RoundedCornerShape(2.dp))
                .fillMaxWidth()
                .height(56.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = colourBackground, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}