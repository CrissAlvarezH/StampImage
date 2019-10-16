package com.cristian.dev.stampimage.utils
import android.graphics.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.media.ExifInterface
import android.os.Environment
import android.util.Log

private const val TAG = "UtilsImagen";

class ImageUtils {

    companion object {

        fun stampImageFromPath(textStamp: String, rutaImg: String, nombreImgDestino: String): String? {
            Log.v(TAG, "Orientación: ${ getOrientacion(rutaImg) }")

            // Creamos un bitmap de la imagen con la ruta pasada por parametros
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmap = BitmapFactory.decodeFile(rutaImg, options)

            return stampImageFromBitmap(textStamp, bitmap, nombreImgDestino);
        }

        fun stampImageFromBitmap(textStamp: String, bitmap: Bitmap, nombreImgDestino: String): String? {

            Log.v(TAG, "Original. Width: ${ bitmap.width }, Height: ${ bitmap.height }")

            // Creamos otro bitmap que contendrá la imagen ya estampada
            val bitmapDestino = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

            Log.v(TAG, "Destino. Width: ${ bitmap.width }, Height: ${ bitmap.height }")

            val canvas = Canvas(bitmapDestino) // Cambas con le bitmap creado para detino

            // Creamos el pincel con el que pintaremos el texto en la imagen
            val textSize = bitmap.width * 0.06f; // Porcentaje del ancho de la imagen

            Log.v(TAG, "TextSize: ${ textSize }")

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
                        File.separator + "StampImage" + File.separator;

                // Creamos la carpeta si no existe
                val carpetaDestino = File(rutaCarpeta)
                if ( !carpetaDestino.exists() ) carpetaDestino.mkdirs()

                rutaDestino = carpetaDestino.absolutePath + File.separator + nombreImgDestino + ".jpg"

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

        fun getOrientacion(rutaImg: String): Int {

            val imgFile = File(rutaImg)

            val exif = ExifInterface(imgFile.absolutePath)

            /* Grados de la clase ExifInterface
            Normal = 1
            Indefinido = 0
            180 = 3
            270 = 8
            90 = 6
             */
            return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        }

    }

}