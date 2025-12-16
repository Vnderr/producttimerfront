package com.example.producttimer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenTestable(
    onRegisterClick: (String, String, String, (Boolean, String?) -> Unit) -> Unit,
    onRegisterSuccess: () -> Unit
) {

    var nombre by remember { mutableStateOf("") }
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
                title = {
                    Text("Crear cuenta", color = azul, modifier = Modifier.testTag("tituloRegistro"))
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .testTag("inputNombre")
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .testTag("inputEmail")
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .testTag("inputPassword")
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        mensaje = "Completa todos los campos"
                        return@Button
                    }

                    onRegisterClick(nombre, email, password) { success, error ->
                        if (success) onRegisterSuccess()
                        else mensaje = error ?: "Error desconocido"
                    }

                },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .testTag("btnRegistrar"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = azul,
                    contentColor = negro
                )
            ) {
                Text("Registrarse")
            }

            Spacer(Modifier.height(12.dp))

            mensaje?.let {
                Text(it, color = rojo, modifier = Modifier.testTag("mensajeError"))
            }
        }
    }
}

class RegisterScreenTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun muestraTituloRegistro() {
        rule.setContent {
            RegisterScreenTestable(
                onRegisterClick = { _, _, _, _ -> },
                onRegisterSuccess = {}
            )
        }

        rule.onNodeWithTag("tituloRegistro")
            .assertIsDisplayed()
            .assertTextContains("Crear cuenta")
    }

    @Test
    fun inputs_se_pueden_escribir() {
        rule.setContent {
            RegisterScreenTestable(
                onRegisterClick = { _, _, _, _ -> },
                onRegisterSuccess = {}
            )
        }

        rule.onNodeWithTag("inputNombre").performTextInput("Luis")
        rule.onNodeWithTag("inputEmail").performTextInput("luis@test.com")
        rule.onNodeWithTag("inputPassword").performTextInput("123456")

        rule.onNodeWithTag("inputNombre").assertTextContains("Luis")
        rule.onNodeWithTag("inputEmail").assertTextContains("luis@test.com")
        rule.onNodeWithTag("inputPassword").assertTextContains("123456")
    }

    @Test
    fun muestraErrorCuandoFaltanCampos() {
        rule.setContent {
            RegisterScreenTestable(
                onRegisterClick = { _, _, _, _ -> },
                onRegisterSuccess = {}
            )
        }

        rule.onNodeWithTag("btnRegistrar").performClick()

        rule.onNodeWithTag("mensajeError")
            .assertIsDisplayed()
            .assertTextContains("Completa todos los campos")
    }

    @Test
    fun registroExitosoLlamaCallback() {
        var registrado = false

        rule.setContent {
            RegisterScreenTestable(
                onRegisterClick = { _, _, _, callback ->
                    callback(true, null)
                },
                onRegisterSuccess = { registrado = true }
            )
        }

        rule.onNodeWithTag("inputNombre").performTextInput("Ana")
        rule.onNodeWithTag("inputEmail").performTextInput("ana@test.com")
        rule.onNodeWithTag("inputPassword").performTextInput("123456")

        rule.onNodeWithTag("btnRegistrar").performClick()

        assert(registrado)
    }

    @Test
    fun registroFallidoMuestraError() {
        rule.setContent {
            RegisterScreenTestable(
                onRegisterClick = { _, _, _, callback ->
                    callback(false, "El correo ya existe")
                },
                onRegisterSuccess = {}
            )
        }

        rule.onNodeWithTag("inputNombre").performTextInput("Pedro")
        rule.onNodeWithTag("inputEmail").performTextInput("pedro@test.com")
        rule.onNodeWithTag("inputPassword").performTextInput("abc123")

        rule.onNodeWithTag("btnRegistrar").performClick()

        rule.onNodeWithTag("mensajeError")
            .assertIsDisplayed()
            .assertTextContains("El correo ya existe")
    }
}