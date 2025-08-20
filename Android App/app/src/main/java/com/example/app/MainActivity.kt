package com.example.app

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavType
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.google.firebase.firestore.DocumentReference

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

lateinit var auth: FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RootApp() {
    val currentUser = Firebase.auth.currentUser
    val startDestination = remember(currentUser) {
        if (currentUser != null) "dashboard" else "login"
    }
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("dashboard") {
            DashboardTApp(navController)
        }
        composable("notes") { NotesListScreen(navController) }
        composable("noteEditor") { backStackEntry ->
            NoteEditorScreen(navController)
        }
        composable("settings") {
            Settings(navController)
        }
        composable("login") {
            LoginApp(navController)
        }
        composable("viewItem") {
            ViewItemUi(navController)
        }
        composable(
            "newItem/{edit}",
                arguments = listOf(
                    navArgument("edit") { type = NavType.StringType }
                )
        ){ backStackEntry ->
            val edit = backStackEntry.arguments?.getString("edit").toBoolean()
            NewItemUi(edit = edit, navController = navController)
        }
    }
}

@Composable
fun LoginApp(navController: NavController) {
    auth = Firebase.auth

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var popupMessage by remember { mutableStateOf<String?>(null) }
    var showPopup by remember { mutableStateOf(false) }

    val isValidEmail = Patterns.EMAIL_ADDRESS.matcher(username).matches()

    val loginWithPassword = {
        isLoading = true
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val userDocRef = db.collection("users").document(uid)

                    userDocRef.get()
                        .addOnSuccessListener { doc ->
                            if (!doc.exists()) {
                                userDocRef.set(mapOf("2faEnabled" to true))
                            } else if (doc.getBoolean("2faEnabled") == null) {
                                userDocRef.update("2faEnabled", true)
                            }
                            navController.navigate("dashboard")
                            isLoading = false
                        }
                        .addOnFailureListener { e ->
                            popupMessage = "Error: ${e.message}"
                            showPopup = true
                            isLoading = false
                        }
                } else {
                    popupMessage = task.exception?.message ?: "Login failed"
                    showPopup = true
                    isLoading = false
                }

                username = ""
                password = ""
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colourBackground),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.drawable.fearlessfalcons), contentDescription = "Logo")
            Spacer(Modifier.height(10.dp))

            Text("Login", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Email") },
                singleLine = true,
                isError = username.isNotEmpty() && !isValidEmail,
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
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (isValidEmail && password.isNotEmpty()) {
                            loginWithPassword()
                        } else {
                            popupMessage = "Invalid email or password"
                            showPopup = true
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
                    if (isValidEmail && password.isNotEmpty()) {
                        loginWithPassword()
                    } else {
                        popupMessage = "Invalid email or password"
                        showPopup = true
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

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colourBackground.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colourButton)
            }
        }

        if (showPopup && popupMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 64.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                PopupMessage(
                    message = popupMessage!!,
                    onDismiss = { showPopup = false }
                )
            }
        }
    }
}



