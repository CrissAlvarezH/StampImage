package com.cristian.dev.stampimage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.cristian.dev.stampimage.utils.ActivityUtils
import com.dev.cristian.alvarez.pensiones.utils.CamaraUtils
import com.dev.cristian.alvarez.pensiones.utils.GaleriaUtils

private const val COD_PERMISOS = 423
private const val COD_CAMARA = 626
private const val COD_GALERIA = 190

class MainActivity : AppCompatActivity() {

    var img: ImageView? = null
    var edtEstampe: EditText? = null
    var camaraUtils: CamaraUtils? = null
    var galeriaUtils: GaleriaUtils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        camaraUtils = CamaraUtils(this)
        galeriaUtils = GaleriaUtils(this)

        img = findViewById(R.id.img);
        edtEstampe = findViewById(R.id.edt_estampe);


    }

    fun clickTomarImagen(view: View) {

        if ( ActivityUtils.validarPermisosFaltantes(this, COD_PERMISOS, true) ) {
            pedirImg()
        }
    }

    private fun pedirImg() {
        if ( ActivityUtils.validarPermisosFaltantes(this, COD_PERMISOS, true) ) {

            val alerDialog = AlertDialog.Builder(this)
                .setTitle("¿De donde desea tomar la imagen?")
                .setPositiveButton("CAMARA") { dialogInterface, i ->

                    camaraUtils?.let {
                        val intent = it.crearIntentCamera()
                        startActivityForResult(intent, COD_CAMARA)
                    }
                }
                .setNegativeButton("GALERIA") { dialogInterface, i ->

                    galeriaUtils?.let {
                        val intent = it.getIntentGaleria()
                        startActivityForResult(intent, COD_GALERIA)
                    }

                }
                .create()

            if ( !this.isFinishing )
                alerDialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ( resultCode == RESULT_OK ) {
            var rutaImg: String? = null

            when (requestCode) {
                COD_CAMARA -> {
                    rutaImg = camaraUtils?.getRuta()
                }
                COD_GALERIA -> {
                    galeriaUtils?.setImgUri(data?.data)

                    rutaImg = galeriaUtils?.getRuta()
                }
            }

            rutaImg?.let {

                img?.let {
                    Glide.with(this)
                        .load(rutaImg)
                        .into(it)
                }
            }
        }
    }
}
