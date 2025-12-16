package com.example.producttimer.ui.screen

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.producttimer.data.local.GestorArchivosLocal
import com.example.producttimer.ui.components.Internet.ProtectorSinInternet
import com.example.producttimer.ui.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val gestorArchivos = remember { GestorArchivosLocal(context) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var nombreError by remember { mutableStateOf(false) }
    var descripcionError by remember { mutableStateOf(false) }
    var fechaError by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var usuarioId by remember { mutableStateOf<Int?>(null) }

    val azul = Color(0xFF1565C0)
    val nombreRegex = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")
    val fechaRegex = Regex("""\d{2}/\d{2}/\d{4}""")

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            imageUri = null
            Toast.makeText(context, "No se tomó la foto", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createImageUri(context)
            if (uri != null) {
                imageUri = uri
                cameraLauncher.launch(uri)
            } else {
                Toast.makeText(context, "Error creando archivo para la foto", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { imageUri = it } }

    LaunchedEffect(Unit) {
        viewModel.currentNeonUserIdFlow.collect { id ->
            usuarioId = id
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Agregar Producto") }) }
    ) { padding ->
        ProtectorSinInternet {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Nuevo producto",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = azul
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        nombreError = !nombreRegex.matches(it)
                    },
                    label = { Text("Nombre del producto") },
                    isError = nombreError,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                if (nombreError) Text("Solo se permiten letras y espacios", color = Color.Red, fontSize = 12.sp)

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = {
                        descripcion = it
                        descripcionError = it.isBlank()
                    },
                    label = { Text("Descripcion") },
                    isError = descripcionError,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                if (descripcionError) Text("La descripcion es obligatoria", color = Color.Red, fontSize = 12.sp)

                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha (dd/MM/yyyy)") },
                    isError = false,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                if (fechaError) Text("Formato inválido. Usa dd/MM/yyyy", color = Color.Red, fontSize = 12.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) { Text("Tomar Foto") }
                    Button(onClick = { galleryLauncher.launch("image/*") }) { Text("Elegir Imagen") }
                }

                imageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(model = uri),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier.size(150.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Button(
                    onClick = {
                        nombreError = !nombreRegex.matches(nombre)
                        descripcionError = descripcion.isBlank()
                        fechaError = !fechaRegex.matches(fecha)

                        if (nombreError || descripcionError || fechaError) {
                            Toast.makeText(context, "Corrige los errores antes de guardar", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (usuarioId == null) {
                            Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isSaving = true
                        scope.launch {
                            val imagenLocalUri: String? = imageUri?.let { uri ->
                                val uriGuardada = gestorArchivos.guardarImagenLocalmente(uri)
                                uriGuardada.toString()
                            }


                            viewModel.guardarNuevoProducto(
                                nombre,
                                descripcion,
                                fecha,
                                imagenLocalUri,
                                usuarioId!!
                            )
                            { success, error ->
                                isSaving = false
                                if (success) {
                                    Toast.makeText(context, "Producto guardado", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Error al guardar: $error", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.6f),
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = azul)
                ) {
                    if (isSaving) CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    ) else Text("Guardar Producto")
                }
            }
        }
    }
}

private fun createImageUri(context: Context): Uri? {
    val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(imagesDir, "IMG_${System.currentTimeMillis()}.jpg")
    return try {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}