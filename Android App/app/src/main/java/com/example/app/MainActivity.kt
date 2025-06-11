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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.filledTonalButtonColors
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.OutlinedButtonDefaults
import com.example.app.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun App() {
    val colourButton = Color(0xFF2E8B57)
    val colourBackground = Color(0xFFF3F1ED)
    val colourSecondary = Color(0xFFD2B48C)
    val colourSecondaryText = Color(0xFF5D5D5D)

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isValidEmail = Patterns.EMAIL_ADDRESS.matcher(username).matches()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(colourBackground),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", fontSize = 32.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Email") }, // Let Compose handle font size animations
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
            label = { Text("Password", fontSize = 24.sp) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
                    .padding(8.dp),
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
                // Firebase login here
            },
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = colourButton,
                contentColor = colourBackground
            ),
            modifier = Modifier
                .padding(14.dp)
                .clip(RoundedCornerShape(2.dp)),
        ) {
            Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}