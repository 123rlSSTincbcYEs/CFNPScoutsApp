package com.example.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import android.util.Base64
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

val colourButton = Color(0xFF2E8B57)
val colourBackground = Color(0xFFF3F1ED)
val colourSecondary = Color(0xFFD2B48C)
val colourSecondaryText = Color(0xFF5D5D5D)
val db = Firebase.firestore
val scriptUrl = "https://script.google.com/macros/s/AKfycbzOY9i7u072jGU0H5ECJ9Nvaud1lnfZ-L1r2ex63PasYMm_ZLQhiFgtYXvRR7fEaS7Zmw/exec"
var currentItem: Map<String, Any>? = null
var imageUrl by mutableStateOf<String?>(null)
var refresh by mutableStateOf(false)

fun getItemsFromFirestore(
    collectionName: String,
    onSuccess: (List<Map<String, Any>>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    db.collection("items")
        .get()
        .addOnSuccessListener { result ->
            val itemsList = result.documents.mapNotNull { document ->
                val data = document.data ?: emptyMap()
                data + ("id" to document.id)
            }
            onSuccess(itemsList)
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}

@Composable
fun ItemUI(name: String, description: String, quantity: Int?, dd: Long?,id: String?, imageBase64: String?, fullItem: Map<String, Any>?, navController: NavController) {
    var colourScheme by remember { mutableStateOf(Color(0xFF306bb1)) }
    var bitmapImage by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    if (imageBase64 != null){
        bitmapImage = base64ToBitmap(imageBase64.toString())
    }
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
                    if (bitmapImage != null) {
                        Image(
                            painter = BitmapPainter(bitmapImage!!.asImageBitmap()),
                            contentDescription = "Decoded Base64 Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = "No Image",
                            tint = Color.Black.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp),
                        )
                    }
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
                                    currentItem = fullItem
                                    navController.navigate("newItem/true")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("View Details") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Info,
                                        contentDescription = "View Details"
                                    )
                                },
                                onClick = {
                                    expanded = false
                                    currentItem = fullItem
                                    navController.navigate("viewItem")
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete"
                                    )
                                },
                                onClick = {
                                    expanded = false
                                    db.collection("items").document(id.toString())
                                        .delete()
                                        .addOnSuccessListener {
                                            triggerSheetSync(scriptUrl)
                                            refresh = true
                                            Toast.makeText(context, "Delete successful", Toast.LENGTH_LONG).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Delete failed: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                                        }
                                }
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
fun NewItemUi(navController: NavController, edit: Boolean? = false) {
    var itemName by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var base64image by remember { mutableStateOf("") }
    var quantity by remember { mutableIntStateOf(0) }
    var normal by remember { mutableIntStateOf(0) }
    var damaged by remember { mutableIntStateOf(0) }
    var missing by remember { mutableIntStateOf(0) }
    var bitmapImage: Bitmap? = null
    val context = LocalContext.current

    LaunchedEffect(edit) {
        if (edit == true && currentItem != null) {
            itemName = currentItem!!["name"] as? String ?: ""
            itemDescription = currentItem!!["description"] as? String ?: ""
            base64image = currentItem!!["imageBase64"] as? String ?: ""
            val quantityMap = currentItem!!["quantity"] as? Map<*, *>
            normal = (quantityMap?.get("normal") as? Long)?.toInt() ?: 0
            damaged = (quantityMap?.get("damaged") as? Long)?.toInt() ?: 0
            missing = (quantityMap?.get("missing") as? Long)?.toInt() ?: 0
            quantity = normal + damaged + missing
        }
    }

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

            ImagePickerBox(base64image)

            Spacer(modifier = Modifier.height(16.dp))

            Column() {

                Edit_Status(normal = normal,
                    onNormalChange = { normal = it },
                    damaged = damaged,
                    onDamagedChange = { damaged = it },
                    missing = missing,
                    onMissingChange = { missing = it },
                    true)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(5f)
                    ) {
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            label = { Text("Name") },
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 14.sp),
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
                            textStyle = TextStyle(fontSize = 12.sp),
                            singleLine = false,
                            maxLines = 6,
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
                    }

