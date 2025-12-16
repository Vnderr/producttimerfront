package com.example.producttimer.ui.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import coil.compose.rememberAsyncImagePainter
import com.example.producttimer.data.model.Producto
import com.example.producttimer.ui.components.Internet.ProtectorSinInternet
import com.example.producttimer.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onItemClick: (Int) -> Unit // remoteId como String
) {
    val productoList by viewModel.listaProductos.collectAsState()
    val azul = Color(0xFF1565C0)
    ProtectorSinInternet {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = { Text("Lista de productos", color = azul) })

            LazyColumn(contentPadding = PaddingValues(8.dp)) {
                items(productoList) { producto ->
                    ItemRow(
                        producto = producto,
                        onClick = { producto.producto_id?.let { id -> onItemClick(id) } },
                        onDeleteClick = { producto.producto_id?.let { viewModel.eliminarProducto(it) } }
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun ItemRow(
    producto: Producto,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    ProtectorSinInternet {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!producto.imagen.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = Uri.parse(producto.imagen)),
                    contentDescription = "imagen del producto",
                    modifier = Modifier
                        .size(70.dp)
                        .padding(end = 12.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onClick() }
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = producto.descripcion ?: "",
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "🗓️ Vence: ${producto.fechaVencimiento ?: ""}",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "eliminar producto",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}