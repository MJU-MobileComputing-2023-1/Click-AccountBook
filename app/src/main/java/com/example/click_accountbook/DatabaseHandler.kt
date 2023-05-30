package com.example.click_accountbook

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.*

class DatabaseHandler(context: Context) {
    private val db = DB.getDatabase(context)
    private val dbDao = db.DBDao()
    private val ocr = NaverClovaOCR()
    private val receiptOcr = ReceiptOCR()

    // Insert operations
    fun insertReceipt(image: Image, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = BitmapFactory.decodeFile(image.path)

            // Convert the Bitmap to File
            val imageFile = bitmapToFile(bitmap, context)

            val ocrResult = ocr.performOCR(imageFile)
            val receiptInfo = receiptOcr.parseReceipt(ocrResult)

            // Generate a new receipt ID
            val newReceiptId = getNextReceiptId()

            // Create a new Receipt object with the information from the ReceiptInfo object
            val newReceipt = Receipt(
                id = newReceiptId,
                imageId = image.id,
                storeName = receiptInfo.storeName,
                storeSubName = receiptInfo.storeSubName,
                storeBizNum = receiptInfo.bizNum,
                storeAddress = receiptInfo.address,
                storeTel = receiptInfo.tel,
                paymentDate = receiptInfo.date,
                paymentTime = receiptInfo.time,
                paymentCardCompany = receiptInfo.cardCompany,
                paymentCardNumber = receiptInfo.cardNumber,
                paymentConfirmNum = receiptInfo.confirmNum,
                totalPrice = receiptInfo.totalPrice,
                estimatedLanguage = ""
            )

            // Insert the new Receipt into the database
            dbDao.insertReceipt(newReceipt)

            // Parse and insert items
            val jsonObject = JSONObject(ocrResult)
            if(jsonObject.has("items")) {
                val itemsJsonArray = jsonObject.getJSONArray("items")
                val items = receiptOcr.parseItems(itemsJsonArray)
                for (item in items) {
                    // Generate a new item ID
                    val newItemId = getNextItemId()

                    // Create new Item object and insert it
                    val newItem = Item(
                        newItemId,
                        newReceiptId,
                        item.itemName,
                        item.itemCode,
                        item.itemCount,
                        item.itemPrice,
                        item.itemUnitPrice
                    )
                    insertItem(newItem)
                }
            } else {
                val items = receiptOcr.parseItems(null)
                for (item in items) {
                    // Generate a new item ID
                    val newItemId = getNextItemId()

                    // Create new Item object and insert it
                    val newItem = Item(
                        newItemId,
                        newReceiptId,
                        item.itemName,
                        item.itemCode,
                        item.itemCount,
                        item.itemPrice,
                        item.itemUnitPrice
                    )
                    insertItem(newItem)
                }
            }
        }
    }

    private fun bitmapToFile(bitmap: Bitmap, context: Context): File {
        // Get the internal storage directory
        val dir = context.filesDir

        // Create a new file in the directory with a unique name
        val file = File(dir, UUID.randomUUID().toString() + ".jpg")

        // Compress the bitmap and write it to the file
        val outputStream = FileOutputStream(file.absolutePath)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()

        return file
    }

    private suspend fun getNextReceiptId(): String {
        return withContext(Dispatchers.IO) {
            val receipts = dbDao.getAllReceipts()
            val maxId = receipts.maxByOrNull { it.id.toIntOrNull() ?: 0 }?.id ?: "-1"
            val newId = maxId.toInt() + 1
            newId.toString()
        }
    }

    fun updateImage(image: Image) {
        CoroutineScope(Dispatchers.IO).launch {
            dbDao.updateImage(image)
        }
    }

    private suspend fun getNextItemId(): String {
        return withContext(Dispatchers.IO) {
            val items = dbDao.getAllItems()
            val maxId = items.maxByOrNull { it.id.toIntOrNull() ?: 0 }?.id ?: "-1"
            val newId = maxId.toInt() + 1
            newId.toString()
        }
    }

    fun insertImage(image: Image) {
        CoroutineScope(Dispatchers.IO).launch {
            dbDao.insertImage(image)
        }
    }

    fun insertItem(item: Item) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                dbDao.insertItem(item)
            }
        }
    }

    // Fetch operations
    suspend fun getAllReceipts(): List<Receipt> {
        return dbDao.getAllReceipts()
    }

    suspend fun getImagesForReceipt(receiptId: String): List<Image> {
        return dbDao.getImagesForReceipt(receiptId)
    }

    suspend fun getItemsForReceipt(receiptId: String): List<Item> {
        return dbDao.getItemsForReceipt(receiptId)
    }

    suspend fun getImages(): List<Image> {
        return dbDao.getImages()
    }

    // Filter operations
    suspend fun getReceiptsByStore(storeName: String): List<Receipt> {
        return dbDao.getReceiptsByStore(storeName)
    }

    suspend fun getReceiptsByCardNumber(cardNumber: String): List<Receipt> {
        return dbDao.getReceiptsByCardNumber(cardNumber)
    }

    suspend fun getItemsByName(itemName: String): List<Item> {
        return dbDao.getItemsByName(itemName)
    }
}
