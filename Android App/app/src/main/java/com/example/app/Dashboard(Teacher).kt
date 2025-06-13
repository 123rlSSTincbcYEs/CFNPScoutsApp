package com.example.app

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Mail
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
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
//                    Row() {
//                        var expanded by remember { mutableStateOf(false) }
//                        Column (horizontalAlignment = Alignment.CenterHorizontally) {
//                            IconButton(onClick = { expanded = true }) {
//                                Icon(Icons.Filled.FilterList, contentDescription = "Filters")
//                            }
//                        }
//
//                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
//                            DropdownMenuItem(
//                                text = { Text("All") },
//                                leadingIcon = { Icon(Icons.Default.Apps, contentDescription = "All") },
//                                trailingIcon = { Icon(Icons.Default.Check, contentDescription = "All") },
//                                onClick = {expanded = false}
//                            )
//                            HorizontalDivider()
//                            DropdownMenuItem(
//                                text = { Text("Due Soon") },
//                                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Due Soon") },
//                                onClick = {expanded = false}
//                            )
//                            DropdownMenuItem(
//                                text = { Text("Normal") },
//                                leadingIcon = { Icon(Icons.Default.FiberManualRecord, contentDescription = "Normal") },
//                                onClick = {
//                                    expanded = false
//                                }
//                            )
//                            DropdownMenuItem(
//                                text = { Text("Requests") },
//                                leadingIcon = { Icon(Icons.Default.Mail, contentDescription = "Requests") },
//                                onClick = {expanded = false}
//                            )
//                        }
//                    }

                    for (item in items) {
                        val name = item["Name"] as? String ?: ""
                        val description = item["Description"] as? String ?: ""
                        val quantity = (item["Quantity"] as? Long)?.toInt() ?: 0
                        val dueDate = item["Due Date"] as? com.google.firebase.Timestamp
                        val days = dueDate?.toDate()?.let { daysUntil(it) }

                        ItemUI(
                            name = name,
                            description = description,
                            quantity = quantity,
                            dd = days,
                            navController = navController
                        )
                    }
                    Row() {
                        Button(
                            onClick = {navController.navigate("newItem")},
                            colors = ButtonDefaults.buttonColors(containerColor = colourSecondary),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(2.dp, Color.White),
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Item")
                            Text(text = "Add Item", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}