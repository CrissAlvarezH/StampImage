package com.cristian.dev.stampimage.utils
import android.graphics.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.os.Environment


class ImageUtils {

    companion object {

        fun stampImage(textStamp: String, rutaImg: String, nombreImgDestino: String): String? {
            // Creamos un bitmap de la imagen con la ruta pasada por parametros
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmap = BitmapFactory.decodeFile(rutaImg, options)

            // Creamos otro bitmap que contendrá la imagen ya estampada
            val bitmapDestino = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

            val canvas = Canvas(bitmapDestino) // Cambas con le bitmap creado para detino

            // Creamos el pincel con el que pintaremos el texto en la imagen
            val textSize = 200f

            val paintText = Paint()
            paintText.style = Paint.Style.FILL
            paintText.textSize = textSize
            paintText.color = Color.WHITE

            // Creamos un pincel para pintar los bordes de otro color
            val paintBorde = Paint()
            paintBorde.style = Paint.Style.STROKE
            paintBorde.textSize = textSize
            paintBorde.color = Color.BLACK
            paintBorde.strokeWidth = 6f

            // Ponemos la imagen original en el canvas
            canvas.drawBitmap(bitmap, 0f, 0f, null)

            // Pintamos el texto con una altura tomada de referencia de las letra yY
            val height = paintText.measureText("yY")
            canvas.drawText(textStamp, 20f, height + 15f, paintBorde) // Borde
            canvas.drawText(textStamp, 20f, height + 15f, paintText) // Texto

            var rutaDestino: String?

            try {
                // Guardamos la imagen resultante
                val rutaCarpeta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath +
                                                "/StampImage";

                // Creamos la carpeta si no existe
                val carpetaDestino = File(rutaCarpeta)
                if ( !carpetaDestino.exists() ) carpetaDestino.mkdirs()

                rutaDestino = carpetaDestino.absolutePath + nombreImgDestino + ".jpg"

                bitmapDestino.compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    FileOutputStream( File(rutaDestino) )
                )

            } catch (e: FileNotFoundException) {
                rutaDestino = null;
                e.printStackTrace()
            }

            return rutaDestino;
        }

    }

}