package com.example.producttimer.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.producttimer.ui.components.Internet.ProtectorSinInternet
import com.example.producttimer.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }

    val azul = Color(0xFF1565C0)
    val negro = Color(0xFF000000)
    val gris = Color(0xFFF5F5F5)
    val rojo = Color(0xFFE53935)

    Scaffold(
        containerColor = gris,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Iniciar Sesion", color = azul) }
            )
        }
    ) { padding ->
        ProtectorSinInternet {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electronico") },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.iniciarSesion(email, password) { success, error ->
                            if (success) onLoginSuccess()
                            else mensaje = error
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.6f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = azul,
                        contentColor = negro
                    )
                ) {
                    Text("Ingresar")
                }

                Spacer(Modifier.height(8.dp))

                TextButton(onClick = onNavigateToRegister) {
                    Text("¿No tienes cuenta? Regístrate", color = negro)
                }

                Spacer(Modifier.height(12.dp))

                mensaje?.let {
                    Text(it, color = rojo)
                }
            }
        }
    }
}

