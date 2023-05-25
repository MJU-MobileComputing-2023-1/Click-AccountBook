package com.example.click_accountbook

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
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
                    // Get the image data as a ByteArray
                    GlobalScope.launch(Dispatchers.Main) {
                        val byteArray = withContext(Dispatchers.IO) {
                            getImageData(uri)
                        }

                        // Generate a new image ID
                        val newImageId = getNextImageId()

                        // Create the image object with the generated ID and the receipt ID
                        val image = Image(
                            id = newImageId,
                            format = "jpg",
                            data = byteArray,
                            timestamp = Date(),
                            receiptId = "" // Replace with the correct receipt ID
                        )

                        // Insert the image into the database
                        db.insertImage(image)

                        finish()
                    }
                }
            }
        }
    }

    private suspend fun getImageData(uri: Uri): ByteArray {
        return withContext(Dispatchers.IO) {
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = ByteArrayOutputStream()

            if (inputStream != null) {
                val buffer = ByteArray(1024)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                inputStream.close()
            }

            outputStream.toByteArray()
        }
    }

    private suspend fun getNextImageId(): String {
        return withContext(Dispatchers.IO) {
            val images = db.getImages()
            val maxId = images.maxByOrNull { it.id.toIntOrNull() ?: 0 }?.id ?: "-1"
            val newId = maxId.toInt() + 1
            newId.toString()
        }
    }
}