//                    Column(
//                        modifier = Modifier
//                            .weight(2f)
//                            .border(2.dp, Color(0xFF2e8b57), RoundedCornerShape(16.dp))
//                            .padding(8.dp),
//                        horizontalAlignment = Alignment.Start
//                    ) {
//                        Text(
//                            "Status",
//                            color = colourSecondaryText,
//                            fontSize = 18.sp,
//                            fontWeight = FontWeight.Bold,
//                            textAlign = TextAlign.Center
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text("Total Qty: $quantity", color = colourSecondaryText, fontSize = 14.sp)
//                        Text("Normal: $normal", color = Color(0xFF2e8b57), fontSize = 13.sp)
//                        Text("Damaged: $damaged", color = Color(0xFFaf2522), fontSize = 13.sp)
//                        Text("Missing: $missing", color = Color(0xFFaf2522), fontSize = 13.sp)
//
//                        Spacer(modifier = Modifier.height(16.dp))
//
//                        FilledTonalButton(
//                            onClick = { TODO() },
//                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2e8b57)),
//                            shape = RoundedCornerShape(20),
//                        ) {
//                            Text("Edit", color = Color.White, fontSize = 12.sp)
//                        }
//                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilledTonalButton(
                    onClick = {
                        navController.navigate("dashboard")
                        imageUrl = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFaf2522)),
                    shape = RoundedCornerShape(30),
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
                        if (itemName.isEmpty() || itemDescription.isEmpty() || normal + damaged + missing == 0) {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_LONG).show()
                        } else {
                            val selectedImageUri: Uri? = imageUrl?.toUri()

                            val imageBase64: String? = selectedImageUri?.let { uri ->
                                try {
                                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                        val bitmap = BitmapFactory.decodeStream(inputStream)
                                        if (bitmap != null) {
                                            ByteArrayOutputStream().use { outputStream ->
                                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                                                val byteArray = outputStream.toByteArray()
                                                Base64.encodeToString(byteArray, Base64.NO_WRAP)
                                            }
                                        } else {
                                            null
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    null
                                }
                            }.toString()

                            fun saveToFirestore(imageBase64: String?) {
                                val item = InventoryItem(
                                    Name = itemName,
                                    Description = itemDescription,
                                    Quantity = QuantityStatus(
                                        normal = normal,
                                        damaged = damaged,
                                        missing = missing,
                                        total = normal + damaged + missing
                                    ),
                                    ImageBase64 = imageBase64 ?: currentItem?.get("ImageBase64") as? String
                                )

                                if (edit == true) {
                                    val docId = currentItem?.get("id") as? String
                                    if (docId != null) {
                                        db.collection("items").document(docId)
                                            .set(item)
                                            .addOnSuccessListener {
                                                triggerSheetSync(scriptUrl)
                                                navController.navigate("dashboard")
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(context, "Update failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                            }
                                    } else {
                                        Toast.makeText(context, "No document ID found", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    db.collection("items")
                                        .add(item)
                                        .addOnSuccessListener {
                                            triggerSheetSync(scriptUrl)
                                            navController.navigate("dashboard")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.d("NewItemUi", "Failed to create item: ${e.localizedMessage}")
                                            Toast.makeText(context, "Failed to create item: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                        }
                                }
                                imageUrl = null
                            }

                            saveToFirestore(imageBase64)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2e8b57)),
                    shape = RoundedCornerShape(30),
                    border = BorderStroke(2.dp, Color.White),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .height(48.dp)
                ) {
                    Text(if (edit == true) "Update" else "Create", color = Color.White, fontSize = 16.sp)
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

@Composable
fun Edit_Status(normal: Int, onNormalChange: (Int) -> Unit, damaged: Int, onDamagedChange: (Int) -> Unit, missing: Int, onMissingChange: (Int) -> Unit, opEn: Boolean) {
    val total = normal + damaged + missing

    if (opEn) {
        Box(
            modifier = Modifier
                .background(Color.White)
                .border(2.dp, colourSecondary, RoundedCornerShape(4.dp))
                .fillMaxWidth()
                .zIndex(1f),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text("Normal: ", fontSize = 12.sp)
                        TextField(
                            value = if (normal == 0) "" else normal.toString(),
                            onValueChange = { newText ->
                                val number = newText.toIntOrNull()
                                if (newText.isEmpty()) {
                                    onNormalChange(0)
                                } else if (number != null && number in 0..999) {
                                    onNormalChange(number)
                                }
                            },
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 12.sp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            modifier = Modifier
                                .width(52.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = colourBackground,
                                unfocusedContainerColor = colourBackground,
                            )
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text("Missing: ", fontSize = 12.sp)
                        TextField(
                            value = if (missing == 0) "" else missing.toString(),
                            onValueChange = { newText ->
                                val number = newText.toIntOrNull()
                                if (newText.isEmpty()) {
                                    onMissingChange(0)
                                } else if (number != null && number in 0..999) {
                                    onMissingChange(number)
                                }
                            },
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 12.sp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            modifier = Modifier
                                .width(52.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = colourBackground,
                                unfocusedContainerColor = colourBackground,
                            )
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text("Damaged: ", fontSize = 12.sp)
                        TextField(
                            value = if (damaged == 0) "" else damaged.toString(),
                            onValueChange = { newText ->
                                val number = newText.toIntOrNull()
                                if (newText.isEmpty()) {
                                    onDamagedChange(0)
                                } else if (number != null && number in 0..999) {
                                    onDamagedChange(number)
                                }
                            },
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 12.sp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            modifier = Modifier
                                .width(52.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = colourBackground,
                                unfocusedContainerColor = colourBackground,
                            )
                        )
                    }
                }
                Text("Total: $total", color = colourSecondaryText, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun EditUi(navController: NavController, item: Int) {

}

@Composable
fun ViewItemUi(navController: NavController) {
    var itemName by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var normal by remember { mutableIntStateOf(0) }
    var damaged by remember { mutableIntStateOf(0) }
    var missing by remember { mutableIntStateOf(0) }
    var quantity by remember { mutableIntStateOf(0) }
    var bitmapImage by remember { mutableStateOf<Bitmap?>(null) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        currentItem?.let {
            itemName = it["name"] as? String ?: ""
            itemDescription = it["description"] as? String ?: ""
            val quantityMap = it["quantity"] as? Map<*, *>
            normal = (quantityMap?.get("normal") as? Long)?.toInt() ?: 0
            damaged = (quantityMap?.get("damaged") as? Long)?.toInt() ?: 0
            missing = (quantityMap?.get("missing") as? Long)?.toInt() ?: 0
            quantity = normal + damaged + missing
            val imageBase64 = it["imageBase64"] as? String
            bitmapImage = base64ToBitmap(imageBase64.toString())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(colourBackground),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("View Item", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colourSecondaryText)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color(0xFFC3A480), RoundedCornerShape(12.dp))
                .border(1.dp, Color.Black, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (bitmapImage != null) {
                Image(
                    painter = BitmapPainter(bitmapImage!!.asImageBitmap()),
                    contentDescription = "Decoded Base64 Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = "No Image",
                    tint = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp),
                )
            }
        }

        OutlinedTextField(
            value = itemName,
            onValueChange = {},
            label = { Text("Item Name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            readOnly = true
        )

        OutlinedTextField(
            value = itemDescription,
            onValueChange = {},
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            enabled = false,
            readOnly = true,
            maxLines = 4
        )

        OutlinedTextField(
            value = "Normal: $normal | Damaged: $damaged | Missing: $missing | Total: $quantity",
            onValueChange = {},
            label = { Text("Quantity Overview") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            readOnly = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        FilledTonalButton(
            onClick = { navController.popBackStack() },
            shape = RoundedCornerShape(30),
            border = BorderStroke(2.dp, Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Back", color = Color.White, fontSize = 16.sp)
        }
    }
}

fun triggerSheetSync(scriptUrl: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = URL(scriptUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connect()
            val responseCode = conn.responseCode
            Log.d("SheetSync", "Triggered Google Apps Script, response code: $responseCode")
        } catch (e: Exception) {
            Log.e("SheetSync", "Failed to call Apps Script: ${e.localizedMessage}")
        }
    }
}

@Composable
fun ImagePickerBox(base64image: String) {
    val context = LocalContext.current

    val bitmapImage = remember(base64image) {
        if (imageUrl == null && base64image.isNotEmpty()) {
            base64ToBitmap(base64image)
        } else null
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUrl = uri?.toString()
        Log.d("ImagePickerBox", "Image selected: $imageUrl")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color(0xFFC3A480), RoundedCornerShape(12.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            .clickable { launcher.launch("image/*") },
        contentAlignment = Alignment.Center
    ) {
        LoadImage(bitmapImage)
    }
}

@Composable
fun LoadImage(bitmapImage: Bitmap? = null) {
    if (imageUrl != null) {
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = "Selected Image",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
    } else if (bitmapImage != null) {
        Image(
            painter = BitmapPainter(bitmapImage.asImageBitmap()),
            contentDescription = "Decoded Base64 Image",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
    } else {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Image",
            tint = Color.Black.copy(alpha = 0.5f),
            modifier = Modifier.size(48.dp)
        )
    }
}

fun base64ToBitmap(base64Str: String): Bitmap? {
    var decodedImage: Bitmap? = null
    try {
        val imageBytes = Base64.decode(base64Str, Base64.DEFAULT)
        decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null
    }
    Log.d("base64ToBitmap", "decodedImage: $decodedImage")
    return decodedImage
}

data class InventoryItem(
    val Name: String? = null,
    val Description: String? = null,
    val Quantity: QuantityStatus = QuantityStatus(),
    val ImageBase64: String? = null
)

data class QuantityStatus(
    val normal: Int = 0,
    val damaged: Int = 0,
    val missing: Int = 0,
    val total: Int = 0,
)
