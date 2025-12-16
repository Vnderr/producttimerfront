package com.example.producttimer

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenTestable(
    nombreUsuario: String,
    onLogout: () -> Unit
) {
    val azul = Color(0xFF1565C0)
    val negro = Color(0xFF000000)
    val rojo = Color(0xFFE53935)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil", color = azul, modifier = Modifier.testTag("tituloPerfil")) },
                actions = {
                    TextButton(
                        onClick = onLogout,
                        modifier = Modifier.testTag("btnLogout")
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
                contentDescription = "iconoPerfil",
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .testTag("iconoPerfil"),
                tint = azul
            )

            Spacer(Modifier.height(20.dp))

            Text("Hola", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = negro, modifier = Modifier.testTag("textoHola"))

            Spacer(Modifier.height(12.dp))

            Text(
                nombreUsuario,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = azul,
                modifier = Modifier.testTag("textoNombre")
            )
        }
    }
}


class ProfileScreenTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun muestraTituloPerfil() {
        rule.setContent {
            ProfileScreenTestable(nombreUsuario = "usuario@test.com", onLogout = {})
        }

        rule.onNodeWithTag("tituloPerfil")
            .assertIsDisplayed()
            .assertTextContains("Mi perfil")
    }


    //  icono
    @Test
    fun muestraIconoDePerfil() {
        rule.setContent {
            ProfileScreenTestable(nombreUsuario = "test@test.com", onLogout = {})
        }

        rule.onNodeWithTag("iconoPerfil").assertIsDisplayed()
    }


    //  el nombre del usuario
    @Test
    fun muestraNombreUsuario() {
        rule.setContent {
            ProfileScreenTestable(nombreUsuario = "correo@correo.com", onLogout = {})
        }

        rule.onNodeWithTag("textoNombre")
            .assertIsDisplayed()
            .assertTextContains("correo@correo.com")
    }


    // logout funciona
    @Test
    fun botonLogoutEjecutaCallback() {
        var logoutPresionado = false

        rule.setContent {
            ProfileScreenTestable(
                nombreUsuario = "correo@correo.com",
                onLogout = { logoutPresionado = true }
            )
        }

        rule.onNodeWithTag("btnLogout").performClick()

        assert(logoutPresionado)
    }
}