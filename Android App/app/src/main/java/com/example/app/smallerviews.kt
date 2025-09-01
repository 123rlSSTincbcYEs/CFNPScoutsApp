package com.example.app

import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.delay

@Composable
fun WaitingScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colourBackground),
        contentAlignment = Alignment.Center
    ) {
        UserTypeWatcher(navController, false, true)
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

@Composable
fun BottomNavBar(navController: NavController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    var userType by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        userType = doc.getString("userType")
                    }
                }
        }
    }

    NavigationBar(
        containerColor = colourBackground,
        contentColor = colourSecondaryText
    ) {
        BottomNavItems.forEach { (label, icon, route, adminRq) ->
            if (!adminRq || userType == "admin") {
                val selected = currentDestination?.route == route

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            icon,
                            contentDescription = label,
                            tint = if (selected) colourButton else colourSecondaryText
                        )
                    },
                    label = {
                        Text(
                            label,
                            color = if (selected) colourButton else colourSecondaryText
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colourButton,
                        unselectedIconColor = colourSecondaryText,
                        selectedTextColor = colourButton,
                        unselectedTextColor = colourSecondaryText,
                        indicatorColor = colourSecondary.copy(alpha = 0.3f)
                    )
                )
            }
        }
    }
}

@Composable
fun ManagementScreen(navController: NavController) {
    Scaffold(
        containerColor = colourBackground,
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        UserTypeWatcher(navController, true, false)
        Column(
            modifier = Modifier
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Management", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    navController.navigate("userManagement")
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = colourButton,
                    contentColor = colourBackground
                ),
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(30),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Manage Users"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Manage Users")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    navController.navigate("dashboardT")
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = colourButton,
                    contentColor = colourBackground
                ),
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(30),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ManageSearch,
                        contentDescription = "Manage Items"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Manage Items")
                }
            }
        }
    }
}

