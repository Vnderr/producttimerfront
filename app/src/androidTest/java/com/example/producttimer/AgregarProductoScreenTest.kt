package com.example.producttimer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoScreenTestable(
    onGuardar: (String, String, String) -> Unit,
    imageUri: String? = null
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }

    val azul = Color(0xFF1565C0)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Agregar Producto", modifier = Modifier.testTag("tituloPantalla")) })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                "Nuevo producto",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("tituloNuevoProducto")
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del producto") },
                modifier = Modifier.testTag("inputNombre")
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.testTag("inputDescripcion")
            )

            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha") },
                modifier = Modifier.testTag("inputFecha")
            )

            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "imagenPreview",
                    modifier = Modifier
                        .height(200.dp)
                        .testTag("imagenPreview"),
                    contentScale = ContentScale.Crop
                )
            }

            Button(
                onClick = { onGuardar(nombre, descripcion, fecha) },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .testTag("btnGuardar"),
                colors = ButtonDefaults.buttonColors(containerColor = azul)
            ) {
                Text("Guardar Producto")
            }
        }
    }
}


class AgregarProductoScreenTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun pantalla_muestraTituloPrincipal() {
        rule.setContent {
            AgregarProductoScreenTestable(onGuardar = { _, _, _ -> })
        }

        rule.onNodeWithTag("tituloPantalla").assertIsDisplayed()
        rule.onNodeWithText("Agregar Producto").assertIsDisplayed()
    }

    @Test
    fun pantalla_muestraTituloNuevoProducto() {
        rule.setContent {
            AgregarProductoScreenTestable(onGuardar = { _, _, _ -> })
        }

        rule.onNodeWithTag("tituloNuevoProducto")
            .assertIsDisplayed()
            .assertTextContains("Nuevo producto")
    }

    @Test
    fun inputs_se_pueden_escribir() {
        rule.setContent {
            AgregarProductoScreenTestable(onGuardar = { _, _, _ -> })
        }

        rule.onNodeWithTag("inputNombre").performTextInput("Pan")
        rule.onNodeWithTag("inputDescripcion").performTextInput("Pan artesanal")
        rule.onNodeWithTag("inputFecha").performTextInput("10/10/2025")

        rule.onNodeWithTag("inputNombre").assertTextContains("Pan")
        rule.onNodeWithTag("inputDescripcion").assertTextContains("Pan artesanal")
        rule.onNodeWithTag("inputFecha").assertTextContains("10/10/2025")
    }

    @Test
    fun muestraImagenCuandoHayUri() {
        rule.setContent {
            AgregarProductoScreenTestable(
                onGuardar = { _, _, _ -> },
                imageUri = "content://test/imagen.jpg"
            )
        }

        rule.onNodeWithTag("imagenPreview").assertIsDisplayed()
    }

    @Test
    fun botonGuardarLlamaCallbackConDatos() {
        var recibidoNombre = ""
        var recibidoDesc = ""
        var recibidoFecha = ""

        rule.setContent {
            AgregarProductoScreenTestable(
                onGuardar = { n, d, f ->
                    recibidoNombre = n
                    recibidoDesc = d
                    recibidoFecha = f
                }
            )
        }

        rule.onNodeWithTag("inputNombre").performTextInput("Cafe")
        rule.onNodeWithTag("inputDescripcion").performTextInput("Cafe molido")
        rule.onNodeWithTag("inputFecha").performTextInput("01/12/2025")

        rule.onNodeWithTag("btnGuardar").performClick()

        assert(recibidoNombre == "Cafe")
        assert(recibidoDesc == "Cafe molido")
        assert(recibidoFecha == "01/12/2025")
    }
}