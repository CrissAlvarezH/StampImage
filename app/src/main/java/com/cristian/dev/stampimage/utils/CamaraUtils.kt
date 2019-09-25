package com.dev.cristian.alvarez.pensiones.utils

import android.content.Context
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import androidx.core.content.FileProvider
import android.os.Build
import android.provider.MediaStore
import android.content.Intent
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CamaraUtils (val contexto: Context) {
    private var ruta: String? = null

    @Throws(IOException::class)
    fun crearIntentCamera(): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val imgFile = crearImagenFile()

        if (imgFile != null) {
            val uri: Uri

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                uri = Uri.fromFile(imgFile)
            } else {
                uri = FileProvider.getUriForFile(
                    contexto,
                    "com.cristian.dev.stampimage.provider",
                    imgFile
                )
            }

            intent.putExtra("output", uri)
        }

        return intent
    }

    @Throws(IOException::class)
    private fun crearImagenFile(): File? {
        val fechaActual = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())

        val dirGuardado = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        dirGuardado.mkdirs()

        val imagen = File.createTempFile(fechaActual, ".jpg", dirGuardado)

        this.ruta = imagen.getAbsolutePath()

        return imagen
    }

    fun getRuta(): String? {
        return this.ruta
    }
}