@Composable
fun UserManagementScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var filterOption by remember { mutableStateOf("Both") }
    var users by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var refresh by remember { mutableStateOf(false) }
    val filterOptions = listOf("Both", "Name", "Email")

    UserTypeWatcher(navController, true, false)

    LaunchedEffect(Unit) {
        getUsersFromFirestore(
            collectionName = "users",
            onSuccess = { fetchedUsers ->
                users = fetchedUsers
                loading = false
                Log.d("sx", "$users")
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
        Log.d("UserManagementScreen", "Inner padding: $users")
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
                    text = "Manage Users",
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
                            .width(90.dp)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colourSecondary.copy(alpha = 0.85f),
                            contentColor = Color.White
                        )
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
                            .background(colourSecondary, RoundedCornerShape(12.dp))
                            .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                    ) {
                        if (loading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color.White)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(users) { user ->
                                    val name = user["name"] as? String ?: ""
                                    val email = user["email"] as? String ?: ""
                                    val userType = user["userType"] as? String ?: "unapproved"
                                    val id = user["id"] as? String ?: ""

                                    val normalizedQuery = searchQuery.lowercase().replace(" ", "")

                                    if (searchQuery.isNotBlank()) {
                                        val nameMatches = name.lowercase().replace(" ", "").contains(normalizedQuery)
                                        val emailMatches = email.lowercase().replace(" ", "").contains(normalizedQuery)

                                        val matches = when (filterOption) {
                                            "Name" -> nameMatches
                                            "Email" -> emailMatches
                                            else -> nameMatches || emailMatches
                                        }

                                        if (!matches) return@items
                                    }

                                    UserCardUI(
                                        name = name,
                                        email = email,
                                        userType = userType,
                                        id = id,
                                        navController = navController,
                                        fullUser = user,
                                        onChangeRole = { userId, type ->
                                            db.collection("users").document(userId)
                                                .update("userType", type)
                                                .addOnSuccessListener {
                                                    Log.d("UserManagement", "User role updated successfully")
                                                    refresh = true
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.e("UserManagement", "Error updating user role", e)
                                                }
                                        },
                                        onRemoveUser = { userId ->
                                            db.collection("users").document(userId)
                                                .delete()
                                                .addOnSuccessListener {
                                                    users = users.filterNot { it["id"] == userId }
                                                    Log.d("UserManagement", "User $userId deleted successfully")
                                                    refresh = true
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.e("UserManagement", "Error deleting user", e)
                                                    errorMessage = "Error deleting user: ${e.message}"
                                                }
                                        },
                                        onResetUser = { userId ->
                                            val email = auth.currentUser?.email
                                            if (email != null) {
                                                auth.sendPasswordResetEmail(email)
                                                    .addOnSuccessListener {
                                                        Log.d("PasswordReset", "Password reset email sent to $email")
                                                    }
                                                    .addOnFailureListener {
                                                    }
                                            } else {
                                                Log.d("PasswordReset", "No user logged in")                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { refresh = true },
                        colors = ButtonDefaults.buttonColors(containerColor = colourSecondary),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.White),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reload Users")
                        Text(text = "Reload", fontSize = 16.sp)
                    }

                    LaunchedEffect(refresh) {
                        if (refresh) {
                            loading = true
                            delay(500)
                            getUsersFromFirestore(
                                collectionName = "users",
                                onSuccess = { fetchedUsers ->
                                    users = fetchedUsers
                                    loading = false
                                    refresh = false
                                },
                                onFailure = { e ->
                                    errorMessage = e.message
                                    loading = false
                                    refresh = false
                                }
                            )
                            Log.d("taggger", "$users")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserCardUI(
    name: String,
    email: String,
    userType: String,
    id: String,
    navController: NavController,
    fullUser: Map<String, Any>,
    onChangeRole: (String, String) -> Unit,
    onRemoveUser: (String) -> Unit,
    onResetUser: (String) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showTypeDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember {mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete $name?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onRemoveUser(id)
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showTypeDialog) {
        AlertDialog(
            onDismissRequest = { showTypeDialog = false },
            title = { Text("Change User Type", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select a new role for $name:")

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(
                            onClick = {
                                onChangeRole(id, "unapproved")
                                showTypeDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Unapproved", color = colourSecondaryText)
                        }
                        TextButton(
                            onClick = {
                                onChangeRole(id, "normal")
                                showTypeDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Normal", color = colourSecondaryText)
                        }
                        TextButton(
                            onClick = {
                                onChangeRole(id, "admin")
                                showTypeDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Admin", color = colourSecondaryText)
                        }
                    }
                }
            },
            confirmButton = {
            },
            dismissButton = {
                TextButton(onClick = { showTypeDialog = false }) {
                    Text("Cancel", color = colourSecondaryText)
                }
            },
            containerColor = colourBackground
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Reset") },
            text = { Text("Are you sure you want to reset the password for $name?") },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    onResetUser(id)
                }) {
                    Text("Reset", color = colourSecondaryText)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(email, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(2.dp))
            Text("Role: $userType", fontSize = 14.sp, color = Color.DarkGray)

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More"
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Change Role") },
                            onClick = {
                                menuExpanded = false
                                showTypeDialog = true
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Upgrade, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Reset Password") },
                            onClick = {
                                menuExpanded = false
                                showResetDialog = true
                            },
                            leadingIcon = {
                                Icon(Icons.Default.LockReset, contentDescription = null)
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Remove User", color = Color.Red) },
                            onClick = {
                                menuExpanded = false
                                showDeleteDialog = true
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun UserTypeWatcher(navController: NavController, adminScreen: Boolean, waiting: Boolean) {
    val user = auth.currentUser

    DisposableEffect(user?.uid) {
        if (user == null) {
            onDispose { }
        } else {
            val userDocRef = db.collection("users").document(user.uid)
            val listener = userDocRef.addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener

                val userType = snapshot.getString("userType")
                Log.d("UserTypeWatcher", "User type: $userType")
                if (userType == "unapproved" && !waiting) {
                    navController.navigate("waiting") {
                        popUpTo(0)
                    }
                } else if (userType != "admin" && adminScreen) {
                    navController.navigate("dashboardN") {
                        popUpTo(0)
                    }
                } else if (waiting && userType != "unapproved") {
                    navController.navigate("dashboardN") {
                        popUpTo(0)
                    }
                }
            }

            onDispose {
                listener.remove()
            }
        }
    }
}