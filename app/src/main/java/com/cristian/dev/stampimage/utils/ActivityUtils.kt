package com.cristian.dev.stampimage.utils

import android.Manifest
import android.Manifest.permission.*
import android.app.Activity
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import java.util.ArrayList

class ActivityUtils {

    companion object {

        fun validarPermisosFaltantes(contexto: Activity, cod: Int, pedir: Boolean): Boolean {
            val permisoCamara = checkSelfPermission(contexto, CAMERA) != PERMISSION_GRANTED
            val permisoWriteStorage = checkSelfPermission(contexto, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED
            val permisoReadStorage = checkSelfPermission(contexto, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED

            val listPermisos: MutableList<String> = mutableListOf();

            if (permisoCamara) listPermisos.add(CAMERA)
            if (permisoWriteStorage) listPermisos.add(WRITE_EXTERNAL_STORAGE)
            if (permisoReadStorage) listPermisos.add(READ_EXTERNAL_STORAGE)

            val estanTodosConsedidos = listPermisos.isEmpty();

            if ( pedir && !estanTodosConsedidos ) {
                ActivityCompat.requestPermissions(
                    contexto,
                    listPermisos.toTypedArray(),
                    cod
                )
            }

            return estanTodosConsedidos;
        }


    }

}