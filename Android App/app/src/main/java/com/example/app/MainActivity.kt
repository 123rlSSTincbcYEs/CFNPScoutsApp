package com.example.app

import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import androidx.navigation.compose.*

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            RootApp()
        }
    }
}

private lateinit var auth: FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
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
        composable("edit") {
            EditUi(navController, null)
        }
        composable("newItem") {
            NewItemUi(navController)
        }
    }
}

@Composable
fun LoginApp(navController: NavController) {
    auth = Firebase.auth

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var authFailed by remember { mutableStateOf(false)}
    var message by remember { mutableStateOf<String?>(null) }

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
        LoadingOverlay(isLoading = isLoading)
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
                .padding(8.dp)
                .width(300.dp),
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
                .padding(8.dp)
                .width(300.dp),
            textStyle = TextStyle(fontSize = 18.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (isValidEmail and password.isNotEmpty()) {
                        isLoading = true
                        auth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener { task ->
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
                                    authFailed = true
                                    message = task.exception?.message
                                }
                                isLoading = false
                                username = ""
                                password = ""
                            }
                    } else {
                        Toast.makeText(context, "Invalid Email or No Password Entered", Toast.LENGTH_SHORT).show()
                    }
                }
            ),
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
            shape = RoundedCornerShape(30),
            onClick = {
                if (isValidEmail and password.isNotEmpty()) {
                    isLoading = true
                    auth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener { task ->
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
                                authFailed = true
                                message = task.exception?.message
                            }
                            isLoading = false
                            username = ""
                            password = ""
                        }
                } else {
                    Toast.makeText(context, "Invalid Email or No Password Entered", Toast.LENGTH_SHORT).show()
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
            Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}