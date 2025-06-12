package com.example.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

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
fun ItemUI(name: String, description: String, quantity: Int?) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(2.dp, Color.Red, RoundedCornerShape(12.dp))
            .background(Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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

                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Mark As Returned") },
                                leadingIcon = { Icon(Icons.Default.Check, contentDescription = "Mark As Returned") },
                                onClick = {expanded = false}
                            )
                            DropdownMenuItem(
                                text = { Text("Report Missing") },
                                leadingIcon = { Icon(Icons.Default.Warning, contentDescription = "Report Missing") },
                                onClick = {expanded = false}
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Edit") },
                                onClick = {expanded = false}
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("View Details") },
                                leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = "View Details") },
                                onClick = {expanded = false}
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        "Due in (<=5) Days",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
//                        Button(
//                            onClick = {},
//                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
//                            shape = RoundedCornerShape(8.dp)
//                        ) {
//                            Text("Mark As Returned", color = Color.White, fontSize = 12.sp)
//                        }
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Button(
//                            onClick = {},
//                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
//                            shape = RoundedCornerShape(8.dp)
//                        ) {
//                            Text("Report Missing", color = Color.White, fontSize = 12.sp)
//                        }

                }
            }
        }
    }
}