package com.example.app

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardTApp(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var items by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        getItemsFromFirestore(
            collectionName = "items",
            onSuccess = { fetchedItems ->
                items = fetchedItems
                loading = false
            },
            onFailure = { e ->
                errorMessage = e.message
                loading = false
            }
        )
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = colourBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(34.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Dashboard",
                    fontSize = 40.sp,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            when {
                errorMessage != null -> {
                    Text(text = "Error: $errorMessage")
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(colourSecondary, shape = RoundedCornerShape(12.dp))
                            .border(2.dp, Color.White, RoundedCornerShape(12.dp)),
                    ) {
                        if (loading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .zIndex(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color.White)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(items) { item ->
                                    val name = item["name"] as? String ?: ""
                                    val description = item["description"] as? String ?: ""
                                    val quantityMap = item["quantity"] as? Map<*, *>
                                    val normal = (quantityMap?.get("normal") as? Long)?.toInt() ?: 0
                                    val damaged = (quantityMap?.get("damaged") as? Long)?.toInt() ?: 0
                                    val missing = (quantityMap?.get("missing") as? Long)?.toInt() ?: 0
                                    val quantity = normal + damaged + missing
                                    val dueDate = item["Due Date"] as? com.google.firebase.Timestamp
                                    val days = dueDate?.toDate()?.let { daysUntil(it) }
                                    val id = item["id"] as? String ?: ""
                                    val imageBase64 = item["imageBase64"] as? String

                                    // Skip item if search query doesn't match
                                    if (searchQuery.isNotBlank() &&
                                        !name.lowercase().replace(" ", "")
                                            .contains(searchQuery.lowercase().replace(" ", ""))
                                    ) {
                                        return@items
                                    }

                                    ItemUI(
                                        name = name,
                                        description = description,
                                        quantity = quantity,
                                        dd = days,
                                        navController = navController,
                                        id = id,
                                        imageBase64 = imageBase64,
                                        fullItem = item,
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            refresh = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colourSecondary),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.White),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reload Items")
                        Text(text = "Reload", fontSize = 16.sp)
                    }

                    Button(
                        onClick = { navController.navigate("newItem/false") },
                        colors = ButtonDefaults.buttonColors(containerColor = colourSecondary),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.White),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item")
                        Text(text = "Add Item", fontSize = 16.sp)
                    }

                    LaunchedEffect(refresh) {
                        if (refresh) {
                            loading = true
                            kotlinx.coroutines.delay(500)
                            getItemsFromFirestore(
                                collectionName = "items",
                                onSuccess = { fetchedItems ->
                                    items = fetchedItems
                                    loading = false
                                    refresh = false
                                },
                                onFailure = { e ->
                                    errorMessage = e.message
                                    loading = false
                                    refresh = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
