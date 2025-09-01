package com.example.app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
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
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import android.util.Base64
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.io.File
import com.yalantis.ucrop.UCrop

@ExperimentalLayoutApi
@Composable
fun ItemUI(
    name: String,
    description: String,
    quantity: Int?,
    dd: Long?,
    id: String?,
    imageBase64: String?,
    fullItem: Map<String, Any>?,
    tags: List<String>?,
    admin: Boolean,
    navController: NavController
) {
    var colourScheme by remember { mutableStateOf(Color(0xFF306bb1)) }
    var bitmapImage by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    if (imageBase64 != null) {
        bitmapImage = base64ToBitmap(imageBase64)
    }

    colourScheme = when {
        dd == null -> Color(0xFF306bb1)
        dd <= 5 -> Color(0xFFe03024)
        else -> Color(0xFF2e8b57)
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
            modifier = Modifier.padding(12.dp)
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
                    Spacer(modifier = Modifier.height(8.dp))

                    if (!tags.isNullOrEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val maxTagsToShow = 1
                            val limitedTags = tags.take(maxTagsToShow)

                            limitedTags.forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFFD3F5A5),
                                            shape = RoundedCornerShape(50)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(tag, fontSize = 12.sp, color = colourButton)
                                }
                            }

                            if (tags.size > maxTagsToShow) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFFE0E0E0),
                                            shape = RoundedCornerShape(50)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("...", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
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
                            if (admin) {
                                DropdownMenuItem(
                                    text = { Text("Edit") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    },
                                    onClick = {
                                        expanded = false
                                        currentItem = fullItem
                                        navController.navigate("newItem/true")
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("View Details") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Info, contentDescription = "View Details")
                                },
                                onClick = {
                                    expanded = false
                                    currentItem = fullItem
                                    navController.navigate("viewItem")
                                }
                            )
                            if (admin) {
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    },
                                    onClick = {
                                        expanded = false
                                        db.collection("items").document(id.toString())
                                            .delete()
                                            .addOnSuccessListener {
                                                triggerSheetSync(scriptUrl)
                                                refresh = true
                                                Toast.makeText(
                                                    context,
                                                    "Delete successful",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(
                                                    context,
                                                    "Delete failed: ${it.localizedMessage}",
                                                    Toast.LENGTH_LONG
                                                ).show()
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
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagDialog(
    tags: List<String>,
    onTagsChange: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var newTag by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Manage Tags") },
        text = {
            Column {
                OutlinedTextField(
                    value = newTag,
                    onValueChange = { newTag = it },
                    label = { Text("Add Tag") },
                    trailingIcon = {
                        IconButton(onClick = {
                            val trimmedTag = newTag.trim()
                            if (trimmedTag.isNotBlank()) {
                                if (tags.any { it.equals(trimmedTag, ignoreCase = true) }) {
                                    Toast.makeText(context, "Tag already exists", Toast.LENGTH_SHORT).show()
                                } else {
                                    onTagsChange(tags + trimmedTag)
                                    newTag = ""
                                }
                            }
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Tag")
                        }
                    },
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tags.forEachIndexed { index, tag ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFD3F5A5), RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(tag, fontSize = 12.sp, color = Color.Black)
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable {
                                            onTagsChange(tags.toMutableList().also { it.removeAt(index) })
                                        }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewItemUi(navController: NavController, edit: Boolean? = false) {
    var itemName by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var base64image by remember { mutableStateOf("") }
    var quantity by remember { mutableIntStateOf(0) }
    var normal by remember { mutableIntStateOf(0) }
    var damaged by remember { mutableIntStateOf(0) }
    var missing by remember { mutableIntStateOf(0) }
    var tags by remember { mutableStateOf<List<String>>(emptyList()) }
    var showTagDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    imageUrl = ""
    UserTypeWatcher(navController, false, false)

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
            tags = (currentItem!!["tags"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(colourBackground),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .background(colourBackground, shape = RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFF2e8b57), RoundedCornerShape(16.dp))
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (edit == true) "Edit Item" else "Add New Item",
                    style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                    color = colourSecondaryText,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ImagePickerBox(base64image = base64image) { croppedBase64 ->
                    base64image = croppedBase64
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    Edit_Status(
                        normal = normal,
                        onNormalChange = { normal = it },
                        damaged = damaged,
                        onDamagedChange = { damaged = it },
                        missing = missing,
                        onMissingChange = { missing = it },
                        true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

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

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showTagDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2e8b57)),
                        shape = RoundedCornerShape(30),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Label, contentDescription = "Tags", tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Edit Tags (${tags.size})", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    if (tags.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            tags.forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFD3F5A5), RoundedCornerShape(50))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(tag, fontSize = 12.sp, color = Color.Black)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilledTonalButton(
                        onClick = {
                            navController.navigate("dashboardT")
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
                                        ImageBase64 = imageBase64 ?: currentItem?.get("ImageBase64") as? String,
                                        Tags = tags
                                    )

                                    if (edit == true) {
                                        val docId = currentItem?.get("id") as? String
                                        if (docId != null) {
                                            db.collection("items").document(docId)
                                                .set(item)
                                                .addOnSuccessListener {
                                                    triggerSheetSync(scriptUrl)
                                                    navController.navigate("dashboardT")
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(context, "Update failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                                }
                                        }
                                    } else {
                                        db.collection("items")
                                            .add(item)
                                            .addOnSuccessListener {
                                                triggerSheetSync(scriptUrl)
                                                navController.navigate("dashboardT")
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(context, "Failed to create item: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                            }
                                    }
                                    imageUrl = null
                                }
                                saveToFirestore(base64image)
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

    if (showTagDialog) {
        TagDialog(
            tags = tags,
            onTagsChange = { tags = it },
            onDismiss = { showTagDialog = false }
        )
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
fun ViewItemUi(navController: NavController) {
    var itemName by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var normal by remember { mutableIntStateOf(0) }
    var damaged by remember { mutableIntStateOf(0) }
    var missing by remember { mutableIntStateOf(0) }
    var quantity by remember { mutableIntStateOf(0) }
    var bitmapImage by remember { mutableStateOf<Bitmap?>(null) }
    var tags by remember { mutableStateOf<List<String>>(emptyList()) }
    UserTypeWatcher(navController, false, false)

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
            tags = (it["tags"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "View Item",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colourSecondaryText
            )

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

            if (tags.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val maxTagsToShow = 7
                    val limitedTags = tags.take(maxTagsToShow)

                    limitedTags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFD3F5A5),
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(tag, fontSize = 12.sp, color = colourButton)
                        }
                    }

                    if (tags.size > maxTagsToShow) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("...", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
}

@Composable
fun ImagePickerBox(
    base64image: String,
    onImageCropped: (String) -> Unit
) {
    val context = LocalContext.current

    var bitmapImage by remember(base64image) {
        mutableStateOf(base64ToBitmap(base64image))
    }

    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultUri = UCrop.getOutput(result.data ?: return@rememberLauncherForActivityResult)
        if (resultUri != null) {
            context.contentResolver.openInputStream(resultUri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                bitmap?.let {
                    val outputStream = ByteArrayOutputStream()
                    it.compress(Bitmap.CompressFormat.WEBP, 40, outputStream)
                    val bytes = outputStream.toByteArray()

                    if (bytes.size > 300 * 1024) {
                        Toast.makeText(context, "Image too large (>300KB). Please crop smaller.", Toast.LENGTH_SHORT).show()
                    } else {
                        bitmapImage = it
                        val base64String = Base64.encodeToString(bytes, Base64.NO_WRAP)
                        onImageCropped(base64String)
                    }
                }
            }
        }
    }

    val pickLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val destinationUri = Uri.fromFile(File(context.cacheDir, "ucrop_${System.currentTimeMillis()}.jpg"))
            val options = UCrop.Options().apply {
                setFreeStyleCropEnabled(false)
                setCompressionQuality(60)
                setToolbarTitle("Crop Image")
            }

            val uCrop = UCrop.of(it, destinationUri)
                .withOptions(options)
                .withAspectRatio(1f, 1f)

            cropLauncher.launch(uCrop.getIntent(context))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f / 1f)
            .clip(RoundedCornerShape(12.dp))
            .background(colourSecondary)
            .border(2.dp, colourSecondaryText, RoundedCornerShape(12.dp))
            .clickable { pickLauncher.launch("image/*") },
        contentAlignment = Alignment.Center
    ) {
        if (bitmapImage != null) {
            Image(
                bitmap = bitmapImage!!.asImageBitmap(),
                contentDescription = "Selected Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Icon(Icons.Default.Add, contentDescription = "Add Image", Modifier.size(40.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotesListScreen(navController: NavController) {
    val uid = auth.currentUser?.uid
    var notes by remember { mutableStateOf<Map<String, Note>>(emptyMap()) }
    var showRenameDialog by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    UserTypeWatcher(navController, false, false)

    LaunchedEffect(uid) {
        if (uid != null) {
            db.collection("users").document(uid).addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    notes = (snapshot.get("notes") as? Map<*, *>)?.mapNotNull { entry ->
                        val id = entry.key as? String ?: return@mapNotNull null
                        val map = entry.value as? Map<*, *> ?: return@mapNotNull null
                        val name = map["name"] as? String ?: "Untitled Note"
                        val content = map["content"] as? String ?: ""
                        id to Note(name, content)
                    }?.toMap() ?: emptyMap()
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (notes.size >= 20) {
                        return@FloatingActionButton
                    }
                    newName = ""
                    showAddDialog = true
                },
                containerColor = colourButton
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note", tint = Color.White)
            }
        },
        containerColor = colourBackground,
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Text(
                    "Your Notes ${notes.size}/20",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = colourSecondaryText,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            items(notes.toList()) { (noteId, note) ->
                var expanded by remember { mutableStateOf(false) }

                Card(
                    colors = CardDefaults.cardColors(containerColor = colourSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.currentBackStackEntry?.savedStateHandle?.set("noteId", noteId)
                            navController.navigate("noteEditor")
                        },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = note.name,
                            color = colourSecondaryText,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = colourSecondaryText)
                            }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(
                                    text = { Text("Rename") },
                                    onClick = {
                                        expanded = false
                                        showRenameDialog = noteId
                                        newName = note.name
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    onClick = {
                                        expanded = false
                                        if (uid != null) {
                                            db.collection("users").document(uid)
                                                .update("notes.$noteId", FieldValue.delete())
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

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val noteId = System.currentTimeMillis().toString()
                    val newNote = mapOf("name" to newName.ifBlank { "Untitled Note" }, "content" to "")
                    if (uid != null) {
                        db.collection("users").document(uid)
                            .update("notes.$noteId", newNote)
                    }
                    showAddDialog = false
                }) {
                    Text("Add", color = colourButton)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = colourSecondaryText)
                }
            },
            title = {
                Text("New Note", color = colourSecondaryText)
            },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Note name", color = colourSecondaryText) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colourSecondary,
                        unfocusedContainerColor = colourSecondary,
                        focusedTextColor = colourSecondaryText,
                        unfocusedTextColor = colourSecondaryText,
                        focusedBorderColor = colourSecondaryText,
                        unfocusedBorderColor = colourSecondaryText,
                        cursorColor = colourSecondaryText
                    )
                )
            },
            containerColor = colourBackground
        )
    }

    if (showRenameDialog != null) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = null },
            confirmButton = {
                TextButton(onClick = {
                    val id = showRenameDialog!!
                    if (uid != null) {
                        db.collection("users").document(uid)
                            .update("notes.$id.name", newName)
                    }
                    showRenameDialog = null
                }) {
                    Text("Save", color = colourButton)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = null }) {
                    Text("Cancel", color = colourSecondaryText)
                }
            },
            title = {
                Text("Rename Note", color = colourSecondaryText)
            },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("New name", color = colourSecondaryText) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colourSecondary,
                        unfocusedContainerColor = colourSecondary,
                        focusedTextColor = colourSecondaryText,
                        unfocusedTextColor = colourSecondaryText,
                        focusedBorderColor = colourSecondaryText,
                        unfocusedBorderColor = colourSecondaryText,
                        cursorColor = colourSecondaryText
                    )
                )
            },
            containerColor = colourBackground
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(navController: NavController) {
    val uid = auth.currentUser?.uid
    val noteId = navController.previousBackStackEntry?.savedStateHandle?.get<String>("noteId")
    var content by remember { mutableStateOf("") }
    var saveStatus by remember { mutableStateOf("Saved") }
    var isInitialized by remember { mutableStateOf(false) }
    UserTypeWatcher(navController, false, false)

    LaunchedEffect(noteId) {
        if (uid != null && noteId != null) {
            val snapshot = db.collection("users").document(uid).get().await()
            val notesMap = snapshot.get("notes") as? Map<*, *> ?: emptyMap<Any, Any>()
            val noteMap = notesMap[noteId] as? Map<*, *>
            content = noteMap?.get("content") as? String ?: ""
        }
        isInitialized = true
    }

    LaunchedEffect(content) {
        if (!isInitialized || noteId == null || uid == null) return@LaunchedEffect

        saveStatus = "Not Saved"
        delay(1000)

        db.collection("users").document(uid)
            .update("notes.$noteId.content", content)
            .addOnSuccessListener { saveStatus = "Saved" }
            .addOnFailureListener { saveStatus = "Save Failed" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Note") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colourSecondary)
            )
        },
        containerColor = colourBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Write your note here...") },
                textStyle = TextStyle(fontSize = 14.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colourSecondary,
                    unfocusedContainerColor = colourSecondary,
                    focusedTextColor = colourSecondaryText,
                    unfocusedTextColor = colourSecondaryText,
                    focusedBorderColor = colourSecondaryText,
                    unfocusedBorderColor = colourSecondaryText,
                    cursorColor = colourSecondaryText
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Text(
                text = saveStatus,
                fontSize = 12.sp,
                color = when (saveStatus) {
                    "Saved" -> colourButton
                    "Not Saved" -> colourSecondaryText
                    "Save Failed" -> Color.Red
                    else -> Color.Unspecified
                },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun Settings(navController: NavController) {
    var popupMessage by remember { mutableStateOf("") }
    UserTypeWatcher(navController, false, false)

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = colourBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                var name by remember { mutableStateOf("") }
                var saveStatus by remember { mutableStateOf("Saved") }

                LaunchedEffect(Unit) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        val snapshot = db.collection("users").document(uid).get().await()
                        if (snapshot.exists()) {
                            name = snapshot.getString("name") ?: ""
                        }
                    }
                }

                LaunchedEffect(name) {
                    saveStatus = "Not Saved"
                    delay(1000)
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        db.collection("users").document(uid)
                            .update("name", name)
                            .addOnSuccessListener { saveStatus = "Saved" }
                            .addOnFailureListener { saveStatus = "Save Failed" }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .align(Alignment.CenterHorizontally)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
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

                Text(
                    text = saveStatus,
                    fontSize = 12.sp,
                    color = when (saveStatus) {
                        "Saved" -> colourButton
                        "Not Saved" -> colourSecondaryText
                        "Save Failed" -> Color.Red
                        else -> Color.Unspecified
                    },
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .align(alignment = Alignment.Start)
                )

                Spacer(modifier = Modifier.height(150.dp))

                Text(
                    text = "Contact Us/Tech Support",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "codefornonprofits@gmail.com",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Acknowledgements",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "This App Is Made By CFNP(Code For Non Profits) In Partnership With SST Scouts",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val email = auth.currentUser?.email
                        if (email != null) {
                            auth.sendPasswordResetEmail(email)
                                .addOnSuccessListener {
                                    popupMessage = "Password reset link sent to $email"
                                    Log.d("PasswordReset", "Password reset email sent to $email")
                                }
                                .addOnFailureListener {
                                    popupMessage = "Failed to send reset email"
                                }
                        } else {
                            popupMessage = "No email found for this account"
                        }
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = colourButton,
                        contentColor = colourBackground
                    ),
                    modifier = Modifier
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(30),
                ) {
                    Icon(Icons.Default.LockReset, contentDescription = "Reset Password")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset Password")
                }

                Button(
                    onClick = {
                        auth.signOut()
                        navController.navigate("login") {
                            popUpTo("settings") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = colourError,
                        contentColor = colourBackground
                    ),
                    modifier = Modifier
                        .padding(14.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(30),
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    Text("Logout")
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                PopupMessage(
                    message = popupMessage,
                    onDismiss = { popupMessage = "" },
                )
            }
        }
    }
}

@Composable
fun PopupMessage(
    message: String,
    onDismiss: () -> Unit,
    backgroundColor: Color = colourButton,
    textColor: Color = colourBackground,
    durationMillis: Long = 3000L
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(message) {
        if (message.isNotEmpty()) {
            visible = true
            delay(durationMillis)
            visible = false
            delay(300)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(animationSpec = tween(durationMillis = 300))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = backgroundColor,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Info",
                        tint = textColor,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = message,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { visible = false; onDismiss() }) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = textColor)
                    }
                }
            }
        }
    }
}
