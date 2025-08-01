package com.example.app

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.io.File
import com.yalantis.ucrop.UCrop

@Composable
fun ItemUI(name: String, description: String, quantity: Int?, dd: Long?,id: String?, imageBase64: String?, fullItem: Map<String, Any>?, navController: NavController) {
    var colourScheme by remember { mutableStateOf(Color(0xFF306bb1)) }
    var bitmapImage by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    if (imageBase64 != null) {
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

@Composable
fun NewItemUi(navController: NavController, edit: Boolean? = false) {
    var itemName by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var base64image by remember { mutableStateOf("") }
    var quantity by remember { mutableIntStateOf(0) }
    var normal by remember { mutableIntStateOf(0) }
    var damaged by remember { mutableIntStateOf(0) }
    var missing by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    imageUrl = ""

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

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(colourBackground),
    ) { paddingValues ->
        Column(
            modifier = Modifier
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
                            Log.d("NewItemUi", "ImageBase64: $base64image")
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
                                                    Log.d("NewItemUi", "Failed to update item: ${e.localizedMessage}")
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

                                saveToFirestore(imageBase64 = base64image)
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

@Composable
fun Notes(navController: NavController) {
    var content by remember { mutableStateOf("") }
    var saveStatus by remember { mutableStateOf("Saved") }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            val snapshot = db.collection("users").document(uid).get().await()
            if (snapshot.exists()) {
                content = snapshot.getString("content") ?: ""
            }
        }
        isInitialized = true
    }

    LaunchedEffect(content) {
        Log.d("Notes", "Content changed: $content")
        if (!isInitialized) return@LaunchedEffect
        saveStatus = "Not Saved"
        delay(1000)
        val uid = auth.currentUser?.uid
        Log.d("Notes", "UID: $uid")
        if (uid != null) {
            db.collection("users").document(uid)
                .update("content", content)
                .addOnSuccessListener { saveStatus = "Saved" }
                .addOnFailureListener { saveStatus = "Save Failed" }
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = colourBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Notes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.CenterHorizontally)
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Notes") },
                textStyle = TextStyle(fontSize = 14.sp),
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
                    .height(500.dp)
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
                    .align(Alignment.Start)
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun Settings(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = colourBackground
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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

            Spacer(modifier = Modifier.height(200.dp))

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

            Spacer(modifier = Modifier.height(10.dp))

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
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val currentDestination = navController
        .currentBackStackEntryAsState().value?.destination

    NavigationBar(
        containerColor = colourBackground,
        contentColor = colourSecondaryText
    ) {
        BottomNavItems.forEach { (label, icon, route) ->
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