package com.example.safealert.presentation

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.safealert.presentation.network.EmailRequest
import com.example.safealert.presentation.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AlertActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlertScreen(onNavigateToSettings = {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            })
        }
    }
}

@Composable
fun AlertScreen(onNavigateToSettings: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón de Enviar Alerta
        Button(
            onClick = {
                val sharedPreferences = context.getSharedPreferences("SafeAlertPrefs", Context.MODE_PRIVATE)
                val email = sharedPreferences.getString("email", null)

                if (!email.isNullOrEmpty()) {
                    // Enviar solicitud POST
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = RetrofitInstance.api.sendAlert(EmailRequest(email))
                            withContext(Dispatchers.Main) {
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Alerta enviada", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error al enviar alerta", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Necesitas conectarte", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Icono de alerta",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Enviar alerta", color = Color.White)
        }




        Button(
            onClick = onNavigateToSettings,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text(text = "Ver datos", color = Color.White)
        }
    }
}
