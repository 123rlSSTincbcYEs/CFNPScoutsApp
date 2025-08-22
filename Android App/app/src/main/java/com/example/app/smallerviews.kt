package com.example.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun WaitingScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colourBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = colourButton,
                strokeWidth = 4.dp,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Waiting for an Admin to approve your account.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = colourSecondaryText,
                modifier = Modifier.padding(horizontal = 32.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Please contact an Admin to approve your account.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = colourSecondaryText,
                modifier = Modifier.padding(horizontal = 32.dp),
                textAlign = TextAlign.Center
            )

            FilledTonalButton(
                shape = RoundedCornerShape(30),
                onClick = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                    auth.signOut()
                },
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
                Text("Back", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}