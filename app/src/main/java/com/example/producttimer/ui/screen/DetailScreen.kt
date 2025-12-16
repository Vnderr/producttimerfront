package com.example.producttimer.ui.screen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import android.net.Uri
import androidx.compose.foundation.Image
import coil.compose.rememberAsyncImagePainter
// Importamos la clase Producto final
import com.example.producttimer.data.model.Producto
import com.example.producttimer.ui.components.Internet.ProtectorSinInternet
import com.example.producttimer.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    productoId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var producto by remember { mutableStateOf<Producto?>(null) }

    LaunchedEffect(productoId) {
        val idInt = productoId.toIntOrNull()
        if (idInt != null) {
            producto = viewModel.obtenerProducto(idInt)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del producto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
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
                .padding(16.dp)
        ) {
            if (producto == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val p = producto!!

                Text(text = p.nombre, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))

                Text(text = "Vence: ${p.fechaVencimiento}", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp))

                Text(text = p.descripcion ?: "", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(24.dp))

                if (!p.imagen.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = Uri.parse(p.imagen)),
                        contentDescription = "Imagen del producto",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "Sin imagen disponible",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
        }
    }
}