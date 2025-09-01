package com.example.app

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardTApp(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var filterOption by remember { mutableStateOf("Both") }
    var items by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var refresh by remember { mutableStateOf(false) }

    val filterOptions = listOf("Both", "Name", "Tag")

    UserTypeWatcher(navController, false, false)

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
                    text = "Manage Items",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Box {
                    Button(
                        onClick = { dropdownExpanded = true },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .width(70.dp)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colourSecondary.copy(alpha = 0.85f),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            filterOption,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Filter Dropdown",
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier
                            .shadow(8.dp, RoundedCornerShape(12.dp))
                            .background(Color.White, RoundedCornerShape(12.dp))
                    ) {
                        filterOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        option,
                                        fontSize = 14.sp,
                                        color = if (option == filterOption) colourSecondary else Color.Black,
                                        fontWeight = if (option == filterOption) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    filterOption = option
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            when {
                errorMessage != null -> {
                    Text(text = "Error: $errorMessage")
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(colourSecondary, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            .border(2.dp, Color.White, androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
                    ) {
                        if (loading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
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
                                    val quantityMap = item["quantity"] as? Map<*, *>
                                    val normal = (quantityMap?.get("normal") as? Long)?.toInt() ?: 0
                                    val damaged = (quantityMap?.get("damaged") as? Long)?.toInt() ?: 0
                                    val missing = (quantityMap?.get("missing") as? Long)?.toInt() ?: 0
                                    val quantity = normal + damaged + missing
                                    val dueDate = item["Due Date"] as? com.google.firebase.Timestamp
                                    val days = dueDate?.toDate()?.let { daysUntil(it) }
                                    val id = item["id"] as? String ?: ""
                                    val imageBase64 = item["imageBase64"] as? String
                                    val tags = item["tags"] as? List<String>

                                    val normalizedQuery = searchQuery.lowercase().replace(" ", "")

                                    if (searchQuery.isNotBlank()) {
                                        val nameMatches = name.lowercase().replace(" ", "").contains(normalizedQuery)
                                        val tagsMatches = tags?.any { it.lowercase().replace(" ", "").contains(normalizedQuery) } == true

                                        val matches = when (filterOption) {
                                            "Name" -> nameMatches
                                            "Tag" -> tagsMatches
                                            else -> nameMatches || tagsMatches
                                        }

                                        if (!matches) return@items
                                    }

                                    ItemUI(
                                        name = name,
                                        description = item["description"] as? String ?: "",
                                        quantity = quantity,
                                        dd = days,
                                        navController = navController,
                                        id = id,
                                        imageBase64 = imageBase64,
                                        fullItem = item,
                                        admin = true,
                                        tags = tags
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
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.White),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reload Items")
                        Text(text = "Reload", fontSize = 16.sp)
                    }

                    Button(
                        onClick = { navController.navigate("newItem/false") },
                        colors = ButtonDefaults.buttonColors(containerColor = colourSecondary),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.White),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item")
                        Text(text = "Add Item", fontSize = 16.sp)
                    }

                    LaunchedEffect(refresh) {
                        if (refresh) {
                            loading = true
                            delay(500)
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

@OptIn(ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardNApp(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var filterOption by remember { mutableStateOf("Both") }
    var items by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var refresh by remember { mutableStateOf(false) }

    val filterOptions = listOf("Both", "Name", "Tag")

    UserTypeWatcher(navController, false, false)

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
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Box {
                    Button(
                        onClick = { dropdownExpanded = true },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .width(70.dp)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colourSecondary.copy(alpha = 0.85f),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            filterOption,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Filter Dropdown",
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier
                            .shadow(8.dp, RoundedCornerShape(12.dp))
                            .background(Color.White, RoundedCornerShape(12.dp))
                    ) {
                        filterOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        option,
                                        fontSize = 14.sp,
                                        color = if (option == filterOption) colourSecondary else Color.Black,
                                        fontWeight = if (option == filterOption) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    filterOption = option
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            when {
                errorMessage != null -> {
                    Text(text = "Error: $errorMessage")
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(colourSecondary, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            .border(2.dp, Color.White, androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
                    ) {
                        if (loading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
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
                                    val quantityMap = item["quantity"] as? Map<*, *>
                                    val normal = (quantityMap?.get("normal") as? Long)?.toInt() ?: 0
                                    val damaged = (quantityMap?.get("damaged") as? Long)?.toInt() ?: 0
                                    val missing = (quantityMap?.get("missing") as? Long)?.toInt() ?: 0
                                    val quantity = normal + damaged + missing
                                    val dueDate = item["Due Date"] as? com.google.firebase.Timestamp
                                    val days = dueDate?.toDate()?.let { daysUntil(it) }
                                    val id = item["id"] as? String ?: ""
                                    val imageBase64 = item["imageBase64"] as? String
                                    val tags = item["tags"] as? List<String>

                                    val normalizedQuery = searchQuery.lowercase().replace(" ", "")

                                    if (searchQuery.isNotBlank()) {
                                        val nameMatches = name.lowercase().replace(" ", "").contains(normalizedQuery)
                                        val tagsMatches = tags?.any { it.lowercase().replace(" ", "").contains(normalizedQuery) } == true

                                        val matches = when (filterOption) {
                                            "Name" -> nameMatches
                                            "Tag" -> tagsMatches
                                            else -> nameMatches || tagsMatches
                                        }

                                        if (!matches) return@items
                                    }

                                    ItemUI(
                                        name = name,
                                        description = item["description"] as? String ?: "",
                                        quantity = quantity,
                                        dd = days,
                                        navController = navController,
                                        id = id,
                                        imageBase64 = imageBase64,
                                        fullItem = item,
                                        admin = false,
                                        tags = tags
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
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.White),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reload Items")
                        Text(text = "Reload", fontSize = 16.sp)
                    }

                    LaunchedEffect(refresh) {
                        if (refresh) {
                            loading = true
                            delay(500)
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