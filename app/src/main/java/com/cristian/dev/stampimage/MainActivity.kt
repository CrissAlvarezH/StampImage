package com.cristian.dev.stampimage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.cristian.dev.stampimage.utils.ActivityUtils
import com.cristian.dev.stampimage.utils.ImageUtils
import com.dev.cristian.alvarez.pensiones.utils.CamaraUtils
import com.dev.cristian.alvarez.pensiones.utils.GaleriaUtils
import java.text.SimpleDateFormat
import java.util.*

private const val COD_PERMISOS = 423
private const val COD_CAMARA = 626
private const val COD_GALERIA = 190

class MainActivity : AppCompatActivity() {

    var imgOriginal: ImageView? = null
    var imgEstampada: ImageView? = null
    var edtEstampe: EditText? = null
    var camaraUtils: CamaraUtils? = null
    var galeriaUtils: GaleriaUtils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        camaraUtils = CamaraUtils(this)
        galeriaUtils = GaleriaUtils(this)

        imgOriginal = findViewById(R.id.img_original);
        imgEstampada = findViewById(R.id.img_estampada)
        edtEstampe = findViewById(R.id.edt_estampe);


    }

    fun clickTomarImagen(view: View) {

        if ( edtEstampe?.text.toString().trim().isEmpty() ) {
            edtEstampe?.error = "Digite el texto a estampar"
            edtEstampe?.requestFocus()
            return;
        }

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

                imgOriginal?.let {
                    Glide.with(this)
                        .load(rutaImg)
                        .into(it)
                }

                estamparImagen(rutaImg)
            }
        }
    }

    private fun estamparImagen(rutaImg: String) {
        val rutaEstampada = ImageUtils.stampImage(
            edtEstampe?.text.toString(),
            rutaImg,
            SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault()).format(Date())
        )

        if ( rutaEstampada != null ) {

            // TODO revisar porque la imagen sale volteada 90 grados
            imgEstampada?.let {
                Glide.with(this)
                    .load(rutaEstampada)
                    .into(it)
            }
        } else {
            Toast.makeText(this, "Estampe fallido", Toast.LENGTH_SHORT).show()
        }
    }
}
