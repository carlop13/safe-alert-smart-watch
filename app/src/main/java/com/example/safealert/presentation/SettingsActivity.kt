package com.example.safealert.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingsScreen(onDisconnect = {
                // Eliminar los datos almacenados
                val sharedPreferences = getSharedPreferences("SafeAlertPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.clear()

                val intent = Intent(this, MainActivity::class.java)

                if (editor.commit()) {
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al desconectar", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}

@Composable
fun SettingsScreen(onDisconnect: () -> Unit) {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("SafeAlertPrefs", Context.MODE_PRIVATE)
    val username = sharedPreferences.getString("username", "")
    val email = sharedPreferences.getString("email", "")
    val contactos = sharedPreferences.getStringSet("contactos", emptySet())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Nombre: $username", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Correo: $email", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar contactos si existen
            //if (!contactos.isNullOrEmpty()) {
            //    Text(text = "Contactos: ${contactos.joinToString(", ")}")
            //} else {
            //    Text(text = "No hay contactos")
            //}

            Button(
                onClick = onDisconnect,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text(text = "Desconectar", color = Color.White)
            }
        }
    }
}
