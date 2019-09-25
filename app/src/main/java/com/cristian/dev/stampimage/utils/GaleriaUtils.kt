package com.dev.cristian.alvarez.pensiones.utils

import android.content.Context
import android.content.Intent
import android.net.Uri


class GaleriaUtils (val contexto: Context) {
    private var imgUri: Uri? = null

    fun getIntentGaleria(): Intent {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        return Intent.createChooser(intent, "Selecciona una imagen")
    }

    fun getRuta(): String? {
        return FileUtils.getPath(contexto, this.imgUri)
    }

    fun setImgUri(imgUri: Uri?) {
        this.imgUri = imgUri
    }
}