    package com.example.producttimer.data.local

    import android.content.Context
    import android.net.Uri
    import java.io.File
    import java.io.FileOutputStream
    import java.util.UUID
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.withContext

    class GestorArchivosLocal(private val contexto: Context) {
        suspend fun guardarImagenLocalmente(uriImagen: Uri): Uri = withContext(Dispatchers.IO) {
            val directorioArchivos = contexto.filesDir
            val nombreArchivo = "${System.currentTimeMillis()}-${UUID.randomUUID()}.jpg"
            val archivoDestino = File(directorioArchivos, nombreArchivo)
            contexto.contentResolver.openInputStream(uriImagen)?.use { flujoEntrada ->
                FileOutputStream(archivoDestino).use { flujoSalida ->
                    flujoEntrada.copyTo(flujoSalida)
                }
            }
            return@withContext Uri.fromFile(archivoDestino)
        }

        fun eliminarImagen(uriLocal: String) {
            val archivo = File(Uri.parse(uriLocal).path ?: return)
            if (archivo.exists()) archivo.delete()
        }
    }