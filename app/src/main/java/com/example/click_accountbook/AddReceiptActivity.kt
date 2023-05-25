// AddReceiptActivity.kt
package com.example.click_accountbook

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.*

class AddReceiptActivity : AppCompatActivity() {
    private val PICK_IMAGE = 1
    private lateinit var db: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_receipt_activity)

        db = DatabaseHandler(this)

        // Start the image picker
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data?.data

                if (uri != null) {
                    GlobalScope.launch(Dispatchers.Main) {
                        // Get the image data as a Bitmap
                        val bitmap = withContext(Dispatchers.IO) {
                            getBitmap(uri)
                        }

                        // Generate a new image ID
                        val newImageId = getNextImageId()

                        // Generate a new receipt ID
                        val newReceiptId = getNextReceiptId()

                        // Save the bitmap to internal storage and get the path
                        val imagePath = withContext(Dispatchers.IO) {
                            saveBitmap(bitmap, newImageId)
                        }

                        // Create the image object with the generated ID, the receipt ID, and the image path
                        val image = Image(
                            id = newImageId,
                            format = "jpg",
                            path = imagePath,
                            timestamp = Date(),
                            receiptId = newReceiptId // Use the new receipt ID
                        )

                        // Save the image metadata in the database
                        db.insertImage(image)
                    }
                }
            }
        }
    }

    private fun getNextImageId(): String {
        // You can generate an ID however you wish, but it should be unique
        // For simplicity, we'll just use the current timestamp here
        return System.currentTimeMillis().toString()
    }

    private fun getNextReceiptId(): String {
        // You can generate an ID however you wish, but it should be unique
        // For simplicity, we'll just use the current timestamp here
        return System.currentTimeMillis().toString()
    }

    private fun getBitmap(uri: Uri): Bitmap {
        val inputStream = contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }

    private fun saveBitmap(bitmap: Bitmap, imageId: String): String {
        // Get the internal storage directory
        val dir = getFilesDir()

        // Create a new file in the directory with the image ID as the name
        val file = File(dir, "$imageId.jpg")

        // Write the bitmap data to the file
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()

        // Return the path to the file
        return file.absolutePath
    }
}
