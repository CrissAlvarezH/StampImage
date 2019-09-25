package com.dev.cristian.alvarez.pensiones.utils

import android.provider.MediaStore
import android.annotation.SuppressLint
import android.provider.DocumentsContract
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment.getExternalStorageDirectory
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.loader.content.CursorLoader


private const val TAG = "GalleryPhoto"

class FileUtils {

    companion object {

        fun getPath(context: Context, photoUri: Uri?): String? {
            var path: String? = null

            photoUri?.let {

                if (Build.VERSION.SDK_INT < 19) {
                    path = FileUtils.getRealPathFromURI_API11to18(context, it)
                } else {
                    path = FileUtils.getRealPathFromURI_API19(context, it)
                }
            }

            return path
        }

        @SuppressLint("NewApi")
        private fun getRealPathFromURI_API19(context: Context, photoUri: Uri): String? {
            var path: String? = ""
            if (DocumentsContract.isDocumentUri(context, photoUri)) {

                // ExternalStorageProvider
                if (isExternalStorageDocument(photoUri)) {
                    val docId = DocumentsContract.getDocumentId(photoUri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    if ("primary".equals(type, ignoreCase = true)) {
                        path = Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }
                } else if (isDownloadsDocument(photoUri)) {

                    val id = DocumentsContract.getDocumentId(photoUri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                    )

                    path = getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(photoUri)) {
                    val docId = DocumentsContract.getDocumentId(photoUri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    var contentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])

                    path = getDataColumn(context, contentUri, selection, selectionArgs)
                }// MediaProvider
                // DownloadsProvider
            } else if ("content".equals(photoUri.getScheme(), ignoreCase = true)) {
                path = getDataColumn(context, photoUri, null, null)
            } else if ("file".equals(photoUri.getScheme(), ignoreCase = true)) {
                path = photoUri.getPath()
            }// File
            // MediaStore (and general)
            return path
        }


        @SuppressLint("NewApi")
        private fun getRealPathFromURI_API11to18(context: Context, contentUri: Uri): String? {
            Log.d(TAG, "API 11 to 18")
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            var result: String? = null

            val cursorLoader = CursorLoader(
                context,
                contentUri, proj, null, null, null
            )
            val cursor = cursorLoader.loadInBackground()

            if (cursor != null) {
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor!!.moveToFirst()
                result = cursor!!.getString(column_index)
                cursor!!.close()
            }
            return result
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context The context.
         * @param uri The Uri to query.
         * @param selection (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        private fun getDataColumn(
            context: Context, uri: Uri, selection: String?,
            selectionArgs: Array<String>?
        ): String? {

            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)

            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null)
                if (cursor != null && cursor!!.moveToFirst()) {
                    val column_index = cursor!!.getColumnIndexOrThrow(column)
                    return cursor!!.getString(column_index)
                }
            } finally {
                if (cursor != null)
                    cursor!!.close()
            }
            return null
        }


        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        private fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.getAuthority()
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        private fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.getAuthority()
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        private fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.getAuthority()
        }
    }

}