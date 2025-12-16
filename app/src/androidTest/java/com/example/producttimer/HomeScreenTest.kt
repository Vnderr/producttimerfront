package com.example.producttimer

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import coil.compose.rememberAsyncImagePainter
import org.junit.Rule
import org.junit.Test


data class HomeTestProducto(
    val producto_id: Int?,
    val nombre: String,
    val descripcion: String?,
    val fechaVencimiento: String?,
    val imagen: String?
)

@Composable
fun ItemRowTestable(
    producto: HomeTestProducto,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .testTag("itemRow_${producto.producto_id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (!producto.imagen.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(Uri.parse(producto.imagen)),
                contentDescription = "imagenProducto",
                modifier = Modifier
                    .size(70.dp)
                    .padding(end = 12.dp)
                    .testTag("imagen_${producto.producto_id}"),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onClick() }
                .testTag("itemClick_${producto.producto_id}")
        ) {
            Text(producto.nombre, modifier = Modifier.testTag("nombre_${producto.producto_id}"))
            Spacer(Modifier.height(4.dp))
            Text(
                producto.descripcion ?: "",
                modifier = Modifier.testTag("desc_${producto.producto_id}")
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "🗓️ Vence: ${producto.fechaVencimiento ?: ""}",
                modifier = Modifier.testTag("fecha_${producto.producto_id}")
            )
        }

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.testTag("delete_${producto.producto_id}")
        ) {
            Icon(Icons.Default.Delete, contentDescription = "eliminar")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTestable(
    productos: List<HomeTestProducto>,
    onItemClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    val azul = Color(0xFF1565C0)

    Column(modifier = Modifier.fillMaxSize()) {

        TopAppBar(
            title = {
                Text(
                    "Lista de productos",
                    color = azul,
                    modifier = Modifier.testTag("tituloHome")
                )
            }
        )

        LazyColumn(contentPadding = PaddingValues(8.dp)) {
            items(productos) { p ->
                ItemRowTestable(
                    producto = p,
                    onClick = { p.producto_id?.let { onItemClick(it) } },
                    onDeleteClick = { p.producto_id?.let { onDeleteClick(it) } }
                )
                Divider()
            }
        }
    }
}


class HomeScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val lista = listOf(
        HomeTestProducto(1, "Pan", "Pan fresco", "10/10/2025", "content://img1"),
        HomeTestProducto(2, "Queso", "Mantecoso", "12/12/2025", null)
    )


    @Test
    fun muestraTituloHome() {
        rule.setContent {
            HomeScreenTestable(lista, {}, {})
        }

        rule.onNodeWithTag("tituloHome")
            .assertIsDisplayed()
            .assertTextContains("Lista de productos")
    }


    @Test
    fun muestraListaDeProductos() {
        rule.setContent {
            HomeScreenTestable(lista, {}, {})
        }

        rule.onNodeWithTag("itemRow_1").assertIsDisplayed()
        rule.onNodeWithTag("itemRow_2").assertIsDisplayed()
    }

    @Test
    fun itemConImagenLaMuestra() {
        rule.setContent {
            HomeScreenTestable(lista, {}, {})
        }

        rule.onNodeWithTag("imagen_1").assertIsDisplayed()
    }

    @Test
    fun itemSinImagenNoMuestraImagen() {
        rule.setContent {
            HomeScreenTestable(lista, {}, {})
        }

        rule.onNodeWithTag("imagen_2").assertDoesNotExist()
    }

    @Test
    fun clickEnItemLlamaCallback() {
        var recibido = -1

        rule.setContent {
            HomeScreenTestable(
                productos = lista,
                onItemClick = { recibido = it },
                onDeleteClick = {}
            )
        }

        rule.onNodeWithTag("itemClick_1").performClick()

        assert(recibido == 1)
    }


    @Test
    fun clickEliminarLlamaCallback() {
        var eliminado = -1

        rule.setContent {
            HomeScreenTestable(
                productos = lista,
                onItemClick = {},
                onDeleteClick = { eliminado = it }
            )
        }

        rule.onNodeWithTag("delete_2").performClick()

        assert(eliminado == 2)
    }
}