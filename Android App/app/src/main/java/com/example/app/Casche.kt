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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.tv.material3.Button
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

val colourButton = Color(0xFF2E8B57)
val colourBackground = Color(0xFFF3F1ED)
val colourSecondary = Color(0xFFD2B48C)
val colourSecondaryText = Color(0xFF5D5D5D)
val db = Firebase.firestore

fun getItemsFromFirestore(
    collectionName: String,
    onSuccess: (List<Map<String, Any>>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    db.collection("items")
        .get()
        .addOnSuccessListener { result ->
            val itemsList = result.documents.mapNotNull { it.data }
            onSuccess(itemsList)
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}

@Composable
fun ItemUI(name: String, description: String, quantity: Int?, dd: Long?, navController: NavController) {
    var colourScheme by remember { mutableStateOf(Color(0xFF306bb1)) }
    colourScheme = if (dd == null) {
        Color(0xFF306bb1)
    } else if (dd <= 5) {
        Color(0xFFe03024)
    } else {
        Color(0xFF2e8b57)
    }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(3.dp, colourScheme, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFFD2B48C), RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Image", color = Color.Black)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Qty: $quantity")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(description)
                }

                Column(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        var expanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit"
                                    )
                                },
                                onClick = {
                                    expanded = false
                                    navController.navigate("edit")
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("View Details") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Info,
                                        contentDescription = "View Details"
                                    )
                                },
                                onClick = { expanded = false }
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun daysUntil(targetDate: java.util.Date): Long {
    val localTargetDate = targetDate.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    val today = LocalDate.now()

    return ChronoUnit.DAYS.between(today, localTargetDate)
}

@Composable
fun EditUi(navController: NavController, id: String?) {

}

@Composable
fun NewItemUi(navController: NavController) {
    var itemName by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(colourBackground),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .background(colourBackground, shape = RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFF2e8b57), RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add New Item",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                color = colourSecondaryText,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFD2B48C), RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Black, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Image, contentDescription = "Image Placeholder", tint = Color.DarkGray, modifier = Modifier.size(64.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Name") },
                singleLine = true,
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
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = itemDescription,
                onValueChange = { itemDescription = it },
                label = { Text("Description") },
                textStyle = TextStyle(fontSize = 18.sp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
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
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilledTonalButton(
                    onClick = {
                        navController.navigate("dashboard")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFaf2522)),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(2.dp, Color.White),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .height(48.dp)
                ) {
                    Text("Cancel", color = Color.White, fontSize = 16.sp)
                }

                FilledTonalButton(
                    onClick = {
                        navController.navigate("dashboard")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2e8b57)),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(2.dp, Color.White),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .height(48.dp)
                ) {
                    Text("Create", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}


@Composable
fun LoadingOverlay(isLoading: Boolean) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .zIndex(1f),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 4.dp)
        }
    }
}

@Composable
fun ErrorPopup(message: String, authStat: Boolean, onDismiss: () -> Unit) {
    if (authStat) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .zIndex(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color.Red, modifier = Modifier.size(128.dp))
                Text("Error", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 32.sp)
                Text(message, color = Color.Red, fontSize = 20.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                FilledTonalButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFaf2522)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(2.dp, Color.White),
                    modifier = Modifier.width(100.dp)
                ) {
                    Text("Dismiss", color = Color.White)
                }
            }
        }
    }
}
