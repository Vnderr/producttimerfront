package com.example.producttimer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import coil.compose.rememberAsyncImagePainter
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenTestable(
    producto: DetailTestProducto?,     // modelo de prueba
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del producto", modifier = Modifier.testTag("tituloDetalle")) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btnBack")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            if (producto == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("loadingBox"),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.testTag("loading"))
                }
            } else {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.testTag("nombreProducto"),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Vence: ${producto.fechaVencimiento}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("fechaProducto")
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = producto.descripcion ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("descripcionProducto")
                )

                Spacer(Modifier.height(24.dp))

                if (!producto.imagen.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = producto.imagen),
                        contentDescription = "Imagen del producto",
                        modifier = Modifier
                            .height(220.dp)
                            .fillMaxWidth()
                            .testTag("imagenProducto"),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        "Sin imagen disponible",
                        modifier = Modifier.testTag("sinImagen"),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


data class DetailTestProducto(
    val id: Int,
    val nombre: String,
    val descripcion: String?,
    val fechaVencimiento: String,
    val imagen: String?
)

class DetailScreenTest {

    @get:Rule
    val rule = createComposeRule()


    // ttulo

    @Test
    fun muestraTituloDetalle() {
        rule.setContent {
            DetailScreenTestable(producto = null, onBack = {})
        }

        rule.onNodeWithTag("tituloDetalle")
            .assertIsDisplayed()
            .assertTextContains("Detalle del producto")
    }


    // cargen progreso

    @Test
    fun muestraLoadingCuandoProductoEsNull() {
        rule.setContent {
            DetailScreenTestable(producto = null, onBack = {})
        }

        rule.onNodeWithTag("loading").assertIsDisplayed()
    }

    // datos del producto
    @Test
    fun muestraDatosCorrectosDelProducto() {
        val productoPrueba = DetailTestProducto(
            id = 1,
            nombre = "Yogurt",
            descripcion = "Lácteo natural",
            fechaVencimiento = "15/12/2025",
            imagen = "https://fakeurl.com/img.jpg"
        )

        rule.setContent {
            DetailScreenTestable(producto = productoPrueba, onBack = {})
        }

        rule.onNodeWithTag("nombreProducto").assertTextContains("Yogurt")
        rule.onNodeWithTag("descripcionProducto").assertTextContains("Lácteo natural")
        rule.onNodeWithTag("fechaProducto").assertTextContains("Vence: 15/12/2025") // Asegúrate de que el texto completo esté incluido
    }


    // imagen si existe
    @Test
    fun muestraImagenSiExiste() {
        val productoPrueba = DetailTestProducto(
            id = 1,
            nombre = "Queso",
            descripcion = "Queso mantecoso",
            fechaVencimiento = "10/10/2026",
            imagen = "https://fakeurl.com/imagen.jpg"
        )

        rule.setContent {
            DetailScreenTestable(producto = productoPrueba, onBack = {})
        }

        rule.onNodeWithTag("imagenProducto").assertIsDisplayed()
    }


    // mensaje si no hay imagen

    @Test
    fun muestraSinImagenCuandoNoHayImagen() {

        val productoPrueba = DetailTestProducto(
            id = 1,
            nombre = "Pan",
            descripcion = "Pan integral",
            fechaVencimiento = "01/01/2026",
            imagen = null
        )

        rule.setContent {
            DetailScreenTestable(producto = productoPrueba, onBack = {})
        }

        rule.onNodeWithTag("sinImagen")
            .assertIsDisplayed()
            .assertTextContains("Sin imagen disponible")
    }


    // volver ejecuta callback
    @Test
    fun botonBackEjecutaCallback() {
        var clicked = false

        rule.setContent {
            DetailScreenTestable(
                producto = null,
                onBack = { clicked = true }
            )
        }

        rule.onNodeWithTag("btnBack").performClick()

        assert(clicked)
    }
}