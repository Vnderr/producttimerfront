package com.example.producttimer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenTestable(
    onLoginClick: (String, String, (Boolean, String?) -> Unit) -> Unit,
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
                title = { Text("Iniciar Sesión", color = azul, modifier = Modifier.testTag("tituloLogin")) }
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
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
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
                    onLoginClick(email, password) { success, error ->
                        if (success) onLoginSuccess()
                        else mensaje = error
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .testTag("btnIngresar"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = azul,
                    contentColor = negro
                )
            ) {
                Text("Ingresar")
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.testTag("btnIrRegistro")
            ) {
                Text("¿No tienes cuenta? Regístrate", color = negro)
            }

            Spacer(Modifier.height(12.dp))

            mensaje?.let {
                Text(it, color = rojo, modifier = Modifier.testTag("mensajeError"))
            }
        }
    }
}


class LoginScreenTest {

    @get:Rule
    val rule = createComposeRule()


    //  título

    @Test
    fun muestraTituloLogin() {
        rule.setContent {
            LoginScreenTestable(
                onLoginClick = { _, _, _ -> },
                onLoginSuccess = {},
                onNavigateToRegister = {}
            )
        }

        rule.onNodeWithTag("tituloLogin")
            .assertIsDisplayed()
            .assertTextContains("Iniciar Sesión")
    }


    //  escriben texto
    @Test
    fun inputs_se_pueden_escribir() {
        rule.setContent {
            LoginScreenTestable(
                onLoginClick = { _, _, _ -> },
                onLoginSuccess = {},
                onNavigateToRegister = {}
            )
        }

        rule.onNodeWithTag("inputEmail").performTextInput("test@email.com")
        rule.onNodeWithTag("inputPassword").performTextInput("12345")

        rule.onNodeWithTag("inputEmail").assertTextContains("test@email.com")
        rule.onNodeWithTag("inputPassword").assertTextContains("12345")
    }


    @Test
    fun botonLoginLlamaSuccess() {
        var loginCorrecto = false

        rule.setContent {
            LoginScreenTestable(
                onLoginClick = { _, _, callback ->
                    callback(true, null)  // success
                },
                onLoginSuccess = { loginCorrecto = true },
                onNavigateToRegister = {}
            )
        }

        rule.onNodeWithTag("btnIngresar").performClick()

        assert(loginCorrecto)
    }


    @Test
    fun muestraErrorCuandoLoginFalla() {
        rule.setContent {
            LoginScreenTestable(
                onLoginClick = { _, _, callback ->
                    callback(false, "Credenciales incorrectas")
                },
                onLoginSuccess = {},
                onNavigateToRegister = {}
            )
        }

        rule.onNodeWithTag("btnIngresar").performClick()

        rule.onNodeWithTag("mensajeError")
            .assertIsDisplayed()
            .assertTextContains("Credenciales incorrectas")
    }


    @Test
    fun botonRegistroLlamaCallback() {
        var fuePresionado = false

        rule.setContent {
            LoginScreenTestable(
                onLoginClick = { _, _, _ -> },
                onLoginSuccess = {},
                onNavigateToRegister = { fuePresionado = true }
            )
        }

        rule.onNodeWithTag("btnIrRegistro").performClick()

        assert(fuePresionado)
    }
}