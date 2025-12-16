package com.example.producttimer.ui.screen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.producttimer.ui.components.Internet.ProtectorSinInternet
import com.example.producttimer.ui.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var nombre by remember { mutableStateOf(user?.email ?: "invitado") }

    val azul = Color(0xFF1565C0)
    val negro = Color(0xFF000000)
    val rojo = Color(0xFFE53935)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil", color = azul) },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.cerrarSesion()
                            onLogout()
                        }
                    ) {
                        Text(
                            "Cerrar sesión",
                            color = rojo,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { padding ->
        ProtectorSinInternet {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape),
                    tint = azul
                )
                Spacer(Modifier.height(20.dp))
                Text("Hola", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = negro)
                Spacer(Modifier.height(12.dp))
                Text(nombre, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = azul)
            }
        }
    }
}