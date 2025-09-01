package com.example.app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

val colourButton = Color(0xFF2E8B57)
val colourBackground = Color(0xFFF3F1ED)
val colourSecondary = Color(0xFFD2B48C)
val colourSecondaryText = Color(0xFF5D5D5D)
val colourError = Color(0xFFC62828)
val db = Firebase.firestore
val scriptUrl = "https://script.google.com/macros/s/AKfycbzOY9i7u072jGU0H5ECJ9Nvaud1lnfZ-L1r2ex63PasYMm_ZLQhiFgtYXvRR7fEaS7Zmw/exec"
var currentItem: Map<String, Any>? = null
var imageUrl by mutableStateOf<String?>(null)
var refresh by mutableStateOf(false)

data class InventoryItem(
    val Name: String? = null,
    val Description: String? = null,
    val Quantity: QuantityStatus = QuantityStatus(),
    val ImageBase64: String? = null,
    val Tags: List<String>? = null,
)

data class QuantityStatus(
    val normal: Int = 0,
    val damaged: Int = 0,
    val missing: Int = 0,
    val total: Int = 0,
)

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String,
    val adminRq: Boolean
)

data class Note(
    val name: String = "",
    val content: String = ""
)

val BottomNavItems = listOf(
    BottomNavItem(
        label = "Home",
        icon = Icons.Filled.Home,
        route = "dashboardN",
        adminRq = false
    ),
    BottomNavItem(
        label = "Notes",
        icon = Icons.Filled.Notes,
        route = "notes",
        adminRq = false
    ),
    BottomNavItem(
        label = "Manage",
        icon = Icons.Filled.Build,
        route = "management",
        adminRq = true
    ),
    BottomNavItem(
        label = "Settings",
        icon = Icons.Filled.Settings,
        route = "settings",
        adminRq = false
    )
)

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

fun getUsersFromFirestore(
    collectionName: String,
    onSuccess: (List<Map<String, Any>>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    db.collection("users")
        .get()
        .addOnSuccessListener { result ->
            val userList = result.documents.mapNotNull { document ->
                val data = document.data ?: emptyMap()
                data + ("id" to document.id)
            }
            onSuccess(userList)
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
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

fun base64ToBitmap(base64Str: String): Bitmap? {
    return try {
        val imageBytes = Base64.decode(base64Str, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null
    }.also {
        Log.d("base64ToBitmap", "decodedImage: $it")
    }
}