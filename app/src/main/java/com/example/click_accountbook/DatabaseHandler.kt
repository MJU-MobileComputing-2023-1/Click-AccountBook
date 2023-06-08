package com.example.click_accountbook

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
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
            Log.d("newReceipt", "Item Name: ${newReceipt}")


            // Insert the new Receipt into the database
            dbDao.insertReceipt(newReceipt)

            val items = receiptOcr.parseItems(ocrResult)

            val itemList = mutableListOf<Item>()
            for (itemInfo in items) {
                val newItem = Item(
                    id = itemInfo.id, // Set the item ID
                    receiptId = newReceiptId, // Set the receipt ID
                    itemName = itemInfo.itemName,
                    itemCode = itemInfo.itemCode,
                    itemCount = itemInfo.itemCount,
                    itemPrice = itemInfo.itemPrice,
                    itemUnitPrice = itemInfo.itemUnitPrice
                )
                itemList.add(newItem)
                Log.d("newItem", "Item Name: $newItem")
            }
            // Insert the new Items into the database
            insertItems(itemList)
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
            var maxId = items.maxByOrNull { it.id.toIntOrNull() ?: 0 }?.id ?: "0"
            val newId = maxId.toInt() + 1
            maxId = newId.toString()
            maxId
        }
    }

    fun insertImage(image: Image) {
        CoroutineScope(Dispatchers.IO).launch {
            dbDao.insertImage(image)
        }
    }

    private fun insertItems(items: List<Item>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (item in items) {
                val insertedId = dbDao.insertItem(item)
                Log.d("Database", "Inserted item with ID: $insertedId")
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