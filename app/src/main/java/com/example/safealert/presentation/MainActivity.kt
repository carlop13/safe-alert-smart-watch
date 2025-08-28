package com.example.safealert.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si ya hay datos almacenados en SharedPreferences
        val sharedPreferences = getSharedPreferences("SafeAlertPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)
        val username = sharedPreferences.getString("username", null)
        val contactos = sharedPreferences.getStringSet("contactos", null)

        if (email != null && username != null && contactos != null) {
            val intent = Intent(this, AlertActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            setContent {
                MainScreen(onNavigateToAlert = {
                    val intent = Intent(this, AlertActivity::class.java)
                    startActivity(intent)
                    finish()
                })
            }
        }
    }
}

@Composable
fun MainScreen(onNavigateToAlert: () -> Unit) {

    var code by remember { mutableStateOf("") }
    val isCodeValid = code.length >= 1
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Código") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp)
        )

        Button(
            onClick = {
                val db = Firebase.firestore

                db.collection("users")
                    .whereEqualTo("codigo", code)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            val document = documents.first()
                            val email = document.getString("email")
                            val username = document.getString("username")

                            // Extraer números de contacto
                            val contactosList = document.get("contactos") as? List<Map<String, Any>>
                            val numerosContactos = contactosList?.map { it["number"] as? String }?.filterNotNull()?.toSet()

                            val sharedPreferences = context.getSharedPreferences("SafeAlertPrefs", Context.MODE_PRIVATE)
                            with(sharedPreferences.edit()) {
                                putString("email", email)
                                putString("username", username)
                                putStringSet("contactos", numerosContactos)
                                apply()
                            }

                            Toast.makeText(context, "Conectado", Toast.LENGTH_SHORT).show()
                            onNavigateToAlert()
                        } else {
                            Toast.makeText(context, "No se pudo conectar", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Error al conectar", Toast.LENGTH_SHORT).show()
                    }
            },
            enabled = isCodeValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text(text = "Conectar")
        }
    }
}
