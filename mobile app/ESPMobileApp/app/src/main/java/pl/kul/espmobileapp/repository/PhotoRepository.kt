package pl.kul.espmobileapp.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.getString
import pl.kul.espmobileapp.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Integer.toString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhotoRepository(private val context: Context) {
    fun saveBitmapToGallery(bitmap: ImageBitmap?): Boolean {
        if (bitmap == null) {
            showToast(context.getString(R.string.no_photo_to_save))
            return false
        }

        val androidBitmap = bitmap.asAndroidBitmap()
        val dateFormat = SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault())
        val name = "ESP-IMG${dateFormat.format(Date())}.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/ESP Mobile APP")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        return try {
            imageUri?.let { uri ->
                resolver.openOutputStream(uri).use { outputStream ->
                    if (outputStream != null) {
                        androidBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                }

                showToast(context.getString(R.string.saved))
                true
            } ?: false
        } catch (e: Exception) {
            Log.e("PHOTO-REPO", "Błąd zapisu: ${e.message}")

            imageUri?.let { resolver.delete(it, null, null) }
            false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT)
            .show()
    }
}