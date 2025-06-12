package com.example.app

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardTApp(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(colourBackground),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dashboard", fontSize = 64.sp)
        Row(

        ) {
            var items by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                getItemsFromFirestore(
                    collectionName = "items",
                    onSuccess = { fetchedItems ->
                        items = fetchedItems
                    },
                    onFailure = { e ->
                        errorMessage = e.message
                    }
                )
            }

            if (errorMessage != null) {
                Text(text = "Error: $errorMessage")
            } else {
                Column(
                    modifier = Modifier
                        .background(colourSecondary, shape = RoundedCornerShape(12.dp))
                        .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                ) {
                    for (item in items) {
                        val name = item["Name"] as? String
                        val description = item["Description"] as? String
                        val quantity = (item["Quantity"] as? Long)?.toInt()

                        val dueDate = item["Due Date"] as? com.google.firebase.Timestamp
                        val days = dueDate?.toDate()?.let { daysUntil(it) }

                        ItemUI(
                            name = name.orEmpty(),
                            description = description.orEmpty(),
                            quantity = quantity,
                            dd = days
                        )
                    }
                }
            }
        }
    }
}